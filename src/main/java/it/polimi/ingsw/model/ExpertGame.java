package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.EmptyBasketException;

import java.util.*;

import static it.polimi.ingsw.Constants.NUM_EXISTING_PERSONALITY_CARDS;
import static it.polimi.ingsw.Constants.NUM_PLAYABLE_PERSONALITY_CARDS;

/**
 * This is a subclass of game
 * contains the information and the actions that can be performed in the expert game mode
 * Each player can play 3 Personality card
 * Each player can earn money to purchase Personality card
 */
public class ExpertGame extends Game {
    private ArrayList<Personality> personalities;
    private Personality activePersonality;
    private Player currentPersonalityPlayer;
    private int coins;
    private Color bannedColor;


    /**
     * Constructor creates an ExpertGame instance
     */
    public ExpertGame(int playersNumber){
        super(playersNumber);
        personalities = new ArrayList<>();
        coins=Constants.MAX_COINS_NUMBER - numOfPlayers;
    }

    /**
     * This method instantiates all the game elements: clouds,teachers,basket,islands, boards and personality cards.
     */
    @Override
    public void instantiateGameElements(ArrayList<String> playersNames) {
        super.instantiateGameElements(playersNames);
        extractPersonalityCards();
    }

    /**
     * Moves the selected student tile from the player's lobby to the color's table.
     * If the tile is placed in position 3,6 or 9 and there are coins left the player is given a coin.
     * @param player: the player making the move.
     * @param studentToMove: the chosen student who is being moved.
     */
    @Override
    protected void moveStudentsFromLobbyToTable(Player player, Color studentToMove){
        if(player.addToBoardTable(studentToMove)) {
            if(coins > 0){
                coins--;
                player.setCoins(player.getCoins()+1);
                ArrayList<Object> coinsChange = new ArrayList<>();
                coinsChange.add(player.getCoins());
                coinsChange.add(player.getNickname());
                coinsChange.add(coins);
                listeners.firePropertyChange("Coins", null, coinsChange);
            }
        }
    }

    /**
     * This Method recalculates the given player's number of students and possibly assigns him 1+
     * teachers ownership
     * It's different from the updateTeachersOwnership method because the player takes control
     * of the professors even if they have the same number of students in the Table as the current owner
     * @param nickname : reference of the player of whom we want to check teachers' ownership
     */
    public void updateTeachersOwnershipForCard2(String nickname){
        Player player = getPlayerByName(nickname);
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

        Island from = islands.get(islands.indexOf(currentMotherNatureIsland));
        Island dest = islands.get((islands.indexOf(from)+numSteps) % islands.size());
        currentMotherNatureIsland = dest;
        from.setMotherNature(false);
        dest.setMotherNature(true);
        listeners.firePropertyChange("MotherNature", from.getIslandIndex(), currentMotherNatureIsland.getIslandIndex());
        return true;
    }

    /**
     * Method isMoveMNLegalForCard4 checks  if the player identified by nickname can move Mother Nature of numSteps.
     * @param nickname : name given to the player.
     * @param numSteps : number of islands that the player identified with the playerId wants to move mother nature
     * @return true if the move is legal, false otherwise.
     */
    public boolean isMoveMNLegalForCard4(String nickname,int numSteps){
        int add = (getPlayerByName(nickname).equals(currentPersonalityPlayer) ? 2 : 0);
        int playerMaxSteps = currentTurnAssistantCards.get(nickname).getNumMoves() + add;
        return numSteps <= playerMaxSteps;
    }

    /**
     * Method that calculates influence of an island but doesn't consider the towers
     * @param island: instance of the island on which I want to calculate influence
     */
    public HashMap<String,String> calculateInfluenceForCard6(Island island){
        HashMap<String,Integer>  influences = calculateStudentsInfluences(island,players);
        HashMap<String, String> result= calculateIslandOwner(island,influences);
        mergeIslands(island);
        return result;
    }

