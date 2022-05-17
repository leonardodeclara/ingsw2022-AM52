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
    protected Island currentMotherNatureIsland; //firePropertyChange
    private boolean lastRound;
    protected HashMap<Color, Player> teachersOwners;
    protected PropertyChangeSupport listeners;


    /**
     * Constructor creates a Game instance
     */
    //AGGIUNGERE TRY CATCH PER refillClouds()che setta lastRound = true
    //AGGIUNGERE TRY CATCH PER playAssistantCard() quando viene giocata l'ultima, che setta lastRound = true
    //AGGIUNGERE TRY CATCH per refillClouds() per quando non ci sono abbastanza studenti per le nuvole => si salta la fase letPlayerPickStudent
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
        currentTurnAssistantCards = new HashMap<String, Assistant>();
        lastRound = false;
        winner = null; //firePropertyChange
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
     * TODO: modificare javadocs e tutti i relativi test
     */
    /**
     * This method add new players to the game.
     *
     * @param playersNames: names of the players which have been admitted to the game.
     */
    //bisogna gestire il lancio di questa eccezione (creare una ad hoc)
    public void addPlayers(ArrayList<String> playersNames) {
        for (String playerName: playersNames)
            if (players.size() < 3) {
                Player player = new Player(players.size(), playerName);
                player.getBoard().setTowers(numOfPlayers == 2 ? 8 : 6);
                players.add(player);
            } else
                throw new RuntimeException("Superato limite di giocatori");
    }

    /**
     * TODO: modificare i test di instantiateGameElements perché sono stati spostati degli assegnamenti, vedere sotto
     */
    /**
     * This method instantiates all the game elements (clouds,teachers,basket,islands and boards).
     */
    public void instantiateGameElements(ArrayList<String> playersNames) {
        //istanziati i professori al tavolo di gioco
        teachers.addAll(Arrays.asList(Color.values()));
        for (int i = 0; i < Color.values().length; i++) {
            teachersOwners.put(Color.values()[i], null);
        }
        //System.out.println("Game: ho finito di istanziare i professori");

        //aggiunte tutte le carte di tutti i maghi
        for (int numWizard = 0; numWizard < 4; numWizard++) {
            int numMoves;
            for (int priority = 1; priority < 11; priority++) {
                numMoves = priority % 2 == 0 ? priority / 2 : priority / 2 + 1;
                assistantDecks.add(new Assistant(numMoves, priority, numWizard));
            }
        }
        //System.out.println("Game: ho finito di istanziare i deck assistenti");

        //posizionata in maniera randomica madre natura
        Random indexGenerator = new Random();
        int initialMotherNature = indexGenerator.nextInt(Constants.MAX_NUM_ISLANDS);

        //non creo un metodo placeMotherNature solo per fare questa azione
        islands.get(initialMotherNature).setMotherNature(true);

        currentMotherNatureIsland = islands.get(initialMotherNature);
        //listeners.firePropertyChange("MotherNature", null, currentMotherNatureIsland.getIslandIndex());
        //vedere se ci va

        //System.out.println("Game: ho finito di settare la posizione iniziale di MN");
        //istanzio il sacchetto preparatorio
        basket = new Basket(new int[]{2, 2, 2, 2, 2});

        //riempio le isole con le pedine
        fillIslands();
        //System.out.println("Game: ho finito di fare fill alle isole ");

        //riempio il sacchetto definitivo
        basket = new Basket(new int[]{24, 24, 24, 24, 24});

        addPlayers(playersNames);
        initiatePlayersLobbies();

        //System.out.println("Game: ho finito di istanziare i gameElements");
    }

    /**
     * TODO: cambiare i relativi test
     */
    /**
     * Method initiatePlayersLobbies calculates the number of students to put in the players' lobby
     * and does it
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
        //si potrebbe fare anche senza il bisogno di currentMotherNatureIsland
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
     * @param playerName : name of the player, used as the index for the players arrayList
     * @param deckId     : id given to the deck, the player uses it communicate which deck he wants
     */
    //da cambiare, in input deve prendere il nickname. cambiare anche tutti i relativi test
    public void giveAssistantDeck(String playerName, int deckId) {
        ArrayList<Assistant> assignedDeck = new ArrayList<>();
        wizards.remove((Integer) deckId); //firePropertyChange
        for (Assistant assistant : assistantDecks) {
            if (assistant.getWizard() == deckId) {
                assignedDeck.add(assistant);
                //assistantDecks.remove(assistant);
            }
        }
        assistantDecks.removeAll(assignedDeck);
        Player player = getPlayerByName(playerName);
        player.setDeck(assignedDeck);
    }

    /**
     * Method playAssistantCard removes the card to the player's deck and adds it to the current turn played assistant cards
     * in order to calculate the playing order for the action phase
     *
     * @param nickname :
     * @param cardId   : id given to the card, used as the index for the player's deck ArrayList
     */
    public int playAssistantCard(String nickname, int cardId) {
        ArrayList<Assistant> newDeck = getPlayerByName(nickname).getDeck();
        cardId--; //priority van da 1 a 10, ma ci serve l'index
        Assistant playedCard = newDeck.get(cardId);
        if (!isCardPlayable(playedCard, newDeck)) return -1;
        int cardScore = playedCard.getPriority();
        currentTurnAssistantCards.put(nickname, playedCard); //firePropertyChange
        listeners.firePropertyChange("CurrentTurnAssistantCards", null, currentTurnAssistantCards);
        getPlayerByName(nickname).removeAssistantCard(cardId);
        return cardScore;
    }

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

    //se il deck del giocatore e quello currentTurnAssistantCards ha almeno un valore differente, ma il valore di playedCard è uguale -> false
    //se il deck del biocatore e quello delle arte giocate hanno gli stessi valori  -> true


    /**
     * Method moveMotherNature checks  if the player with Player ID can move Mother Nature of numSteps
     * and if it's doable, moves Mother Nature from the current island to the new one
     *
     * @param nickname : name given to the player, TO DO
     * @param numSteps : number of islands that the player identified with the playerId wants to move mother nature
     */
    public boolean moveMotherNature(String nickname, int numSteps) {
        if (!isMoveMNLegal(nickname, numSteps))
            return false;

        Island from = islands.get(currentMotherNatureIsland.getIslandIndex());
        Island dest = islands.get((from.getIslandIndex() + numSteps) % islands.size());

        currentMotherNatureIsland = islands.get(dest.getIslandIndex()); //firePropertyChange
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
    //da cambiare, in input deve prendere il nickname. cambiare anche tutti i relativi test
    public boolean moveStudentsFromLobby(String nickname, ArrayList<Integer> studentIDs, ArrayList<Integer> islandIDs) {
        Player player = getPlayerByName(nickname);
        for (int i = 0; i < studentIDs.size(); i++) { //controlliamo se la mossa è legit per ogni studente e per ogni destinazione
            if (!isMoveStudentFromLobbyLegal(player, studentIDs.get(i), islandIDs.get(i)))
                return false;
        }
        for (int i = 0; i < studentIDs.size(); i++) { //controlliamo se la mossa è legit per ogni studente e per ogni destinazione
            Color studentToMove = player.removeFromBoardLobby(studentIDs.get(i));
            if (islandIDs.get(i) == Constants.ISLAND_ID_NOT_RECEIVED)
                player.getBoard().addToTable(studentToMove);
            else {
                Island islandDest = islands.get(islandIDs.get(i));
                islandDest.addStudent(studentToMove);
            }
            //aggiorna l'ownership dei teacher
            updateTeachersOwnership(player);
        }

        return true;
    }



    /**
     * Method updateTeachersOwnership recalculates the given player's number of students and possibly assigns him 1+
     *  teachers ownership
     * @param player : reference of the player of whom we want to check teachers' ownership
     *
     */
    public void updateTeachersOwnership(Player player){
        for(Color c : Color.values()) {
            Player owner = teachersOwners.get(c);
            if(owner != null){
                if (player.getBoard().getTableNumberOfStudents(c) > owner.getBoard().getTableNumberOfStudents(c)) {
                    owner.removeTeacherFromBoard(c); //firePropertyChange
                    player.addTeacherToBoard(c); //firePropertyChange
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
     * Method moveStudentsToLobby moves all the students on a given cloud, to the given player lobby
     *
     * @param playerId : id given to the player, used as the index for the players ArrayList
     * @param cloudId : id given to the cloud to empty
     */
    //da cambiare, in input deve prendere il nickname. cambiare anche tutti i relativi test
    public boolean moveStudentsToLobby(int playerId,int cloudId){
        Player player = players.get(playerId);
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
        return numSteps > playerMaxSteps? false : true;
    }

    /**
     * Method isMoveStudentFromLobbyLegal checks  if the player with Player ID can move the student in his lobby to an island or
     *  the corresponding table.
     * @param player : id given to the player, used as the index for the players ArrayList
     * @param studentIndex : number of islands that the player identified with the playerId wants to move mother nature
     * @param islandId : the id of the destination island (or -1 if no island is specified)
     */
    public boolean isMoveStudentFromLobbyLegal(Player player,int studentIndex,int islandId){
        //non sono sicuro sia il modo giusto per gestire questo caso
        if (studentIndex >= player.getBoard().getLobby().size() || studentIndex<0)
            return false;
        Color studentToMove = player.getBoard().getLobbyStudent(studentIndex);
        if(studentToMove != null){
            if(islandId == Constants.ISLAND_ID_NOT_RECEIVED){
                if(!player.getBoard().isTableFull(studentToMove))
                    return true;
            }
            else if(islandId >= 0 && islandId <= islands.size())
                    return true;
        }
        return false;
    }

    /**
     * Method isMoveStudentsToLobbyLegal checks if the given cloudId identifies a cloud which contains students
     *
     * @param player: reference of the player who wants to perform the
     * @param cloudId : id given to the cloud to empty
     */
    public boolean isMoveStudentsToLobbyLegal(Player player,int cloudId){
        if (cloudId >= 0 && cloudId <= clouds.size()-1)
            return (!clouds.get(cloudId).getStudents().isEmpty()) ? true : false;
        return false;
    }

    /**
     * Method refillClouds refills all the clouds in the clouds ArrayList.
     * It's responsible for checking whether a picked student is null, in which case it sets lastRound flag to true.
     */
    public void refillClouds(){
        int numOfPicks = players.size()+1;
        ArrayList<Color> picks = new ArrayList<>();
        Color pick;
        for (Cloud cloud: clouds){
            for(int i= 0; i< numOfPicks;i++){
                //questo andrebbe sostituito con un trycath, il caso null è per l'errore di estrazione
                try{
                    pick = basket.pickStudent();
                    if (pick == null)
                        throw new IllegalStateException(); //rivedere se questo è il modo giusto per gestire questo caso
                    picks.add(pick);
                }
                catch (EmptyBasketException e){
                    boolean oldLastRound = lastRound;
                    setLastRound(true); //firePropertyChange per messaggio di Last Round
                    listeners.firePropertyChange("LastRound", oldLastRound, lastRound);
                    //vedere poi come funziona la segnalazione del last round a tutti i giocatori
                    return;
                }
            }
            cloud.fillStudents(picks); //firePropertyChange
            listeners.firePropertyChange("CloudsRefill", null, new ArrayList<>(clouds)); //rivedere come mandare effettivamente
            picks.clear();
        }
    }



    /**
     * Method calculateInfluence calculate the player with the highest influence on the given island
     *If two or more players have the highest influence, then only one of them is returned, together with the isDraw flag
     * which tells to the Controller who called the calculateInfluence method if the outcome of the calculation was a draw or not.
     * @param island : reference of the island on which the influence is calculated
     */


    public HashMap<String,Integer> calculateInfluence(Island island){
        ArrayList<Integer>  influences = calculateStudentsInfluences(island,players);
        int towersOwnerIndex = getTowersOwnerIndex(island,players);
        if(towersOwnerIndex != -1)
            influences.set(towersOwnerIndex,influences.get(towersOwnerIndex) + island.getTowers().size());
            mergeIslands(island);
            return calculateIslandOwner(island,influences);
    }

    /**
     * Method responsible for the merging of two islands.
     * Two islands are merged when they are close and they have the same owner.
     * @param island: reference of the island that has to be merged
     */
    protected void mergeIslands(Island island){
        int islandId = islands.indexOf(island);
        int mergerId;
        Island leftIsland = islands.get((islandId + islands.size() - 1)%islands.size()); //previous island
        Island rightIsland = islands.get((islandId+1)%islands.size()); //next island

        //c'è un problema quando l'isola non ha owner
       if(leftIsland.getOwnerTeam() != null){
            if(leftIsland.getOwnerTeam().equals(island.getOwnerTeam())){
                if(leftIsland.getIslandIndex() < island.getIslandIndex()){
                    mergerId = leftIsland.getIslandIndex();
                    leftIsland.merge(island);
                    islands.remove(island);
                }else{
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
                    mergerId = rightIsland.getIslandIndex();
                    rightIsland.merge(island);
                    islands.remove(island);
                }else{
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
     * @param influences: list of the calculated influences of each player on that island
     * @return HashMap<String, Integer>: Hashmap that contains the information about a possible draw about the influence
     * and the PlayerID of of the Owner
     */
    protected HashMap<String,Integer> calculateIslandOwner(Island island,ArrayList<Integer> influences){
        int max = getMax(influences);
        int isDraw = isDuplicate(influences,max);
        HashMap<String, Integer> returnMap = new HashMap<>();

        Player owner = (isDraw == 1) ? island.getOwner() : players.get(influences.indexOf(max));
        returnMap.put("Is Draw", isDraw);
        returnMap.put("ID Player", (owner==null) ? null : owner.getPlayerId());
        if(owner!=null && owner!=island.getOwner())
            island.setOwner(owner);
        return returnMap;
    }

    /**
     * Method that calculates the influence of each player on an island
     * @param island: instance of the island on which I want to calculate influence
     * @param players: list of all the players of the game
     * @return ArrayList<Integer>: list of integer that represents the influence of each players on that island
     */
    protected ArrayList<Integer> calculateStudentsInfluences(Island island,ArrayList<Player> players){
        int infl;
        ArrayList<Integer> influences = new ArrayList<>();
        for(Player p: players){
            infl = 0;
            for(Color t:p.getBoard().getTeacherTable()){
                infl += island.getStudentsOfColor(t).size();
            }
            influences.add(players.indexOf(p),infl);
        }
        return influences;
    }

    /**
     * Method that returns the PlayerID of the player that are of the team corresponding to the color of the towers
     * on the island. If the team of the player and the tower color on the island it's not the same it returns -1
     * @param island: reference of the island of which I want to know the OwnerID
     * @param players: list of all the players of the game
     * @return PlayerID of the owner of the island or -1 if there's not an owner
     */
    protected int getTowersOwnerIndex(Island island, ArrayList<Player> players){
        if (island.getTowers().size() == 0) return -1;
        for(Player p : players)
                if (island.getOwnerTeam().equals(p.getTeam()))
                    return players.indexOf(p);
        return -1;
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
     * @return istance of the basket used in the game
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
    //da cambiare, in input deve prendere il nickname. cambiare anche tutti i relativi test
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
     * @return true if there's a Game over. false if there isn't
     */

    /*Da rivedere*/
    /*potremmo aggiungere attributo winner a game*/
    /*Vittoria: return true, winner = playerVincitore
    * Partita continua: return false, winner = null
    * Parità: return true, winner=null
    * */
    public boolean checkGameOver(){
        for(Player player: players){
            if(player.getBoard().getTowers()==0){
                winner = player;
                listeners.firePropertyChange("GameOver", null, winner.getNickname());
                return true;
            }
        }
        if (islands.size()<=Constants.ISLAND_THRESHOLD_FOR_GAME_OVER){
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
            if (potentialWinners.size() == 1){
                winner = potentialWinners.get(0); //firePropertyChange
                listeners.firePropertyChange("GameOver", null, winner.getNickname());
                return true;

            }
            else {
                int minTeachers = 5;
                for (Player finalPlayer : potentialWinners) {
                    if (finalPlayer.getBoard().getTeacherTable().size()<minTeachers){
                        winner = finalPlayer; //firePropertyChange
                        minTeachers= finalPlayer.getBoard().getTeacherTable().size();
                    }
                    else if (finalPlayer.getBoard().getTeacherTable().size()==minTeachers){
                        winner = null;
                    }
                }
                listeners.firePropertyChange("GameOver", null, null); //rivedere, magari mando un dato strutturato
                return true;
            }
        }
        return false;
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
     * @param currentPlayer: reference of the player
     */
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Method that returns the max from an ArrayList of integer
     * @param list: generic list of integer
     * @return the max number of the list
     */
    private int getMax(ArrayList<Integer> list){
        return list
                .stream()
                .mapToInt(v -> v)
                .max().orElseGet(() -> -1);
    }

    /**
     * Method that checks if a value occurs more than once in an ArrayList
     * @param values: ArrayList that method has to check
     * @param value: integer that method has to check if there's a duplicate
     * @return 1 if there's a duplicate of the value or 0
     */
    private int isDuplicate(ArrayList<Integer> values, int value) {
        return Collections.frequency(values,value) > 1? 1:0;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    //metodo che aggiunge i listener a tutte le classi ascoltate
    public void setPropertyChangeListeners(GameController controller){
        listeners.addPropertyChangeListener("MotherNature", controller); //fire fatto, anche in exp
        listeners.addPropertyChangeListener("Merge", controller); //fire fatto
        listeners.addPropertyChangeListener("LastRound", controller); //fire fatto
        listeners.addPropertyChangeListener("Gameover", controller); //fire fatto, rivedere un po' cosa viene mandato
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

    /*
    public void setPlayerPropertyChangeListener(String nickname, GameController controller){
        getPlayerByName(nickname).setPropertyChangeListener(controller);
    }

     */

    //in teoria
    //ordine di chiamata metodi Game inizio partita:
    //costruttore
    //addPlayer per ogni player
    //game.instantiateGameElements()
    //game.setPropertyChangeListeners(controller)
}






