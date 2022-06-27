package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.EmptyBasketException;

import java.beans.PropertyChangeSupport;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class contains the majority of the game's elements and logic.
 */

public class Game {
    protected Basket basket;
    protected ArrayList<Player> players;
    protected int numOfPlayers;
    protected Player currentPlayer;
    private Player winner;
    protected ArrayList<Island> islands;
    private ArrayList<Cloud> clouds;
    protected ArrayList<Color> teachers;
    private ArrayList<Integer> wizards;
    private ArrayList<Assistant> assistantDecks;
    protected HashMap<String, Assistant> currentTurnAssistantCards;
    protected Island currentMotherNatureIsland;
    protected boolean lastRound;
    protected HashMap<Color, Player> teachersOwners;
    protected PropertyChangeSupport listeners;


    /**
     * Constructor creates a Game instance
     */
    public Game(int playersNumber) {
        numOfPlayers = playersNumber;
        players = new ArrayList<>();
        islands = new ArrayList<>();
        clouds = new ArrayList<>();
        teachers = new ArrayList<>();
        wizards = new ArrayList<>();
        wizards.add(1);
        wizards.add(2);
        wizards.add(3);
        wizards.add(4);
        assistantDecks = new ArrayList<>();
        currentTurnAssistantCards = new HashMap<>();
        lastRound = false;
        winner = null;
        teachersOwners = new HashMap<>();
        listeners = new PropertyChangeSupport(this);

        for (int i = 0; i < numOfPlayers; i++) {
            clouds.add(new Cloud(i));
        }
        for (int i = 0; i < Constants.MAX_NUM_ISLANDS; i++) {
            islands.add(new Island(i));
        }
    }

    /**
     * This method add new players to the game.
     *
     * @param playersNames: names of the players which have been admitted to the game.
     */
    public void addPlayers(ArrayList<String> playersNames) {
        for (String playerName: playersNames)
            if (players.size() < 3) {
                Player player = new Player(players.size(), playerName,(this instanceof ExpertGame));
                player.getBoard().setTowers(numOfPlayers == 2 ? 8 : 6);
                players.add(player);
            } else
                throw new RuntimeException("Superato limite di giocatori");
    }

    /**
     * This method instantiates all the game elements (clouds,teachers,basket,islands and boards).
     */
    public void instantiateGameElements(ArrayList<String> playersNames) {
        teachers.addAll(Arrays.asList(Color.values()));
        for (int i = 0; i < Color.values().length; i++) {
            teachersOwners.put(Color.values()[i], null);
        }
        for (int numWizard = 0; numWizard < 4; numWizard++) {
            int numMoves;
            for (int priority = 1; priority < 11; priority++) {
                numMoves = priority % 2 == 0 ? priority / 2 : priority / 2 + 1;
                assistantDecks.add(new Assistant(numMoves, priority, numWizard));
            }
        }
        Random indexGenerator = new Random();
        int initialMotherNature = indexGenerator.nextInt(Constants.MAX_NUM_ISLANDS);
        islands.get(initialMotherNature).setMotherNature(true);
        currentMotherNatureIsland = islands.get(initialMotherNature);
        basket = new Basket(new int[]{2, 2, 2, 2, 2});
        fillIslands();
        basket = new Basket(new int[]{24, 24, 24, 24, 24});
        addPlayers(playersNames);
        initiatePlayersLobbies();
    }

    /**
     * Method initiatePlayersLobbies calculates the number of students to put in the players' lobby
     * and makes the move.
     *
     */
    public void initiatePlayersLobbies() {
        int studentLimit = numOfPlayers == 2 ? 7 : 9;
        for (Player player: players)
            for (int i = 0; i< studentLimit; i++)
                player.addToBoardLobby(basket.pickStudent());
    }

    /**
     * Method responsible for the filling of the islands' students spots at the beginning of the game.
     * Each student tile is randomly picked by the basket, which in this case only contains 2 tiles for each color.
     * Nor mother nature's island neither the opposite one will receive any student.
     */
    public void fillIslands() {
        for (Island island : islands) {
            if (island.getIslandIndex() != currentMotherNatureIsland.getIslandIndex()
                    && !(island.getIslandIndex() == (6 + currentMotherNatureIsland.getIslandIndex()) % 12)) {
                island.addStudent(basket.pickStudent());
            }
        }
    }

