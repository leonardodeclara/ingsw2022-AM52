package it.polimi.ingsw;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class contains the majority of the game's elements and logic.
 */

public class Game {
    private static final int MAX_NUM_ISLANDS = 12;
    private Basket basket;
    private ArrayList<Player> players;
    //private ArrayList<Player> activePlayers;
    //potrebbe servire per gestire la resilienza
    private Player currentPlayer;
    private ArrayList<Island> islands;
    private ArrayList<Cloud> clouds;
    private ArrayList<Teacher> teachers;
    private ArrayList<Assistant> assistantDecks;
    private Island currentMotherNatureIsland;
    //private boolean motherNature;
    private boolean lastRound;

    public Game() {
        players = new ArrayList<>();
        islands = new ArrayList<>();
        clouds = new ArrayList<>();
        teachers = new ArrayList<>();
        assistantDecks = new ArrayList<>();
        this.lastRound = false;
    }

    /**
     * This method add an existing player to the game.
     * @param player: instance of the player which has been admitted to the game.
     */
    //bisogna gestire il lancio di questa eccezione (creare una ad hoc)
    public void addPlayer(Player player) throws Exception{
        if (players.size()<3)
            players.add(player);
        else
            throw new Exception("Superato limite di giocatori");
    }

    //si potrebbe inserire parte del suo funzionamento all'interno del costruttore in modo da alleggerirlo
    //ad esempio: monete, divieti (solo nel costruttore di expert game),
    public void instantiateGameElements(){
        //istanziati i professori al tavolo di gioco
        for (Color color: Color.values()){
            teachers.add(new Teacher(color));
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

        //per l'aggiunta delle torri bisogna conoscere il numero di giocatori e la scelta dei giocatori
        //vedere come fare


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
                island.addStudent(basket.pickStudent());
            }
        }
    }

    //input: a player and its deck number
    //gives him the relative cards
    public void giveAssistantDeck(int playerId, int deckId){
        ArrayList<Assistant> assignedDeck = new ArrayList<>();
        for (Assistant assistant: assistantDecks){
            if (assistant.getWizard()==deckId)
                assignedDeck.add(assistant);
        }
        players.get(playerId).setDeck(assignedDeck);
    }

    /**
     * Utility method used to check whether it's the game's last round or not.
     * @return true if the game will be over at the end of the current round, false if not.
     */
    public boolean isLastRound() {
        return lastRound;
    }

    /**
     * Method lastRound sets the relative boolean flag to true or false, according to the game's state.
     * @param lastRound : flag representative of the satisfaction of two gameover's conditions.
     */
    public void setLastRound(boolean lastRound) {
        this.lastRound = lastRound;
    }

    public ArrayList<Teacher> getTeachers() {
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

    public void setCurrentMotherNatureIsland(Island currentMotherNatureIsland) {
        this.currentMotherNatureIsland = currentMotherNatureIsland;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }
}
