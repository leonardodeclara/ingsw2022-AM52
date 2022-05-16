package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.EmptyBasketException;
import it.polimi.ingsw.exceptions.InvalidMoveException;

import java.util.*;

/**
 * This is a subclass of game
 * contains the informations and the actions that can be performed in the expert game mode
 * Each player can play 3 Personality card
 * Each player can earn money to purchase Personality card
 */
public class ExpertGame extends Game {
    private ArrayList<Personality> personalities;
    private static final int NUM_PLAYABLE_PERSONALITY_CARDS = 3;
    private static final int NUM_EXISTING_PERSONALITY_CARDS = 12;
    private Personality activePersonality; //c'è un solo personaggio attivo per round
    private int coins;
    private int bans;

    /**
     * Constructor creates an ExpertGame instance
     */
    public ExpertGame(int playersNumber){
        super(playersNumber);
        personalities = new ArrayList<>();
        coins=20;
        bans=4;
    }

    /**
     * This method instantiates all the game elements (clouds,teachers,basket,islands and boards)
     */
    @Override
    public void instantiateGameElements() {
        super.instantiateGameElements();
        //l'estrazione potrebbe essere resa indipendente da instantiateGameElements
        //extractPersonalityCards();
    }

    /**
     * This method is called when a player uses the Personality card whit CardID 2
     * The student can be moved to an island or to the table according to the parameters of the method
     * @param playerId: id given to the player, used as the index for the players arrayList
     * @param studentIndex: index that identifies the position of the student in the lobby
     * @param islandId: ID of the island where I want to place the student, if I want to move the student
     *                to the table I have to write -1
     * @return false if the move isn't legal, true otherwise
     */
    public boolean moveStudentFromLobbyForCard2(int playerId,int studentIndex,int islandId){
        Player player = players.get(playerId);
        if(!isMoveStudentFromLobbyLegal(player,studentIndex,islandId))
            return false;
        Color studentToMove = player.removeFromBoardLobby(studentIndex);
        if(islandId == Constants.ISLAND_ID_NOT_RECEIVED)
            player.addToBoardTable(studentToMove);
        else {
            Island islandDest = islands.get(islandId);
            islandDest.addStudent(studentToMove);
        }
        //aggiorna l'ownership dei teacher
        updateTeachersOwnershipForCard2(player);
        return true;
    }

    /**
     * This Method recalculates the given player's number of students and possibly assigns him 1+
     * teachers ownership
     * It's different from the updateTeachersOwnership method because the player takes control
     * of the professors even if they have the same number of students in the Table as the current owner
     * @param player : reference of the player of whom we want to check teachers' ownership
     */
    public void updateTeachersOwnershipForCard2(Player player){
        for(Color c : Color.values()) {
            Player owner = teachersOwners.get(c);
            if(owner!=null){
                if (player.getBoard().getTableNumberOfStudents(c) >= owner.getBoard().getTableNumberOfStudents(c)) {
                    owner.removeTeacherFromBoard(c);
                    player.addTeacherToBoard(c);
                    teachersOwners.put(c, player);
                }
            }
            else {
                if (player.getBoard().getTableNumberOfStudents(c)>0){
                    player.addTeacherToBoard(c);
                    teachers.remove(c);
                    teachersOwners.put(c, player);
                }
            }
        }
    }

    /**
     *Method moveMotherNature checks  if the player with Player ID can move Mother Nature of numSteps
     *  and if it's doable, moves Mother Nature from the current island to the new one
     *  It's different from the MoveMotherNature method because MotherNature can do 2 additional moves
     * @param nickname : name given to the player, TO DO
     * @param numSteps : number of islands that the player identified with the playerId wants to move mother nature
     */
    public boolean moveMotherNatureForCard4(String nickname,int numSteps){
        if(!isMoveMNLegalForCard4(nickname,numSteps))
            return false;

        Island from = islands.get(currentMotherNatureIsland.getIslandIndex());
        Island dest = islands.get((from.getIslandIndex() + numSteps) % islands.size());

        //manca il ricalcolo dell'influenza
        currentMotherNatureIsland=islands.get(dest.getIslandIndex());
        from.setMotherNature(false);
        dest.setMotherNature(true);
        listeners.firePropertyChange("MotherNature", from.getIslandIndex(), currentMotherNatureIsland.getIslandIndex());
        return true;
    }

    /**
     * Method isMoveMNLegalForCard4 checks  if the player with Player ID can move Mother Nature of numSteps
     * @param nickname : name given to the player, TO DO
     * @param numSteps : number of islands that the player identified with the playerId wants to move mother nature
     */
    public boolean isMoveMNLegalForCard4(String nickname,int numSteps){
        int playerMaxSteps = currentTurnAssistantCards.get(nickname).getNumMoves() + 2;
        return numSteps > playerMaxSteps? false : true;
    }

    /**
     * Method that calculates influence of an island but doesn't consider the towers
     * @param island: instance of the island on which I want to calculate influence
     */
    public HashMap<String,Integer> calculateInfluenceForCard6(Island island){
        ArrayList<Integer>  influences = calculateStudentsInfluences(island,players);
        return calculateIslandOwner(island,influences);
    }