    /**
     * Method giveAssistantDeck assigns the deck of 10 assistant cards to the given player.
     *
     * @param playerName: name of the player, used as the index for the players arrayList
     * @param deckId : id given to the deck, the player uses it communicate which deck he wants
     */
    public void giveAssistantDeck(String playerName, int deckId) {
        ArrayList<Assistant> assignedDeck = new ArrayList<>();
        wizards.remove((Integer) deckId); //firePropertyChange
        for (Assistant assistant : assistantDecks) {
            if (assistant.getWizard() == deckId) {
                assignedDeck.add(assistant);
            }
        }
        assistantDecks.removeAll(assignedDeck);
        Player player = getPlayerByName(playerName);
        player.setDeck(assignedDeck);
    }

    /**
     * Method playAssistantCard removes the card to the player's deck and adds it to the current turn played assistant cards
     * in order to calculate the playing order for the action phase.
     *
     * @param nickname : name of the player trying to play the card.
     * @param cardPriority: priority of the card in play.
     * @return the priority of the played card if the selection is legal, -1 otherwise.
     */
    public int playAssistantCard(String nickname, int cardPriority) {
        Player choosingPlayer = getPlayerByName(nickname);
        Assistant chosenCard = choosingPlayer.getCardByPriority(cardPriority);
        if (chosenCard == null || !isCardPlayable(chosenCard, choosingPlayer.getDeck())) return -1;
        currentTurnAssistantCards.put(nickname, chosenCard);
        listeners.firePropertyChange("CurrentTurnAssistantCards", null, currentTurnAssistantCards);
        getPlayerByName(nickname).removeAssistantCard(cardPriority);
        if (choosingPlayer.getDeck().size()==0){
            boolean oldLastRound = lastRound;
            setLastRound(true);
            listeners.firePropertyChange("LastRound", oldLastRound, lastRound);
        }
        return cardPriority;
    }

    /**
     * Method getActionPhasePlayerOrder returns the players' order for action according to the chosen Assistant cards.
     * @return the list of players ordered according to their action priority.
     */
    public ArrayList<String> getActionPhasePlayerOrder() {
        ArrayList<Assistant> assistants = new ArrayList<>(currentTurnAssistantCards.values());
        assistants.sort(Comparator.comparingInt(Assistant::getPriority));
        Collections.reverse(assistants);
        ArrayList<String> nicknames = new ArrayList<>();

        for (Assistant assistant : assistants) {
            for (Map.Entry<String, Assistant> entry : currentTurnAssistantCards.entrySet()) {
                if (Objects.equals(assistant, entry.getValue())) {
                    nicknames.add(entry.getKey());
                }
            }
        }
        return nicknames;
    }

    /**
     * Method isCardPlayable checks if a card can be played or not in the current turn.
     * If it's the only card the player has or every other card in the player deck has already been played by someone else,
     * or the played card is different from the ones played by the other players
     * then return true
     * otherwise, if the played card has already been played but the player has in its deck other cards, different from the ones already played
     * return false
     *
     * @param playedCard : the card played by the player
     * @param playerDeck : the deck of the player
     */
    protected boolean isCardPlayable(Assistant playedCard, ArrayList<Assistant> playerDeck) {
        List<Integer> playerPriorities = playerDeck.stream().map(Assistant::getPriority).collect(Collectors.toList());
        List<Integer> currentTurnPriorities = currentTurnAssistantCards.values().stream().map(Assistant::getPriority).collect(Collectors.toList());
        if (currentTurnPriorities.containsAll(playerPriorities) || playerDeck.size() == 1)
            return true;
        else if (currentTurnPriorities.contains(playedCard.getPriority()))
            return false;
        else
            return true;
    }

