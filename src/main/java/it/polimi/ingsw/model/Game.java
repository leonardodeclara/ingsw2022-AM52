package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyBasketException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class contains the majority of the game's elements and logic.
 */

public class Game {
    private static final int MAX_NUM_ISLANDS = 12;
    private static final int MAX_LOBBY_SIZE = 10;
    protected static final int ISLAND_ID_NOT_RECEIVED = -1;
    private static final int ISLAND_THRESHOLD_FOR_GAME_OVER = 3;
    private static final int MAX_TOWER_NUMBER = 10;
    protected Basket basket;
    protected ArrayList<Player> players;
    //private ArrayList<Player> activePlayers;
    //potrebbe servire per gestire la resilienza
    protected Player currentPlayer;
    private Player winner;
    protected ArrayList<Island> islands;
    private ArrayList<Cloud> clouds;
    protected ArrayList<Color> teachers;
    private ArrayList<Assistant> assistantDecks;
    private HashMap<Integer,Assistant> currentTurnAssistantCards;
    protected Island currentMotherNatureIsland;
    private boolean lastRound;
    protected HashMap<Color,Player> teachersOwners;


    /**
     * Constructor creates a Game instance
     */
    //AGGIUNGERE TRY CATCH PER refillClouds()che setta lastRound = true
    //AGGIUNGERE TRY CATCH PER playAssistantCard() quando viene giocata l'ultima, che setta lastRound = true
    //AGGIUNGERE TRY CATCH per refillClouds() per quando non ci sono abbastanza studenti per le nuvole => si salta la fase letPlayerPickStudent
    public Game() {
        players = new ArrayList<>();
        islands = new ArrayList<>();
        clouds = new ArrayList<>();
        teachers = new ArrayList<>();
        assistantDecks = new ArrayList<>(); //bisogna implementare effettivamente le 40 carte con relative statistiche
        currentTurnAssistantCards = new HashMap<Integer,Assistant>();
        this.lastRound = false;
        winner = null;
        teachersOwners = new HashMap<>();
    }

    /**
     * This method add an existing player to the game.
     * @param player: instance of the player which has been admitted to the game.
     */
    //bisogna gestire il lancio di questa eccezione (creare una ad hoc)
    public void addPlayer(Player player){
        if (players.size()<3)
            players.add(player);
        else
            throw new RuntimeException("Superato limite di giocatori");
    }

    /**
     * This method instantiates all the game elements (clouds,teachers,basket,islands and boards).
     *
     */
    public void instantiateGameElements(){
        //istanziati i professori al tavolo di gioco
        teachers.addAll(Arrays.asList(Color.values()));
        for(int i =0; i< Color.values().length;i++){
            teachersOwners.put(Color.values()[i], null);
        }

        //aggiunte tutte le carte di tutti i maghi
        for(int numWizard = 0; numWizard < 4; numWizard++){
            int numMoves;
            for(int priority = 1; priority < 11; priority++){
                numMoves = priority%2==0 ? priority/2 : priority/2+1;
                assistantDecks.add(new Assistant(numMoves,priority, numWizard));
            }
        }

        //istanziate le isole
        for (int i = 0; i< MAX_NUM_ISLANDS; i++){
            islands.add(new Island(i));
        }
        //posizionata in maniera randomica madre natura
        Random indexGenerator = new Random();
        int initialMotherNature = indexGenerator.nextInt(MAX_NUM_ISLANDS);

        //non creo un metodo placeMotherNature solo per fare questa azione
        islands.get(initialMotherNature).setMotherNature(true);
        currentMotherNatureIsland=islands.get(initialMotherNature);

        //istanzio il sacchetto preparatorio
        basket = new Basket(new int[]{2, 2, 2, 2, 2});

        //riempio le isole con le pedine
        fillIslands();

        //riempio il sacchetto definitivo
        basket = new Basket(new int[]{24,24,24,24,24});

        //creo le nuvole
        for (int i = 0; i < players.size(); i++){
            clouds.add(new Cloud(i));
        }

        //setto il numero di torri nella board
        for (Player player: players)
            player.getBoard().setTowers(players.size()==2? 8:6);


    }

    /**
     * Method initiatePlayerLobby calculates the number of students to put in the players' lobby
     * and does it
     * @param playerId : id given to the player, used as the index for the players arrayList
     */
    public void initiatePlayerLobby(int playerId){
        int studentLimit = players.size()==2? 7: 9;
        for(int k = 0; k < studentLimit; k++)
            players.get(playerId).getBoard().addToLobby(basket.pickStudent());
    }

