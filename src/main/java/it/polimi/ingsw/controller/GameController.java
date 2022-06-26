package it.polimi.ingsw.controller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.function.*;


/**
 * Class GameController receives parameters relative to game moves from GameHandler and communicates with model's Game class in order to apply those moves.
 * Depending on the model's response GameController builds a new message that will be sent to one or more players.
 */
public class GameController implements PropertyChangeListener {
    private final Game game;
    private String currentPlayer;
    private final ArrayList<String> players;
    private final PropertyChangeSupport listener;
    private final UpdateMessageBuilder updateMessageBuilder;
    private final ArrayList<Tower> availableTowers;
    private final ArrayList<Integer> availableWizards;
    private final HashMap<String, Integer> playerToWizardMap;
    private BiPredicate<String,Integer> moveMotherNature;
    private Function<Island,HashMap<String,String>> calculateInfluence;
    private Consumer<String> updateTeachersOwnership;


    /**
     * Constructor GameController creates a new GameController instance.
     * @param isExpert game mode for the match, can be expert or base.
     * @param players nicknames of the players taking part in the game.
     */
    public GameController(boolean isExpert, ArrayList<String> players) {
        updateMessageBuilder = new UpdateMessageBuilder();
        listener = new PropertyChangeSupport(this);
        game = (isExpert) ? new ExpertGame(players.size()) : new Game(players.size());
        this.players = new ArrayList<>();
        this.players.addAll(players);
        availableWizards = new ArrayList<>();
        for (int i = 0; i< 4; i++){
            availableWizards.add(i);
        }
        availableTowers = new ArrayList<>();
        availableTowers.addAll(Arrays.asList(Tower.values()));
        playerToWizardMap = new HashMap<>();

        changeGameRulesForPersonalityCard(0);
        System.out.println("GameController: mi sono istanziato");
    }

    /**
     * Method handleGameInstantiation communicates with Game class in order to begin a new match.
     * It also sets the listening system between GameController and GameHandler.
     * @return a Message instance containing the first status of the game.
     */
    public Message handleGameInstantiation(){
        game.instantiateGameElements(players);
        game.setPropertyChangeListeners(this);
        System.out.println("GC: ho impostato correttamente i primi gameElements e ho settato i PropertyChange");
        return updateMessageBuilder.buildGameInstantiationMessage(game);
    }

