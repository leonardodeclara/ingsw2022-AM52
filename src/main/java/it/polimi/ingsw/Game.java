package it.polimi.ingsw;

import java.sql.Array;
import java.util.*;

/**
 * This class contains the majority of the game's elements and logic.
 */

public class Game {
    private static final int MAX_NUM_ISLANDS = 12;
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

    //si potrebbe inserire parte del suo funzionamento all'interno del costruttore in modo da alleggerirlo
    //ad esempio: monete, divieti (solo nel costruttore di expert game),
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
            player.getBoard().setTowers(players.size()==2? 6:8);


    }

    //pescata in automatico di 7 pedine se 2 player, 9 pedine se 3, ma in teoria dovrebbe essere il giocatore a pescarne una alla volta
    //rivedere se cambiare o va bene così
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

    //input: a player and its deck number
    //gives him the relative cards
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

    //rimuove dalle carte del giocatore quella che ha appena giocata, la aggiunge in quelle giocate nel turno corrente
    //e returna la priority (cardScore)
    public int playAssistantCard(int playerId,int cardId){
        ArrayList<Assistant> newDeck = players.get(playerId).getDeck();
        Assistant playedCard = newDeck.get(cardId);
        int cardScore = playedCard.getPriority();
        currentTurnAssistantCards.put(playerId,playedCard);
        newDeck.remove(cardId);
        players.get(playerId).setDeck(newDeck);
        return cardScore;
    }

    //chiamato nello step 1 della fase azione
    public boolean moveMotherNature(int playerId,int numSteps){
        if(!isMoveMNLegal(playerId,numSteps))
            return false;

        Island from = islands.get(currentMotherNatureIsland.getIslandIndex());
        Island dest = islands.get(from.getIslandIndex() + numSteps % islands.size());

        //manca il ricalcolo dell'influenza

        from.setMotherNature(false);
        dest.setMotherNature(true);
        return true;
    }

    //chiamato dal Controller nello step 2 della fase Azione
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
    //chiamato dal Controller nello step 3 della fase Azione
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

    public boolean isMoveMNLegal(int playerId,int numSteps){
        int playerMaxSteps = currentTurnAssistantCards.get(playerId).getNumMoves();
        return numSteps > playerMaxSteps ? false : true;
    }

    public boolean isMoveStudentFromLobbyLegal(Player player,int studentIndex,int islandId){
        Color studentToMove = player.getBoard().getLobbyStudent(studentIndex);
        if(studentToMove != null){
            if(islandId == -1){
                if(!player.getBoard().isTableFull(studentToMove))
                    return true;
            }
            else
            {
                if(islandId >= 0 && islandId <= islands.size())
                    return true;
            }
        }
        return false;
    }

    public boolean isMoveStudentsToLobbyLegal(Player player,int cloudId){
        if (cloudId >= 0 && cloudId <= clouds.size()-1)
            return (!clouds.get(cloudId).getStudents().isEmpty()) ? true : false;
        return false;
    }

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



    //se ci sono due valori uguali => non accade nulla
    //calculateInfluence deve segnalare se c'è stata parità oppure no al Controller


    //returniamo isDraw e id giocatore (sarebbe meglio String, ma a quel punto non ci sarebbe modo di comunicare
    //al controller che il giocatore returnato è sempre lo stesso e quindi che non deve far mettere torri a nessuno)
    public HashMap<String,Number> calculateInfluence(Island island) {
        int max_infl = 0, infl = 0;
        short isDraw = 0;
        Player owner = island.getOwner();
        HashMap<String, Number> returnMap = new HashMap<>();

        for (Player p : players) {
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

        if (!owner.equals(island.getOwner()))
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
}