    /**
     * Method responsible for the filling of the islands' students spots at the beginning of the game.
     * Each student tile is randomly picked by the basket, which in this case only contains 2 tiles for each color.
     * Nor mother nature's island neither the opposite one will receive any student.
     */
    public void fillIslands(){
        //si potrebbe fare anche senza il bisogno di currentMotherNatureIsland
        for (Island island: islands){
            if(island.getIslandIndex()!= currentMotherNatureIsland.getIslandIndex() && !(island.getIslandIndex()==(6+currentMotherNatureIsland.getIslandIndex())%12)){
                island.addStudent(basket.pickStudent());
            }
        }
    }

    /**
     * Method giveAssistantDeck assigns the deck of 10 assistant cards to the given player.
     * @param playerId : id given to the player, used as the index for the players arrayList
     * @param deckId : id given to the deck, the player uses it communicate which deck he wants
     */
    public void giveAssistantDeck(int playerId, int deckId){
        ArrayList<Assistant> assignedDeck = new ArrayList<>();
        for (Assistant assistant: assistantDecks){
            if (assistant.getWizard()==deckId){
                assignedDeck.add(assistant);
                //assistantDecks.remove(assistant);
            }
        }
        assistantDecks.removeAll(assignedDeck);
        players.get(playerId).setDeck(assignedDeck);
    }

    /**
     * Method playAssistantCard removes the card to the player's deck and adds it to the current turn played assistant cards
     * in order to calculate the playing order for the action phase
     * @param playerId : id given to the player, used as the index for the players ArrayList
     * @param cardId : id given to the card, used as the index for the player's deck ArrayList
     */
    public int playAssistantCard(int playerId,int cardId){
        ArrayList<Assistant> newDeck = players.get(playerId).getDeck();
        Assistant playedCard = newDeck.get(cardId);
        if(!isCardPlayable(playedCard, newDeck)) return -1;
        int cardScore = playedCard.getPriority();
        currentTurnAssistantCards.put(playerId,playedCard);
        newDeck.remove(cardId);
        players.get(playerId).setDeck(newDeck);
        return cardScore;
    }

    /**
     * Method isCardPlayable checks if a card can be played or not in the current turn.
     * If it's the only card the player has or every other card in the player deck has already been played by someone else,
     * or the played card is different from the ones played by the other players
     * then return true
     * otherwise, if the played card has already been played but the player has in its deck other cards, different from the ones already played
     * return false
     * @param playedCard : the card played by the player
     * @param playerDeck : the deck of the player
     */
    protected boolean isCardPlayable(Assistant playedCard, ArrayList<Assistant> playerDeck){
        List<Integer> playerPriorities = playerDeck.stream().map(Assistant::getPriority).collect(Collectors.toList());
        List<Integer> currentTurnPriorities = currentTurnAssistantCards.values().stream().map(Assistant::getPriority).collect(Collectors.toList());
        if(currentTurnPriorities.containsAll(playerPriorities) || playerDeck.size() == 1)
            return true;
        else
            if(currentTurnPriorities.contains(playedCard.getPriority()))
                return false;
            else
                return true;
    }

    //se il deck del giocatore e quello currentTurnAssistantCards ha almeno un valore differente, ma il valore di playedCard è uguale -> false
    //se il deck del biocatore e quello delle arte giocate hanno gli stessi valori  -> true



    /**
     * Method moveMotherNature checks  if the player with Player ID can move Mother Nature of numSteps
     *  and if it's doable, moves Mother Nature from the current island to the new one
     * @param playerId : id given to the player, used as the index for the players ArrayList
     * @param numSteps : number of islands that the player identified with the playerId wants to move mother nature
     */
    public boolean moveMotherNature(int playerId,int numSteps){
        if(!isMoveMNLegal(playerId,numSteps))
            return false;

        Island from = islands.get(currentMotherNatureIsland.getIslandIndex());
        Island dest = islands.get((from.getIslandIndex() + numSteps) % islands.size());

        currentMotherNatureIsland=islands.get(dest.getIslandIndex());
        from.setMotherNature(false);
        dest.setMotherNature(true);
        return true;
    }

