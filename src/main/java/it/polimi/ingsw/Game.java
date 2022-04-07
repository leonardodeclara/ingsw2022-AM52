package it.polimi.ingsw;

import java.sql.Array;
import java.util.*;

/**
 * This class contains the majority of the game's elements and logic.
 */

public class Game {
    private static final int MAX_NUM_ISLANDS = 12;
    private static final int MAX_LOBBY_SIZE = 10;
    protected static final int ISLAND_ID_NOT_RECEIVED = -1;
    private static final int ISLAND_THRESHOLD_FOR_GAME_OVER = 3;
    private static final int MAX_TOWER_NUMBER = 10;
    private Basket basket;
    protected ArrayList<Player> players;
    //private ArrayList<Player> activePlayers;
    //potrebbe servire per gestire la resilienza
    protected Player currentPlayer;
    private Player winner;
    protected ArrayList<Island> islands;
    private ArrayList<Cloud> clouds;
    private ArrayList<Color> teachers;
    private ArrayList<Assistant> assistantDecks;
    private HashMap<Integer,Assistant> currentTurnAssistantCards;
    protected Island currentMotherNatureIsland;
    private boolean lastRound;
    protected HashMap<Color,Player> teachersOwners;

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


        for (Player player: players)
            player.getBoard().setTowers(players.size()==2? 8:6);


    }

    /**
     * Method initiatePlayerLobby calculates the number of students to put in the players' lobby
     * and does it
     * @param:playerId : id given to the player, used as the index for the players arrayList
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
        int cardScore = playedCard.getPriority();
        currentTurnAssistantCards.put(playerId,playedCard);
        newDeck.remove(cardId);
        players.get(playerId).setDeck(newDeck);
        return cardScore;
    }

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
        Island dest = islands.get(from.getIslandIndex() + numSteps % islands.size());

        from.setMotherNature(false);
        dest.setMotherNature(true);
        return true;
    }

    /**
     * Method moveStudentFromLobby checks  if the player with Player ID can move the student in his lobby to an island or
     *  the corresponding table. If it's doable the student is moved from the lobby to the table/island. If the island id is -1
     *  (ISLAND_ID_NOT_RECEIVED) the student is moved to the table. Otherwise the student is moved to the island
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
            }else{
                player.getBoard().addTeacher(c);
                teachersOwners.put(c, player);
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
     * Method refillClouds refills all the clouds in the clouds ArrayList
     *
     */
    public void refillClouds(){
        int numOfPicks = players.size()+1;
        ArrayList<Color> picks = new ArrayList<>();
        for (Cloud cloud: clouds){
            for(int i= 0; i< numOfPicks;i++){
                picks.add(basket.pickStudent());
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
    public HashMap<String,Number> calculateInfluence(Island island) {
        int max_infl = 0, infl = 0;
        short isDraw = 0;
        Player owner = island.getOwner();
        HashMap<String, Number> returnMap = new HashMap<>();

        for (Player p : players) {
            infl = 0;
            for (Color t : p.getBoard().getTeacherTable()) {
                infl += island.getStudentsOfColor(t).size();
                if (island.getTowers().size() > 0)
                    if (island.getOwnerTeam().equals(p.getTeam()))
                        infl += island.getTowers().size();
            }
            if (infl > max_infl) {
                max_infl = infl;
                owner = p;
                isDraw = 0;
            } else if (infl == max_infl)
                isDraw = 1;
        }

        if (!owner.equals(island.getOwner())&& isDraw==0)
            island.setOwner(owner);

            returnMap.put("ID Player", owner.getPlayerId());
            returnMap.put("Is Draw", isDraw);
            return returnMap;
    }
    /**
     * Method lastRound sets the relative boolean flag to true or false, according to the game's state.
     * @param lastRound : flag representative of the satisfaction of two gameover's conditions.
     */
    public void setLastRound(boolean lastRound) {
        this.lastRound = lastRound;
    }

    public ArrayList<Color> getTeachers() {
        return teachers;
    }

    public ArrayList<Island> getIslands() {
        return islands;
    }

    public ArrayList<Cloud> getClouds() {
        return clouds;
    }

    public Basket getBasket() {
        return basket;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public HashMap<Integer,Assistant> getCurrentTurnAssistantCards(){
        return currentTurnAssistantCards;
    }

    public ArrayList<Integer> GetEmptyCloudsID(){
        ArrayList<Integer> emptyClouds = new ArrayList<>();
        for(Cloud cloud : clouds){
            if(cloud.getStudents().isEmpty())
                emptyClouds.add(cloud.getCloudIndex());
        }
        return emptyClouds;
    }

    public ArrayList<Assistant> getPlayableAssistantCards(int playerId){return players.get(playerId).getDeck();}

    public void setCurrentMotherNatureIsland(Island currentMotherNatureIsland) {
        this.currentMotherNatureIsland = currentMotherNatureIsland;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }

    public ArrayList<Assistant> getAssistantDecks() {
        return assistantDecks;
    }


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
            }
        }
        return false;

    }

    public Player getPlayerByName(String name){
        for (Player player: players){
            if(player.getNickname().equalsIgnoreCase(name))
                return player;
        }
        return null;
    }
}