    /**
     * Method updateWizardSelection handles the selection of a wizard deck by a player. If the selection is not legal the player is being sent a GameError message.
     * @param player name of the player choosing a wizard deck.
     * @param wizard wizard chosen by the player.
     * @return a message relative to the next game phase if the selection is legal, an ErrorMessage instance otherwise.
     */
    public Message updateWizardSelection(String player, Integer wizard){
        System.out.println("Maghi disponibili lato server: "+availableWizards);
        if (availableWizards.contains(wizard)){
            game.giveAssistantDeck(player, wizard);
            playerToWizardMap.put(player, wizard);
            availableWizards.remove(wizard);
            System.out.println("GameController: ho aggiornato i mazzi disponibili togliendo il deck " + wizard);
            return new AvailableTowerMessage(availableTowers);
        }
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    /**
     * Method updateWizardSelection handles the selection of a Tower by a player. If the selection is not legal the player is being sent a GameError message.
     * @param player name of the player choosing a Tower team.
     * @param tower Tower chosen by the player.
     * @return a message relative to the next game phase if the selection is legal, an ErrorMessage instance otherwise.
     */
    public Message updateTowerSelection(String player, Tower tower){
        System.out.println("Torri disponibili lato server: "+availableTowers);
        if (availableTowers.contains(tower)){
            game.getPlayerByName(player).setTeam(tower);
            availableTowers.remove(tower);
            System.out.println("GameController: ho aggiornato le torri disponibili togliendo " + tower.toString());
            return new ClientStateMessage(ClientState.WAIT_TURN);
        }
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    /**
     * Method updateCloudsStudents calls the Game's method responsible for the refilling of clouds' students list.
     */
    public void updateCloudsStudents(){
        game.refillClouds();
    }

    /**
     * Method updateAssistantCards handles the selection of an Assistant card by a player.
     * @param player name of the player choosing an Assistant card.
     * @param cardID priority of the chosen card.
     * @return a message relative to the next game phase if the selection is legal, an ErrorMessage instance otherwise.
     */
    public Message updateAssistantCards(String player, int cardID){
        if(game.playAssistantCard(player,cardID)==-1)
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
        else
            return new ClientStateMessage(ClientState.WAIT_TURN);

    }

    /**
     * Method moveStudentsFromLobby handles the game action carried out by a player of moving three student tiles from his board's lobby to his board's table.
     * @param player name of the player responsible for the game action.
     * @param studentIDs indexes of the students tile being moved from the board's lobby.
     * @param destIDs destinations of the selected student tiles.
     * @return a message relative to the next game phase if the selection is legal, an ErrorMessage instance otherwise.
     */
    public Message moveStudentsFromLobby(String player, ArrayList<Integer> studentIDs, ArrayList<Integer> destIDs){
        if(game.moveStudentsFromLobby(player,studentIDs,destIDs)){
            updateTeachersOwnership.accept(player);
            return new ClientStateMessage(ClientState.MOVE_MOTHER_NATURE);
        }
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    /**
     * Method moveMotherNature handles the game action carried out by a player of moving Mother Nature from its current island to a new destination island.
     * Furthermore, if the action triggers a end-game condition a message communicating the winner is being sent to all players.
     * @param player name of the player responsible for the game action.
     * @param steps number of steps by which Mother Nature is being moved.
     * @return a message relative to the next game phase if the selection is legal, an ErrorMessage instance otherwise.
     */
    public Message moveMotherNature(String player, int steps){
        if(moveMotherNature.test(player,steps)){
            if (!game.getCurrentMotherNatureIsland().isBanned()){
                calculateInfluence.apply(game.getCurrentMotherNatureIsland());
                if (game.checkGameOver())
                    return new EndGameMessage(game.getWinner()==null? Constants.TIE: game.getWinner().getNickname());
            }
            else {
                ((ExpertGame)game).resetIslandBan(game.getCurrentMotherNatureIsland());
            }
            if (game.isLastRound() && !game.areCloudsFull())
                return new ClientStateMessage(ClientState.END_TURN);
            else
                return new ClientStateMessage(ClientState.PICK_CLOUD);

        }
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    /**
     * Method refillLobby handles the game action carried out by a player of picking a Cloud tile whose students tile will be moved to his board's lobby.
     * @param player name of the player responsible for the game action.
     * @param cloudIndex identification for the chosen Cloud instance.
     * @return a message relative to the next game phase if the selection is legal, an ErrorMessage instance otherwise.
     */
    public Message refillLobby(String player, int cloudIndex){
        if(game.moveStudentsToLobby(player, cloudIndex))
            return new ClientStateMessage(ClientState.END_TURN);

        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    /**
     * Method playPersonalityCard handles the game action, carried out by a player, of activating a Personality card.
     * @param player name of the player responsible for the game action.
     * @param cardID identification for the chosen Personality card.
     * @param currentClientState state of the playing client.
     * @return a message relative to the game phase linked to the Personality effect if the selection is legal, an ErrorMessage instance otherwise.
     */
    public Message playPersonalityCard(String player, int cardID,ClientState currentClientState){
        if(((ExpertGame) game).setActivePersonality(cardID)) {
            changeGameRulesForPersonalityCard(cardID);
            Optional<ClientState> clientState = ClientState.valueOf(cardID);
            return clientState.map(ClientStateMessage::new).orElseGet(() -> new ClientStateMessage(currentClientState));
        }else{
            return new ErrorMessage(ErrorKind.ILLEGAL_MOVE);
        }
    }

    /**
     * Method resetPersonalityCard cancels the game effects applied as a result of a Personality activation.
     */
    public void resetPersonalityCard(){
        ((ExpertGame) game).resetActivePersonality();
        changeGameRulesForPersonalityCard(0);
        if (((ExpertGame)game).getBannedColor()!=null)
            ((ExpertGame)game).resetBannedColor();
    }

    /**
     * Method changeGameRulesForPersonalityCard changes a specific game rule as a result of a Personality activation or deactivation.
     * If the input is 0 it sets the rules to their default, otherwise it sets them accordingly to the effect of cardID.
     * @param cardID Personality identification used to change a specific game rule.
     */
    private void changeGameRulesForPersonalityCard(int cardID){
        switch(cardID){
            case 0:
                moveMotherNature = game::moveMotherNature;
                updateTeachersOwnership = game::updateTeachersOwnership;
                calculateInfluence = game::calculateInfluence;
                break;
            case 2:
                moveMotherNature = game::moveMotherNature;
                updateTeachersOwnership = ((ExpertGame)game)::updateTeachersOwnershipForCard2;
                calculateInfluence = game::calculateInfluence;
                break;
            case 4:
                moveMotherNature = ((ExpertGame)game)::moveMotherNatureForCard4;
                updateTeachersOwnership = game::updateTeachersOwnership;
                calculateInfluence = game::calculateInfluence;
                break;
            case 6:
                moveMotherNature = game::moveMotherNature;
                updateTeachersOwnership = game::updateTeachersOwnership;
                calculateInfluence = ((ExpertGame)game)::calculateInfluenceForCard6;
                break;
            case 8:
                moveMotherNature = game::moveMotherNature;
                updateTeachersOwnership = game::updateTeachersOwnership;
                calculateInfluence = ((ExpertGame)game)::calculateInfluenceForCard8;
                break;
            case 9:
                moveMotherNature = game::moveMotherNature;
                updateTeachersOwnership = game::updateTeachersOwnership;
                calculateInfluence = ((ExpertGame)game)::calculateInfluenceForCard9;
                break;
        }
    }

    public Message applyEffect1(int studentIndex, int islandId){
        if (((ExpertGame)game).executeCard1Effect(studentIndex, islandId))
            return null;
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    public Message applyEffect3(int islandId){
        if (game.getIslandById(islandId)!=null){
            calculateInfluence.apply(game.getIslandById(islandId));
            if (game.checkGameOver())
                return new EndGameMessage(game.getWinner()==null? Constants.TIE: game.getWinner().getNickname());
            else
                return null;
        }
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    public Message applyEffect5(int islandId){
        if (((ExpertGame) game).executeCard5Effect(islandId))
            return null;
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    public Message applyEffect7(ArrayList<Integer> cardStudentsIndexes, ArrayList<Integer> lobbyStudentsIndexes){
        if (((ExpertGame) game).executeCard7Effect(cardStudentsIndexes, lobbyStudentsIndexes))
            return null;
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    //void perché non possono esserci errore in teoria
    public void applyEffect9(Color banned){
        ((ExpertGame)game).setBannedColor(banned);
    }


    public Message applyEffect10(String player,ArrayList<Color> tableStudents, ArrayList<Integer> lobbyStudentsIndexes){
        if (((ExpertGame) game).executeCard10Effect(tableStudents, lobbyStudentsIndexes)){
            updateTeachersOwnership.accept(player);
            return null;}
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    public Message applyEffect11(String player, int cardStudentIndex){
        if (((ExpertGame) game).executeCard11Effect(cardStudentIndex)){
            updateTeachersOwnership.accept(player);
            return null;}

        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    public Message applyEffect12(Color chosenCard){
        if (((ExpertGame) game).executeCard12Effect(chosenCard))
            return null;
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }


    //return true se la partita è finita, false otherwise
    public Message closeCurrentRound(){
        //bisogna gestire la fine partita nel caso fosse il lastRound
        if (game.isLastRound()){
            game.calculateWinner();
            //in game manca tutto il calcolo del vincitore in caso la partita finisca al termine del lastRound
            System.out.println("Finita la partita");
            return new EndGameMessage(game.getWinner()==null? Constants.TIE: game.getWinner().getNickname());
        }
        game.resetCurrentTurnAssistantCards();
        return null;
    }



    public void setUpdateListener(GameHandler gameHandler){
        listener.addPropertyChangeListener("UpdateMessage", gameHandler);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        System.out.println("GC: è stato invocato il mio propertyChange");
        String eventName = event.getPropertyName();
        Message toSend = null;
        switch (eventName) {
            case "MotherNature":
                toSend = updateMessageBuilder.buildMotherNatureMessage(event);
                break;
            case "Merge":
                toSend = updateMessageBuilder.buildMergeMessage(event);
                break;
            case "LastRound":
                toSend = updateMessageBuilder.buildLastRoundMessage(event);
                break;
            case "CloudsRefill":
                toSend = updateMessageBuilder.buildCloudsRefillMessage(event);
                break;
            case "CurrentTurnAssistantCards":
                toSend = updateMessageBuilder.buildCurrentTurnAssistantCardsMessage(event);
                break;
            case "Deck": //non se è messaggio broadcast, forse in gameHandler va gestito in maniera diversa
                toSend = updateMessageBuilder.buildDeckUpdateMessage(event);
                break;
            case "IslandTowers":
                toSend = updateMessageBuilder.buildIslandTowersMessage(event);
                break;
            case "IslandStudents":
                toSend = updateMessageBuilder.buildIslandStudentsMessage(event);
                break;
            case "IslandBans":
                toSend = updateMessageBuilder.buildIslandBansMessage(event);
                break;
            case "PickedCloud":
                toSend = updateMessageBuilder.buildPickedCloudMessage(event);
                break;
            case "Board":
                toSend = updateMessageBuilder.buildBoardUpdateMessage(event);
                break;
            case "ActivePersonality":
                toSend = updateMessageBuilder.buildActivePersonalityMessage(event);
                break;
            case "NoLongerActivePersonality":
                toSend = updateMessageBuilder.buildNoLongerActivePersonalityMessage(event);
                break;
            case "Coins":
                toSend = updateMessageBuilder.buildCoinsUpdate(event);
                break;
            case "PersonalityUsage":
                toSend = updateMessageBuilder.buildPersonalityUsageMessage(event);
                break;
        }
        if (toSend!=null){
            System.out.println("GC: ho generato un messaggio di update valido: ora lo passo a GH");
            listener.firePropertyChange("UpdateMessage", null, toSend);
        }
    }

    public Game getGame() {
        return game;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
        game.setCurrentPlayer(currentPlayer);
    }

    private String getRandomPlayer(){
        Random rand = new Random();
        return players.get(rand.nextInt(players.size()));
    }

    public ArrayList<Integer> getAvailableWizards() {
        return availableWizards;
    }

    public ArrayList<Tower> getAvailableTowers() {
        return availableTowers;
    }

    public ArrayList<String> getActionPhaseTurnOrder(){
        return game.getActionPhasePlayerOrder();
    }

}