    /**
     * Method moveStudentFromLobby checks  if the player with Player ID can move the student in his lobby to an island or
     *  the corresponding table. If it's doable the student is moved from the lobby to the table/island. If the island id is -1
     *  (ISLAND_ID_NOT_RECEIVED) the student is moved to the table. Otherwise, the student is moved to the island
     * @param playerId : id given to the player, used as the index for the players ArrayList
     * @param studentIndex : number of islands that the player identified with the playerId wants to move mother nature
     * @param islandId : the id of the destination island (or -1 if no island is specified)
     */
    public boolean moveStudentFromLobby(int playerId,int studentIndex,int islandId){
        Player player = players.get(playerId);
        if(!isMoveStudentFromLobbyLegal(player,studentIndex,islandId))
            return false;
        Color studentToMove = player.getBoard().getLobbyStudent(studentIndex);
        player.getBoard().removeFromLobby(studentIndex);
        if(islandId == ISLAND_ID_NOT_RECEIVED)
            player.getBoard().addToTable(studentToMove);
        else
        {
            Island islandDest = islands.get(islandId);
            islandDest.addStudent(studentToMove);
        }
        //aggiorna l'ownership dei teacher
        updateTeachersOwnership(player);
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
                    owner.getBoard().removeTeacher(c);
                    player.getBoard().addTeacher(c);
                    teachersOwners.put(c, player);
                }
            }
            else {
                if (player.getBoard().getTableNumberOfStudents(c)>0){
                    player.getBoard().addTeacher(c);
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
    public boolean moveStudentsToLobby(int playerId,int cloudId){
        Player player = players.get(playerId);
        if(!isMoveStudentsToLobbyLegal(player,cloudId))
            return false;
        Cloud cloud = clouds.get(cloudId);
        ArrayList<Color> studentsToMove = cloud.emptyStudents();
        for(Color student : studentsToMove)
            player.getBoard().addToLobby(student);
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
     * @param playerId : id given to the player, used as the index for the players ArrayList
     * @param numSteps : number of islands that the player identified with the playerId wants to move mother nature
     */
    public boolean isMoveMNLegal(int playerId,int numSteps){
        int playerMaxSteps = currentTurnAssistantCards.get(playerId).getNumMoves();
        return numSteps > playerMaxSteps ? false : true;
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
            if(islandId == ISLAND_ID_NOT_RECEIVED){
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
                    setLastRound(true);
                    //vedere poi come funziona la segnalazione del last round a tutti i giocatori
                    return;
                }
            }
            cloud.fillStudents(picks);
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
     * Method responsible for the merging of two islands
     * Two island is merged when they are close and they have the same owner
     * @param island: reference of the island that has to be merged
     */
    protected void mergeIslands(Island island){
        int islandId = islands.indexOf(island);
        Island leftIsland = islands.get((islandId + islands.size() - 1)%islands.size()); //previous island
        Island rightIsland = islands.get((islandId+1)%islands.size()); //next island

        //c'è un problema quando l'isola non ha owner
       if(leftIsland.getOwnerTeam() != null){
            if(leftIsland.getOwnerTeam().equals(island.getOwnerTeam())){
                if(leftIsland.getIslandIndex() < island.getIslandIndex()){
                    leftIsland.merge(island);
                    islands.remove(island);
                }else{
                    island.merge(leftIsland);
                    islands.remove(leftIsland);
                }
            }
        }

        if(rightIsland.getOwnerTeam() != null){
            if(rightIsland.getOwnerTeam().equals(island.getOwnerTeam())){
                if(rightIsland.getIslandIndex() < island.getIslandIndex()){
                    rightIsland.merge(island);
                    islands.remove(island);
                }else{
                    island.merge(rightIsland);
                    islands.remove(rightIsland);
                }
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
    public HashMap<Integer,Assistant> getCurrentTurnAssistantCards(){
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
     * @param playerId: id given to the player, used as the index for the players ArrayList
     * @return ArrayList<Assistant>: list of Assistant Card that are still playable
     */
    public ArrayList<Assistant> getPlayableAssistantCards(int playerId){
        return players.get(playerId).getDeck();
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
                return true;
            }
        }
        if (islands.size()<=ISLAND_THRESHOLD_FOR_GAME_OVER){
            int minTowers = MAX_TOWER_NUMBER;
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
                winner = potentialWinners.get(0);
                return true;

            }
            else {
                int minTeachers = 5;
                for (Player finalPlayer : potentialWinners) {
                    if (finalPlayer.getBoard().getTeacherTable().size()<minTeachers){
                        winner = finalPlayer;
                        minTeachers= finalPlayer.getBoard().getTeacherTable().size();
                    }
                    else if (finalPlayer.getBoard().getTeacherTable().size()==minTeachers){
                        winner = null;
                    }
                }
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
}