    /**
     * Method that calculates influence of an island and adds 2 additional points
     * @param island: instance of the island on which I want to calculates influence
     */
    public HashMap<String,String> calculateInfluenceForCard8(Island island){
        HashMap<String,Integer>  influences = calculateStudentsInfluences(island,players);
        String towersOwnerName = getTowersOwnerName(island,players);
        int incrementedValue = influences.get(currentPlayer.getNickname()) + 2;
        influences.put(currentPlayer.getNickname(),incrementedValue);
        if(towersOwnerName != null) {
            int towerIncrement = influences.get(towersOwnerName) + island.getTowers().size();
            influences.put(towersOwnerName, towerIncrement);
        }
        HashMap<String, String> result= calculateIslandOwner(island,influences);
        mergeIslands(island);
        return result;
    }

    /**
     * Method that calculates influence on an island but doesn't consider the bannedColor students
     * @param island: instance of the island on which I want to calculate influence
     */
    public HashMap<String,String> calculateInfluenceForCard9(Island island){
        Color bannedColor = getBannedColor();
        HashMap<String,Integer> influences = calculateStudentsInfluences(island,players,bannedColor);
        String towersOwnerName = getTowersOwnerName(island,players);
        if(towersOwnerName != null){
            int towerIncrement = influences.get(towersOwnerName) + island.getTowers().size();
            influences.put(towersOwnerName,towerIncrement);
        }
        HashMap<String, String> result= calculateIslandOwner(island,influences);
        mergeIslands(island);
        return result;
    }