    /**
     * Method moveMotherNature checks  if the player with Player ID can move Mother Nature of numSteps
     * and if it's doable, moves Mother Nature from the current island to the new one.
     *
     * @param nickname : name of the player making the move.
     * @param numSteps : number of islands that the player identified with the playerId wants to move mother nature
     */
    public boolean moveMotherNature(String nickname, int numSteps) {
        if (!isMoveMNLegal(nickname, numSteps))
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
     * Method moveStudentFromLobby checks  if the player with Player ID can move the student in his lobby to an island or
     * the corresponding table. If it's doable the student is moved from the lobby to the table/island. If the island id is -1
     * (ISLAND_ID_NOT_RECEIVED) the student is moved to the table. Otherwise, the student is moved to the island
     *
     * @param nickname     : id given to the player, used as the index for the players ArrayList
     * @param studentIDs : number of islands that the player identified with the playerId wants to move mother nature
     * @param islandIDs     : the id of the destination island (or -1 if no island is specified)
     */
    public boolean moveStudentsFromLobby(String nickname, ArrayList<Integer> studentIDs, ArrayList<Integer> islandIDs) {
        ArrayList<Color> studentsToMove = new ArrayList<>();
        Player player = getPlayerByName(nickname);
        int islandIndexCounter = 0;
        int maxStudentsToMove = numOfPlayers==2?
                Constants.MOVE_FROM_LOBBY_STUDENTS_NUMBER_FOR_2_PLAYERS:
                Constants.MOVE_FROM_LOBBY_STUDENTS_NUMBER_FOR_3_PLAYERS;

        if(studentIDs.size() != maxStudentsToMove || islandIDs.size()!=maxStudentsToMove || hasDuplicates(studentIDs))
            return false;

        for (int i = 0; i < studentIDs.size(); i++) { //controlliamo se la mossa è legit per ogni studente e per ogni destinazione
            if (!isMoveStudentFromLobbyLegal(player, studentIDs.get(i), islandIDs.get(i), studentsToMove))
                return false;
            studentsToMove.add(player.getBoard().getLobbyStudent(studentIDs.get(i)));
        }

        for (Color studentToMove : studentsToMove) {
            if(player.removeFromBoardLobby(studentToMove)){
                if (islandIDs.get(islandIndexCounter) == Constants.ISLAND_ID_NOT_RECEIVED)
                    moveStudentsFromLobbyToTable(player,studentToMove);
                else {
                    Island islandDest = getIslandById(islandIDs.get(islandIndexCounter));
                    islandDest.addStudent(studentToMove);
                }
                islandIndexCounter++;
            }
            else return false;
        }
        return true;
    }

    /**
     * Method moveStudentsFromLobbyToTable moves the selected student tile to the selected Player's board table.
     * @param player: Player instance of the player who is adding a student tile to his board table.
     * @param studentToMove: student tile being moved to the board's table.
     */
    protected void moveStudentsFromLobbyToTable(Player player, Color studentToMove){
        player.addToBoardTable(studentToMove);
    }

    /**
     * Method updateTeachersOwnership recalculates the given player's number of students and possibly assigns him 1+
     *  teachers ownership
     * @param nickname : name of the player of whom we want to check teachers' ownership
     *
     */
    public void updateTeachersOwnership(String nickname){
        Player player = getPlayerByName(nickname);
        for(Color c : Color.values()) {
            Player owner = teachersOwners.get(c);
            if(owner != null){
                if (player.getBoard().getTableNumberOfStudents(c) > owner.getBoard().getTableNumberOfStudents(c)) {
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
     * Method moveStudentsToLobby moves all the students on a given cloud, to the given player lobby.
     *
     * @param playerName : name of to the player making the movement.
     * @param cloudId : id given to the cloud to empty.
     */
    public boolean moveStudentsToLobby(String playerName,int cloudId){
        Player player = getPlayerByName(playerName);
        if(!isMoveStudentsToLobbyLegal(player,cloudId))
            return false;
        Cloud cloud = clouds.get(cloudId);
        ArrayList<Color> studentsToMove = cloud.emptyStudents(); //firePropertyChange
        for(Color student : studentsToMove)
            player.addToBoardLobby(student); //firePropertyChange
        return true;
    }

    /**
     * Utility method used to check whether it's the game's last round or not.
     * @return true if the game will be over at the end of the current round, false if not.
     */
    public boolean isLastRound() {
        return lastRound;
    }

    /**
     * Method isMoveMNLegal checks  if the player with Player ID can move Mother Nature of numSteps
     * @param nickname : name given to the player, .. TO DO
     * @param numSteps : number of islands that the player identified with the playerId wants to move mother nature
     */
    public boolean isMoveMNLegal(String nickname,int numSteps){
        int playerMaxSteps = currentTurnAssistantCards.get(nickname).getNumMoves();
        return numSteps <= playerMaxSteps && numSteps > 0;
    }

    /**
     * Method isMoveStudentFromLobbyLegal checks if the player can move the student in his lobby to an island or
     *  the corresponding table.
     * @param player : player making the move.
     * @param studentIndex : index of the student in the board's lobby.
     * @param islandId : id of the destination island (or -1 if no island is specified)
     * @param studentsToBeAdded: students whose move has already been validated.
     */
    public boolean isMoveStudentFromLobbyLegal(Player player,int studentIndex,int islandId, ArrayList<Color> studentsToBeAdded){
        if (studentIndex >= player.getBoard().getLobby().size() || studentIndex<0)
            return false;
        Color studentToMove = player.getBoard().getLobbyStudent(studentIndex);
        if(studentToMove != null){
            if(islandId == Constants.ISLAND_ID_NOT_RECEIVED){
                int toBeAdded = 0;
                if (studentsToBeAdded!=null)
                    for (Color color: studentsToBeAdded)
                        if(color.equals(studentToMove))
                            toBeAdded++;
                if(!player.getBoard().isTableFull(studentToMove, toBeAdded))
                    return true;
            }
            else if(getIslandById(islandId)!=null)
                    return true;
        }
        return false;
    }

    /**
     * Method isMoveStudentsToLobbyLegal checks if the given cloudId identifies a cloud which contains students
     *
     * @param player: reference of the player who wants to perform the
     * @param cloudId : id given to the cloud to empty
     * @return true if the students can be moved from the cloud tile to the player's board's lobby.
     */
    public boolean isMoveStudentsToLobbyLegal(Player player,int cloudId){
        if (cloudId >= 0 && cloudId <= clouds.size()-1)
            return !clouds.get(cloudId).getStudents().isEmpty();
        return false;
    }

    /**
     * Method refillClouds refills all the clouds in the clouds ArrayList.
     * It's responsible for checking whether a picked student is null, in which case it sets lastRound flag to true.
     */
    public void refillClouds(){
        int numOfPicks = numOfPlayers+1;
        ArrayList<Color> picks = new ArrayList<>();
        Color pick;
        for (Cloud cloud: clouds){
            for(int i= 0; i< numOfPicks;i++){
                try{
                    pick = basket.pickStudent();
                    if (pick == null)
                        throw new IllegalStateException();
                    picks.add(pick);
                }
                catch (EmptyBasketException e){
                    cloud.fillStudents(picks);
                    cloud.setFilled(false);
                    picks.clear();
                    listeners.firePropertyChange("CloudsRefill", null, new ArrayList<>(clouds));
                    boolean oldLastRound = lastRound;
                    setLastRound(true);
                    listeners.firePropertyChange("LastRound", oldLastRound, lastRound);
                    return;
                }
            }
            cloud.fillStudents(picks);
            cloud.setFilled(true);
            picks.clear();
        }
        listeners.firePropertyChange("CloudsRefill", null, new ArrayList<>(clouds)); //rivedere come mandare effettivamente
    }



    /**
     * Method calculateInfluence calculate the player with the highest influence on the given island
     *If two or more players have the highest influence, then only one of them is returned, together with the isDraw flag
     * which tells to the Controller who called the calculateInfluence method if the outcome of the calculation was a draw or not.
     * @param island : reference of the island on which the influence is calculated
     */
    public HashMap<String,String> calculateInfluence(Island island){
        HashMap<String,Integer>  influences = calculateStudentsInfluences(island,players);
        String towersOwnerName = getTowersOwnerName(island,players);
        if(towersOwnerName != null)
            influences.put(towersOwnerName,influences.get(towersOwnerName) + island.getTowers().size());
        HashMap<String, String> result = calculateIslandOwner(island,influences);
        mergeIslands(island);
        return result;
    }

    /**
     * Method responsible for the merging of two islands.
     * Two islands are merged when they are close and they have the same owner.
     * @param island: reference of the island that has to be merged
     */
    protected void mergeIslands(Island island){
        int islandId = islands.indexOf(island);
        int mergerId;
        System.out.println("Sono sull'isola "+island.getIslandIndex() + ", indice nell'array islands: "+islands.indexOf(island));
        Island leftIsland = islands.get((islandId + islands.size() - 1)%islands.size()); //previous island
        Island rightIsland = islands.get((islandId+1)%islands.size()); //next island
        System.out.println("Isola a dx: "+rightIsland.getIslandIndex() + ", indice nell'array islands: "+islands.indexOf(rightIsland));
        System.out.println("Isola a sx: "+leftIsland.getIslandIndex() + ", indice nell'array islands: "+islands.indexOf(leftIsland));

       if(leftIsland.getOwnerTeam() != null){
            if(leftIsland.getOwnerTeam().equals(island.getOwnerTeam())){
                if(leftIsland.getIslandIndex() < island.getIslandIndex()){
                    if (island.equals(currentMotherNatureIsland))
                        currentMotherNatureIsland=leftIsland;
                    mergerId = leftIsland.getIslandIndex();
                    leftIsland.merge(island);
                    islands.remove(island);
                }else{
                    if (leftIsland.equals(currentMotherNatureIsland))
                        currentMotherNatureIsland=leftIsland;
                    mergerId = island.getIslandIndex();
                    island.merge(leftIsland);
                    islands.remove(leftIsland);
                }

                listeners.firePropertyChange("Merge", null, new ArrayList<>(islands));
            }
        }

        if(rightIsland.getOwnerTeam() != null){
            if(rightIsland.getOwnerTeam().equals(island.getOwnerTeam())){
                if(rightIsland.getIslandIndex() < island.getIslandIndex()){
                    if (island.equals(currentMotherNatureIsland))
                        currentMotherNatureIsland=rightIsland;
                    mergerId = rightIsland.getIslandIndex();
                    rightIsland.merge(island);
                    islands.remove(island);
                }else{
                    if (rightIsland.equals(currentMotherNatureIsland))
                        currentMotherNatureIsland=island;
                    mergerId = island.getIslandIndex();
                    island.merge(rightIsland);
                    islands.remove(rightIsland);
                }
                listeners.firePropertyChange("Merge", null, new ArrayList<>(islands));
            }
        }
    }

    /**
     * Method that calculates the Owner of an island according to his influence on that island
     * If there is a draw of influence there's no change about the island's owner
     * @param island: reference to the island on I want to calculate influence
     * @param influences: map of the calculated influences for each player on that island
     * @return HashMap<String, String>: Hashmap that contains the information about a possible draw about the influence
     * and the PlayerID of the Owner
     */
    protected HashMap<String,String> calculateIslandOwner(Island island,HashMap<String,Integer> influences){
        int max = getMax(influences.values());
        boolean isDraw = isDuplicate(influences.values(),max);
        HashMap<String, String> returnMap = new HashMap<>();

        Player newOwner=null;
        if (isDraw){
            newOwner = island.getOwner();
            returnMap.put("Is Draw", Constants.DRAW);
        }
        else{
            returnMap.put("Is Draw", Constants.NO_DRAW);
            for (String player: influences.keySet())
                if (influences.get(player)==max)
                    newOwner = getPlayerByName(player);
        }
        returnMap.put("Player Name", (newOwner==null) ? null : newOwner.getNickname()); //null se non c'è mai stato un proprietario
        if(newOwner!=null && newOwner!=island.getOwner()){
            changeIslandOwnership(island, newOwner);
        }

        return returnMap;
    }

    protected void changeIslandOwnership(Island island, Player newOwner){
        ArrayList<Tower> oldTowers = island.removeTower();
        //si potrebbe evitare di chiamare sempre questo metodo perché a prescindere dalla presenza di vecchie torri
        // scatta un messaggio di propertyChange che, se non ci sono torri, è inutile
        int numOldTowers = oldTowers.size();
        if (numOldTowers!=0)
            for (Player player: players)
                if (player.getTeam().equals(oldTowers.get(0)))
                    player.addTowersToBoard(numOldTowers);
        island.setOwner(newOwner);
        for (int i = 0; i< island.getNumMergedIslands();i++){
            island.addTower(newOwner.getTeam());
            newOwner.removeTowerFromBoard();
        }
    }

    /**
     * Method that calculates the influence of each player on an island
     * @param island: instance of the island on which I want to calculate influence
     * @param players: list of all the players of the game
     * @return
     */
    protected HashMap<String,Integer> calculateStudentsInfluences(Island island,ArrayList<Player> players){
        int infl;
        HashMap<String,Integer> influences = new HashMap<>();
        for(Player player: players){
            infl = 0;
            for(Color t:player.getBoard().getTeacherTable()){
                infl += island.getStudentsOfColor(t).size();
            }
            influences.put(player.getNickname(),infl);
        }
        return influences;
    }

    /**
     * Method that returns the PlayerID of the player that are of the team corresponding to the color of the towers
     * on the island. If the team of the player and the tower color on the island it's not the same it returns -1
     * @param island: reference of the island of which I want to know the OwnerID
     * @param players: list of all the players of the game
     * @return nickname of the owner of the island or -1 if there's not an owner
     */
    protected String getTowersOwnerName(Island island, ArrayList<Player> players){
        if (island.getTowers().size() == 0) return null;
        for(Player player : players)
                if (island.getOwnerTeam().equals(player.getTeam()))
                    return player.getNickname();
        return null;
    }

    /**
     * Method lastRound sets the relative boolean flag to true or false, according to the game's state.
     * @param lastRound : flag representative of the satisfaction of gameover's conditions.
     */
    public void setLastRound(boolean lastRound) {
        this.lastRound = lastRound;
    }

    /**
     * @return ArrayList<color>: list of teachers in the game
     *     */
    public ArrayList<Color> getTeachers() {
        return teachers;
    }

    /**
     * @return ArrayList<island>: list of islands in the game
     */
    public ArrayList<Island> getIslands() {
        return islands;
    }

    /**
     * @return ArrayList<Cloud>: list of Clouds in the game
     */
    public ArrayList<Cloud> getClouds() {
        return clouds;
    }

    /**
     * @return instance of the basket used in the game
     */
    public Basket getBasket() {
        return basket;
    }

    /**
     * @return ArrayList<Player>: list of players of the game
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * @return HashMap<Integer,Assistant>: List of Assistant Cards that have been played on a turn
     *     */
    public HashMap<String,Assistant> getCurrentTurnAssistantCards(){
        return currentTurnAssistantCards;
    }

    /**
     * Method that returns the ID of the Clouds that are empty
     * @return ArrayList<Integer>: list of the CloudID of the empty clouds
     */
    public ArrayList<Integer> GetEmptyCloudsID(){
        ArrayList<Integer> emptyClouds = new ArrayList<>();
        for(Cloud cloud : clouds){
            if(cloud.getStudents().isEmpty())
                emptyClouds.add(cloud.getCloudIndex());
        }
        return emptyClouds;
    }

    /**
     * Method that returns the list of the Assistant card that have not been played yet
     * @param nickname: name given to the player,TO DO
     * @return ArrayList<Assistant>: list of Assistant Card that are still playable
     */
    public ArrayList<Assistant> getPlayableAssistantCards(String nickname){
        return getPlayerByName(nickname).getDeck();
    }

    /**
     * Method that set an Island as the Island on where Mother Nature is located
     * @param currentMotherNatureIsland: reference of the Island where I want to place Mother Nature
     */
    public void setCurrentMotherNatureIsland(Island currentMotherNatureIsland) {
        this.currentMotherNatureIsland = currentMotherNatureIsland;
    }

    /**
     * Sets the selected basket as the game's basket.
     * @param basket: Basket instance being set as the game's basket.
     */
    public void setBasket(Basket basket) {
        this.basket = basket;
    }

    /**
     * This method returns the entire list of Assistant cards of the game
     * @return ArrayList<Assistant>: list of Assistant cards
     */
    public ArrayList<Assistant> getAssistantDecks() {
        return assistantDecks;
    }

    /**
     * Method that checks if the Game is ended
     * @return true if the game current game is over, false otherwise
     */
    public boolean checkGameOver(){
        for(Player player: players){
            if(player.getBoard().getTowers()==0){
                winner = player;
                return true;
            }
        }
        if (islands.size()<=Constants.ISLAND_THRESHOLD_FOR_GAME_OVER){
            calculateWinner();
            return true;
        }
        return false;
    }

    /**
     * Method calculateWinner checks and, if present, sets the winner name by counting each player's number of towers and teacher tiles. If a tie is reached winner is set to null.
     */
    public void calculateWinner(){
        int minTowers = Constants.MAX_TOWER_NUMBER;
        ArrayList<Player> potentialWinners = new ArrayList<>();
        for (Player potentialWinner: players){
            if (potentialWinner.getBoard().getTowers()<minTowers){
                potentialWinners.clear();
                potentialWinners.add(potentialWinner);
                minTowers=potentialWinner.getBoard().getTowers();
            }
            else if (potentialWinner.getBoard().getTowers()==minTowers)
                potentialWinners.add(potentialWinner);
        }
        if (potentialWinners.size() == 1)
            winner = potentialWinners.get(0);
        else {
            int maxTeachers = -1;
            for (Player finalPlayer : potentialWinners) {
                if (finalPlayer.getBoard().getTeacherTable().size()>maxTeachers){
                    winner = finalPlayer;
                    maxTeachers= finalPlayer.getBoard().getTeacherTable().size();
                }
                else if (finalPlayer.getBoard().getTeacherTable().size()==maxTeachers)
                    winner = null;
            }
        }
    }

    /**
     * Method that returns the instance of a Player based on his nickname
     * @param name: nickname of a Player
     * @return instance of the Player corresponding to the given nickname
     */
    public Player getPlayerByName(String name){
        for (Player player: players){
            if(player.getNickname().equalsIgnoreCase(name))
                return player;
        }
        return null;
    }

    /**
     * Method that returns the instance of the winner
     * @return instance of the Winner
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Method that returns a Map that identifies the player that is the owner of a given color teacher
     * @return HashMap<Color, Player>: Map that associates at every color the owner of its corresponding teacher
     */
    public HashMap<Color, Player> getTeachersOwners() {
        return teachersOwners;
    }

    /**
     * Method that sets a player as a current player according to the turn priority
     * @param currentPlayerName: name of the current Plater
     */
    public void setCurrentPlayer(String currentPlayerName) {
        this.currentPlayer = getPlayerByName(currentPlayerName);
    }

    /**
     * Method that returns the max from an ArrayList of integer
     * @param list: generic list of integer
     * @return the max number of the list
     */
    private int getMax(Collection<Integer> list){
        return list
                .stream()
                .mapToInt(v -> v)
                .max().orElseGet(() -> -1);
    }

    /**
     * Method that checks if a value occurs more than once in an ArrayList
     * @param values: Collection that method has to check
     * @param value: integer that method has to check if there's a duplicate
     * @return true if there's a duplicate of the value, false otherwise
     */
    private boolean isDuplicate(Collection<Integer> values, int value) {
        return Collections.frequency(values, value) > 1;
    }

    /**
     * Method getNumOfPlayers returns the number of players playing.
     * @return: number of of players playing.
     */
    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public boolean areCloudsFull(){
        for (Cloud cloud: clouds)
            if (!cloud.isFilled())
                return false;
        return true;
    }

    /**
     * Method resetCurrentTurnAssistantCards clears the players' current Assistant cards.
     */
    public void resetCurrentTurnAssistantCards(){
        currentTurnAssistantCards.clear();
        listeners.firePropertyChange("CurrentTurnAssistantCards", null, currentTurnAssistantCards);
    }

    /**
     * Method getCurrentMotherNatureIsland returns the island instance on which Mother Nature can be found.
     * @return Island instance where Mother Nature is placed.
     */
    public Island getCurrentMotherNatureIsland() {
        return currentMotherNatureIsland;
    }

    /**
     * Method getIslandById returns the Island instance identified by the selected id.
     * @param islandId: identification for an Island instance.
     * @return the Island instance identified by islandId if it exists, null otherwise.
     */
    public Island getIslandById(int islandId){
        for (Island island: islands){
            if (island.getIslandIndex()==islandId)
                return island;
        }
        return null;
    }

    /**
     * Method hasDuplicates checks if the selected ArrayList has duplicate elements.
     * @param studentIndexes: ArrayList carrying Integer instances that need to be checked for duplicates.
     * @return true if the input ArrayList has duplicated elements, false otherwise.
     */
    public boolean hasDuplicates(ArrayList<Integer> studentIndexes){
        return (studentIndexes.stream().distinct().count()!=studentIndexes.size());
    }

    /**
     * Method setPropertyChangeListeners sets the listeners of Game's main attributes.
     * @param controller: controller instance listening to the game's changes.
     */
    public void setPropertyChangeListeners(GameController controller){
        listeners.addPropertyChangeListener("MotherNature", controller); //fire fatto, anche in exp
        listeners.addPropertyChangeListener("Merge", controller); //fire fatto
        listeners.addPropertyChangeListener("LastRound", controller); //fire fatto
        listeners.addPropertyChangeListener("CloudsRefill", controller); //fire fatto
        listeners.addPropertyChangeListener("CurrentTurnAssistantCards", controller); //fire fatto
        for (Cloud cloud: clouds){
            cloud.setPropertyChangeListener(controller); //fire fatto
        }
        for (Island island: islands){
            island.setPropertyChangeListener(controller); //fire fatto, in teoria (vedere per students)
        }
        for (Player player: players){
            player.setPropertyChangeListener(controller); //fire fatto
        }
    }
}






