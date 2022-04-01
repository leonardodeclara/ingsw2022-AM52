package it.polimi.ingsw;

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
    private Player currentPlayer;
    private ArrayList<Island> islands;
    private ArrayList<Cloud> clouds;
    private ArrayList<Teacher> teachers;
    private ArrayList<Assistant> assistantDecks;
    private Island currentMotherNatureIsland;
    private boolean motherNature;
    private boolean lastRound;

    public Game() {
        this.basket = null;
    }

    //si potrebbe inserire parte del suo funzionamento all'interno del costruttore in modo da alleggerirlo
    //ad esempio: monete, divieti (solo nel costruttore di expert game),
    public void instantiateGameElements(){
        //istanziati i professori al tavolo di gioco
        for (Color color: Color.values()){
            teachers.add(new Teacher(color));
        }


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


    //pescata in automatico di 7 pedine, ma in teoria dovrebbe essere il giocatore a pescarne una alla volta
    //rivedere se cambiare o va bene così
    public void initiatePlayerLobby(int playerId){
            for(int k = 0; k < 7; k++)
                players.get(playerId).getBoard().addToLobby(basket.pickStudent());
    }

    public void fillIslands(){
        for (Island island: islands){
            if (!island.isMotherNature() && !(island.getIslandIndex() == 6 + currentMotherNatureIsland.getIslandIndex())){
                island.addStudent(basket.pickStudent());
                island.addStudent(basket.pickStudent());
            }

        }
    }

    public void giveAssistantDeck(int playerId, int deckId){
        ArrayList<Assistant> assignedDeck = new ArrayList<>();
        for (Assistant assistant: assistantDecks){
            if (assistant.getWizard()==deckId)
                assignedDeck.add(assistant);
        }
        players.get(playerId).setDeck(assignedDeck);
    }
}
