package it.polimi.ingsw.controller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.ServerMessages.ClientStateMessage;
import it.polimi.ingsw.messages.ServerMessages.ErrorKind;
import it.polimi.ingsw.messages.ServerMessages.ErrorMessage;
import it.polimi.ingsw.messages.UpdateMessages.AvailableTowerMessage;
import it.polimi.ingsw.messages.UpdateMessages.EndGameMessage;
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
        return UpdateMessageBuilder.buildGameInstantiationMessage(game);
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

    /**
     * Method applyEffect1 manages the execution of Personality 1's effect by calling the ExpertGame class' method responsible for its enforcement: that is the
     * displacement of a student tile onto an island tile.
     * @param studentIndex index of the student which is being affected by the Personality effect.
     * @param islandId identification number for the destination island.
     * @return null if the effect has been correctly applied, an ErrorMessage instance otherwise.
     */
    public Message applyEffect1(int studentIndex, int islandId){
        if (((ExpertGame)game).executeCard1Effect(studentIndex, islandId))
            return null;
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    /**
     * Method applyEffect3 manages the execution of Personality 3's effect by calling the ExpertGame class' method responsible for its enforcement: that is the
     * computation of the players influence on a chosen island.
     * @param islandId identification number for the chosen island.
     * @return EndGameMessage if the effect has triggered an endgame condition, null if the execution had no side effects,
     * an ErrorMessage instance otherwise.
     */
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

    /**
     * Method applyEffect5 manages the execution of Personality 5's effect by calling the ExpertGame class' method responsible for its enforcement:
     * that is the ban of the selected island.
     * @param islandId identification number for the chosen island.
     * @return null if the effect has been correctly applied, an ErrorMessage instance otherwise.
     */
    public Message applyEffect5(int islandId){
        if (((ExpertGame) game).executeCard5Effect(islandId))
            return null;
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    /**
     * Method applyEffect7 manages the execution of Personality 7's effect by calling the ExpertGame class' method responsible for its enforcement:
     * that is the switch between at most three student tiles from the card with as many from the current player's board's lobby.
     * @param cardStudentsIndexes indexes of the chosen card students.
     * @param lobbyStudentsIndexes indexes of the chosen lobby students.
     * @return null if the effect has been correctly applied, an ErrorMessage instance otherwise.
     */
    public Message applyEffect7(ArrayList<Integer> cardStudentsIndexes, ArrayList<Integer> lobbyStudentsIndexes){
        if (((ExpertGame) game).executeCard7Effect(cardStudentsIndexes, lobbyStudentsIndexes))
            return null;
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    /**
     * Method applyEffect9 manages the execution of Personality 9's effect by calling the ExpertGame class' method responsible for its enforcement:
     * that is the ban of a selected color from the influence computation.
     * @param banned chosen color to be banned.
     */
    public void applyEffect9(Color banned){
        ((ExpertGame)game).setBannedColor(banned);
    }

    /**
     * Method applyEffect10 manages the execution of Personality 10's effect by calling the ExpertGame class' method responsible for its enforcement:
     * that is the switch between at most two student tiles from the current player's lobby with as many from his board's table.
     * @param player nickname of the player applying the Personality effect.
     * @param tableStudents colors of the chosen table students.
     * @param lobbyStudentsIndexes indexes of the chosen lobby students.
     * @return null if the effect has been correctly applied, an ErrorMessage instance otherwise.
     */
    public Message applyEffect10(String player,ArrayList<Color> tableStudents, ArrayList<Integer> lobbyStudentsIndexes){
        if (((ExpertGame) game).executeCard10Effect(tableStudents, lobbyStudentsIndexes)){
            updateTeachersOwnership.accept(player);
            return null;}
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    /**
     * Method applyEffect11 manages the execution of Personality 11's effect by calling the ExpertGame class' method responsible for its enforcement:
     * that is the displacement of a student tile from the card to the acting player's table.
     * @param player nickname of the player applying the effect.
     * @param cardStudentIndex  indexes of the chosen card student.
     * @return null if the effect has been correctly applied, an ErrorMessage instance otherwise.
     */
    public Message applyEffect11(String player, int cardStudentIndex){
        if (((ExpertGame) game).executeCard11Effect(cardStudentIndex)){
            updateTeachersOwnership.accept(player);
            return null;}

        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    /**
     * Method applyEffect12 manages the execution of Personality 12's effect by calling the ExpertGame class' method responsible for its enforcement:
     * that is the removal of at most three students of the chosen color from each player's table.
     * @param chosenCard color of the students that are being removed from the players' table.
     * @return null if the effect has been correctly applied, an ErrorMessage instance otherwise.
     */
    public Message applyEffect12(Color chosenCard){
        if (((ExpertGame) game).executeCard12Effect(chosenCard))
            return null;
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    /**
     * Method closeCurrentRound manages the end of a game round by checking if the one just completed was the final one.
     * In this case every player is sent a message with the result (the name of the winner if present, tie otherwise).
     * The current turn's assistant cards are also reset.
     * @return EndGameMessage if an endgame condition has been triggered, null otherwise.
     */
    public Message closeCurrentRound(){
        if (game.isLastRound()){
            game.calculateWinner();
            return new EndGameMessage(game.getWinner()==null? Constants.TIE: game.getWinner().getNickname());
        }
        game.resetCurrentTurnAssistantCards();
        return null;
    }

    /**
     * Method setUpdateListener sets the listener of GameController's update system.
     * @param gameHandler GameHandler instance listening to GameController.
     */
    public void setUpdateListener(GameHandler gameHandler){
        listener.addPropertyChangeListener("UpdateMessage", gameHandler);
    }

    /**
     * Method propertyChange receives an update notification from model classes and hands GameHandler an update message
     * carrying the information about the update.
     * @param event event of type PropertyChangeEvent.
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        System.out.println("GC: è stato invocato il mio propertyChange");
        String eventName = event.getPropertyName();
        Message toSend = null;
        switch (eventName) {
            case "MotherNature":
                toSend = UpdateMessageBuilder.buildMotherNatureMessage(event);
                break;
            case "Merge":
                toSend = UpdateMessageBuilder.buildMergeMessage(event);
                break;
            case "LastRound":
                toSend = UpdateMessageBuilder.buildLastRoundMessage(event);
                break;
            case "CloudsRefill":
                toSend = UpdateMessageBuilder.buildCloudsRefillMessage(event);
                break;
            case "CurrentTurnAssistantCards":
                toSend = UpdateMessageBuilder.buildCurrentTurnAssistantCardsMessage(event);
                break;
            case "Deck": //non se è messaggio broadcast, forse in gameHandler va gestito in maniera diversa
                toSend = UpdateMessageBuilder.buildDeckUpdateMessage(event);
                break;
            case "IslandTowers":
                toSend = UpdateMessageBuilder.buildIslandTowersMessage(event);
                break;
            case "IslandStudents":
                toSend = UpdateMessageBuilder.buildIslandStudentsMessage(event);
                break;
            case "IslandBans":
                toSend = UpdateMessageBuilder.buildIslandBansMessage(event);
                break;
            case "PickedCloud":
                toSend = UpdateMessageBuilder.buildPickedCloudMessage(event);
                break;
            case "Board":
                toSend = UpdateMessageBuilder.buildBoardUpdateMessage(event);
                break;
            case "ActivePersonality":
                toSend = UpdateMessageBuilder.buildActivePersonalityMessage(event);
                break;
            case "NoLongerActivePersonality":
                toSend = UpdateMessageBuilder.buildNoLongerActivePersonalityMessage(event);
                break;
            case "Coins":
                toSend = UpdateMessageBuilder.buildCoinsUpdateMessage(event);
                break;
            case "PersonalityUsage":
                toSend = UpdateMessageBuilder.buildPersonalityUsageMessage(event);
                break;
        }
        if (toSend!=null){
            System.out.println("GC: ho generato un messaggio di update valido: ora lo passo a GH");
            listener.firePropertyChange("UpdateMessage", null, toSend);
        }
    }

    /**
     * Method getGame returns the Game instance GameController is communicating with.
     * @return Game instance, the model entry point.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Method setCurrentPlayer updates the name of the currentPlayer in GameController and Game classes.
     * @param currentPlayer nickname of the current player in action.
     */
    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
        game.setCurrentPlayer(currentPlayer);
    }

    /**
     * Method getAvailableWizards returns the list of wizard decks currently available to players.
     * @return ArrayList containing wizards deck yet to be chosen.
     */
    public ArrayList<Integer> getAvailableWizards() {
        return availableWizards;
    }

    /**
     * Method getAvailableWizards returns the list of players ordered by their action priority.
     * @return ArrayList of ordered players.
     */
    public ArrayList<String> getActionPhaseTurnOrder(){
        return game.getActionPhasePlayerOrder();
    }
}