    /**
     * Method that calculates influence of an island and adds 2 additional points
     * @param island: instance of the island on which I want to calculates influence
     */
    public HashMap<String,Integer> calculateInfluenceForCard8(Island island){
        ArrayList<Integer>  influences = calculateStudentsInfluences(island,players);
        int towersOwnerIndex = getTowersOwnerIndex(island,players);
        int incrementedValue = influences.get(currentPlayer.getPlayerId()) + 2;
        influences.add(currentPlayer.getPlayerId(),incrementedValue);
        if(towersOwnerIndex != -1) {
            int towerIncrement = influences.get(towersOwnerIndex) + island.getTowers().size();
            influences.set(towersOwnerIndex, towerIncrement);
        }
        return calculateIslandOwner(island,influences);
    }

    /**
     * Method that calculates influence on an island but doesn't consider the bannedColor students
     * @param island: instance of the island on which I want to calculate influence
     * @param bannedColor: Color that I want to exclude from the influence count
     */
    public HashMap<String,Integer> calculateInfluenceForCard9(Island island,Color bannedColor){
        ArrayList<Integer>  influences = calculateStudentsInfluences(island,players,bannedColor);
        int towersOwnerIndex = getTowersOwnerIndex(island,players);
        if(towersOwnerIndex != -1){
            int towerIncrement = influences.get(towersOwnerIndex) + island.getTowers().size();
            influences.set(towersOwnerIndex,towerIncrement);
        }
        return calculateIslandOwner(island,influences);
    }

    /**
     * Method that calculates the influence of each player on an island but doesn't consider the bannedColor students
     * @param island: instance of the island on which I want to calculate influence
     * @param players: list of all the players of the game
     * @param bannedColor: Color that I want to exclude from the influence count
     * @return ArrayList<Integer>: list of integer that represents the influence of each players on that island
     */
    protected ArrayList<Integer> calculateStudentsInfluences(Island island,ArrayList<Player> players,Color bannedColor){
        int infl = 0;
        ArrayList<Integer> influences = new ArrayList<>();
        for(Player p: players){
            infl = 0;
            for(Color t:p.getBoard().getTeacherTable()){
                if(t != bannedColor)
                    infl+=island.getStudentsOfColor(t).size();
            }
            influences.add(players.indexOf(p),infl);
        }
        return influences;
    }

    /**
     * Method that randomly extract 3 Personality card
     * This method differentiates the types of card drawn according to the ID
     */
    public void extractPersonalityCards() {
        ArrayList<Integer> extractedIndexes = new ArrayList<>();
        int randomIndex=0;
        for (int i = 0; i < NUM_PLAYABLE_PERSONALITY_CARDS; i++) {
            Random random = new Random();
            do{
                randomIndex = random.nextInt(NUM_EXISTING_PERSONALITY_CARDS)+1;
            } while(extractedIndexes.contains(randomIndex));
            extractedIndexes.add(randomIndex);
            if (randomIndex == 1 || randomIndex == 7 || randomIndex == 11) {
                LobbyPersonality extractedCard = new LobbyPersonality(randomIndex);
                int lobbyDimension = randomIndex==7 ? 6 : 4;
                for (int j = 0; j < lobbyDimension; j++){
                    //rivedere
                    try{
                        extractedCard.addStudent(basket.pickStudent());}
                    catch (EmptyBasketException e){
                        setLastRound(true);
                    }
                }
                personalities.add(extractedCard);
            } else if (randomIndex == 5) {
                BanPersonality extractedCard = new BanPersonality(randomIndex);
                personalities.add(extractedCard);
            } else {
                Personality extractedCard = new Personality(randomIndex);
                personalities.add(extractedCard);
            }
        }
    }

    /**
     * Method that sets the Personality card that is active in the round
     * @param cardId: ID of the Personality card that is active in the round
     */
    public void setActivePersonality(int cardId){
        if (activePersonality!=null){
            throw new InvalidMoveException(); //vedere se aggiungere messaggio
        }

        int playedCardIndex=-1;
        for (Personality card: personalities){
            if (card.getCharacterId()==cardId){
                playedCardIndex=personalities.indexOf(card);
            }
        }
        try {
            activePersonality=personalities.remove(playedCardIndex);
            activePersonality.setHasBeenUsed(true);
            activePersonality.updateCost();
            listeners.firePropertyChange("ActivePersonality", null, cardId);
        }
        catch (IndexOutOfBoundsException exception){
            throw new RuntimeException();
        };
        //bisogna creare una classe di eccezioni InvalidMoveException

        //rivedere come gestire questo caso
        //si potrebbe inserire all'interno di un blocco try questa chiamata + le modifiche alle monete ecc
    }

    /**
     * Method that reset the active Personality card
     */
    public void resetActivePersonality(){
        Personality oldCard = getActivePersonality();
        if (oldCard != null && !personalities.contains(oldCard))
            personalities.add(oldCard);
    }

    /**
     * Method that returns the list of all personality cards
     */
    public ArrayList<Personality> getPersonalities() {
        return personalities;
    }

    /**
     * Method that returns the Personality card instance of the active card in the round
     */
    public Personality getActivePersonality() {
        return activePersonality;
    }

    /**
     * Method that returns the number of coins in the game
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Method that returns the number of bans in the game
     */
    public int getBans() {
        return bans;
    }

    @Override
    public void setPropertyChangeListeners(GameController controller) {
        super.setPropertyChangeListeners(controller);
        listeners.addPropertyChangeListener("ActivePersonality", controller);
        listeners.addPropertyChangeListener("NotOwnedCoins", controller);
        listeners.addPropertyChangeListener("Bans", controller);
        listeners.addPropertyChangeListener("SelectedPersonality", controller);
    }
}