    /**
     * Method that calculates the influence of each player on an island but doesn't consider the bannedColor students
     * @param island: instance of the island on which I want to calculate influence
     * @param players: list of all the players of the game
     * @param bannedColor: Color that I want to exclude from the influence count
     * @return map that represents the influence of each players on that island.
     */
    protected HashMap<String,Integer> calculateStudentsInfluences(Island island,ArrayList<Player> players,Color bannedColor){
        int infl = 0;
        HashMap<String,Integer> influences = new HashMap<>();
        for(Player p: players){
            infl = 0;
            for(Color t:p.getBoard().getTeacherTable()){
                if(t != bannedColor)
                    infl+=island.getStudentsOfColor(t).size();
            }
            influences.put(p.getNickname(),infl);
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
    public boolean setActivePersonality(int cardId){
        if (activePersonality!=null){
            return false;
        }
        if (!areCardRequirementsSatisfied(cardId))
            return false;
        int playedCardIndex=-1;
        for (Personality card: personalities){
            if (card.getCharacterId()==cardId){
                playedCardIndex=personalities.indexOf(card);
            }
        }
        if (playedCardIndex==-1)
            return false;
        if(currentPlayer.getCoins()<personalities.get(playedCardIndex).getCost())
          return false;

        try {
            activePersonality=personalities.remove(playedCardIndex);
            boolean hasBeenUsed = activePersonality.isHasBeenUsed();
            int cardCost=activePersonality.getCost();
            coins += activePersonality.isHasBeenUsed()?cardCost: cardCost-1;
            currentPlayer.setCoins(currentPlayer.getCoins()-activePersonality.getCost());
            activePersonality.updateCost();
            currentPersonalityPlayer = currentPlayer;
            listeners.firePropertyChange("ActivePersonality", null, cardId);
            ArrayList<Object> coinsChange = new ArrayList<>();
            coinsChange.add(currentPlayer.getCoins());
            coinsChange.add(currentPlayer.getNickname());
            coinsChange.add(coins);
            listeners.firePropertyChange("Coins", null, coinsChange);
            return true;

        }
        catch (IndexOutOfBoundsException exception){
            return false;
        }
    }

    /**
     * Method areCardRequirementsSatisfied verifies that preconditions for the activation of a Personality are satisfied.
     * @param cardId Personality identification number.
     * @return true if the requirements are satisfied, false otherwise.
     */
    private boolean areCardRequirementsSatisfied(int cardId){
        int lobbySize = currentPlayer.getBoard().getLobby().size();
        if (cardId==10 && (currentPlayer.getBoard().isTableEmpty() || lobbySize==0))
            return false;
        if (cardId==7 && lobbySize==0)
            return false;
        else return true;
    }

    /**
     * Method that reset the active Personality card
     */
    public void resetActivePersonality(){
        if(activePersonality!=null){
            int cardID = activePersonality.getCharacterId();
            personalities.add(activePersonality);
            activePersonality = null;
            currentPersonalityPlayer = null;
            listeners.firePropertyChange("NoLongerActivePersonality", null, cardID);
        }
    }

    /**
     * Method executeCard1Effect implements Personality 1's Effect, therefore it moves a student tile from the card
     * to the chosen island.
     * @param chosenStudent: the index of the chosen student tile placed the card.
     * @param islandId: the ID that identifies the destination island.
     * @return true if the parameters are correct and the effect has been correctly applied, false otherwise.
     */
    public boolean executeCard1Effect(int chosenStudent,int islandId){
       Color student =((LobbyPersonality) activePersonality).getStudent(chosenStudent);
       Island destination = getIslandById(islandId);
       if (student==null || destination==null)
           return false;
       destination.addStudent(student);
       ((LobbyPersonality) activePersonality).removeStudent(student);
        replaceCardStudent();
        return true;

    }

    /**
     * Implements Personality 5's effect by adding a ban tile placeholder on the chosen island. The ban will
     * prevent influence's computation and tower placements on the banned island.
     * @param islandId: the banned island's unique ID.
     * @return true if parameters are correct and the effect has been correctly applied, false otherwise.
     */
    public boolean executeCard5Effect(int islandId){
        Island bannedIsland = getIslandById(islandId);
        if (bannedIsland==null || ((BanPersonality) activePersonality).getBans()<=0) return false;
        bannedIsland.putBan();
        ((BanPersonality) activePersonality).removeBan();
        return true;
    }

    /**
     * Method executeCard7Effect implements Personality 7's Effect, therefore it swaps up to 3 student tiles
     * from the card with as many student tiles in the active player's lobby.
     * @param cardStudentsIndexes: indexes of the student tiles on the card.
     * @param lobbyStudentsIndexes: indexed of the students tiles int the player's lobby.
     * @return true if the parameters are correct and the effect has been correctly applied, false otherwise.
     */
    public boolean executeCard7Effect(ArrayList<Integer> cardStudentsIndexes, ArrayList<Integer> lobbyStudentsIndexes){
        if (cardStudentsIndexes.size()>3 || lobbyStudentsIndexes.size()>3
                || cardStudentsIndexes.size()!=lobbyStudentsIndexes.size()
                || hasDuplicates(cardStudentsIndexes)
                || hasDuplicates(lobbyStudentsIndexes))
            return false;
        ArrayList<Color> fromCard = new ArrayList<>();
        ArrayList<Color> fromLobby = new ArrayList<>();
        for (Integer index: cardStudentsIndexes){
            if (isMoveStudentFromCardLegal((LobbyPersonality) activePersonality, index))
                fromCard.add(((LobbyPersonality) activePersonality).getStudent(index));
            else
                return false;
        }
        for (Integer index: lobbyStudentsIndexes){
            if (isMoveStudentFromLobbyLegal(index))
                fromLobby.add(currentPlayer.getBoard().getLobbyStudent(index));
            else
                return false;
        }
        for (int i = 0; i< cardStudentsIndexes.size();i++){
            if(currentPlayer.removeFromBoardLobby(fromLobby.get(i))){
                ((LobbyPersonality) activePersonality).removeStudent(fromCard.get(i));
                currentPlayer.addToBoardLobby(fromCard.get(i));
                ((LobbyPersonality) activePersonality).addStudent(fromLobby.get(i));
            }
            else return false;
        }
        return true;
    }

    /**
     * Checks if the selected student tile can be moved from the active personality card.
     * @param personality: the active personality card.
     * @param cardStudentId: index of the personality's student whose move is checked.
     * @return true if the move can be performed, false otherwise.
     */
    public boolean isMoveStudentFromCardLegal(LobbyPersonality personality, int cardStudentId){
        if (cardStudentId>=0 && cardStudentId <personality.getStudents().size())
            return true;
        else
            return false;
    }

    /**
     * Checks if the selected student tile can be moved from the current player's lobby.
     * @param lobbyStudentId: index of the lobby's student whose move is checked.
     * @return true if the move can be performed, false otherwise.
     */
    public boolean isMoveStudentFromLobbyLegal(int lobbyStudentId){
        if (lobbyStudentId>=0 && lobbyStudentId <currentPlayer.getBoard().getLobby().size())
            return true;
        else
            return false;
    }

    /**
     * Implements Personality 10's effect by swapping up to two students' tiles between the active player's lobby and table.
     * @param tableStudents: color of the tiles being moved from the board's table.
     * @param lobbyStudentsIndexes: indexes of the tiles being moved from the board's lobby.
     * @return true if the parameters are correct and the effect has been correctly applied, false otherwise.
     */
    public boolean executeCard10Effect(ArrayList<Color> tableStudents, ArrayList<Integer> lobbyStudentsIndexes){
        return currentPlayer.switchStudents(tableStudents,lobbyStudentsIndexes);
    }

    /**
     * Implements Personality's 11 effect by moving the chosen student tile from the personality card to
     * the active player's lobby.
     * @param cardStudentIndex: index of the student being moved from the card.
     * @return true if the move is legal and the effect has been correctly applied, false otherwise.
     */
    public boolean executeCard11Effect(int cardStudentIndex){
        Color toBeMoved = ((LobbyPersonality)activePersonality).getStudent(cardStudentIndex);
        if (toBeMoved==null || currentPlayer.getBoard().isTableFull(toBeMoved,0))
            return false;
        currentPlayer.addToBoardTable(toBeMoved);
        ((LobbyPersonality) activePersonality).removeStudent(toBeMoved);
        replaceCardStudent();
        return true;
    }

    /**
     * Implements Personality 12's effect, therefore moves up to three student tiles of the chosen color from
     * each player's table.
     * @param chosenColor: the color of the tiles which are being removed from the players' tables.
     * @return true if the effect has been correctly applied, false otherwise.
     */
    public boolean executeCard12Effect(Color chosenColor){
        int removed=0;
        for (Player player: players) {
            removed = Math.min(player.getBoard().getTableNumberOfStudents(chosenColor), 3);
            for (int i = 0; i<removed; i++){
                player.removeFromBoardTable(chosenColor);
                basket.putStudent(chosenColor);}
        }
        return true;
    }

    /**
     * Adds a student tile to the lobby personality's arraylist.
     * In the case of an empty basket lastRound's flag is set to true.
     */
    private void replaceCardStudent(){
        try{
            ((LobbyPersonality) activePersonality).addStudent(basket.pickStudent());
        }
        catch (EmptyBasketException e){
            boolean oldLastRound = lastRound;
            setLastRound(true);
            listeners.firePropertyChange("LastRound", oldLastRound, lastRound);
        }
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
     * Method resetIslandBan removes a ban tile from the selected island as a result of MotherNature's placement.
     * @param island island from which a ban tile is being removed.
     */
    public void resetIslandBan(Island island){
        island.removeBan();
        if (activePersonality instanceof BanPersonality)
            ((BanPersonality) activePersonality).addBan();
        else{
            for (Personality card: personalities)
                if (card instanceof BanPersonality)
                    ((BanPersonality) card).addBan();
        }
    }

    /**
     * Method that returns the number of coins in the game
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Sets the color that will not be considered in the influence's computation,
     * as is expected by Personality 9's effect.
     * @param color: the chosen color.
     */
    public void setBannedColor(Color color){
        bannedColor=color;
    }

    /**
     * Sets the color not considered in the influence's computation to null.
     */
    public void resetBannedColor(){
        bannedColor=null;
    }

    /**
     * Method getBannedColor returns the color chosen by the player who actived card
     * @return the color not considered in the influence's computation for this round.
     */
    public Color getBannedColor() {
        return bannedColor;
    }

    /**
     * Method setPropertyChangeListeners sets the listeners of ExpertGame's main attributes.
     * @param controller: controller instance listening to the game's changes.
     */
    @Override
    public void setPropertyChangeListeners(GameController controller) {
        super.setPropertyChangeListeners(controller);
        listeners.addPropertyChangeListener("ActivePersonality", controller);
        listeners.addPropertyChangeListener("Coins", controller);
        for (Personality personality: personalities)
            personality.setPropertyChangeListener(controller);
        listeners.addPropertyChangeListener("NoLongerActivePersonality", controller);
    }
}