package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.ExpertGame;
import it.polimi.ingsw.model.Game;

import java.util.ArrayList;
import java.util.Random;

//aggiorna model
public class GameController {
    Game game;
    String currentPlayer;
    ArrayList<String> players;

    //risoluzione stupida al problema del tipo statico di Game GM: cast esplicito in base al boolean isExpert
    public GameController(boolean isExpert) {
        game = (isExpert) ? new ExpertGame() : new Game();

    }

    public void setUpPhase(){
        //attende che il server dia conferma che i giocatori sono connessi e riceve anche l'arrayList string players
        //la passa anche a Game così può fare addPlayer
        game.instantiateGameElements();
        assignAssistantDeck();
        assignInitialStudents();
        currentPlayer = getRandomPlayer();
    }

    public void assignAssistantDeck(){

    }

    public void assignInitialStudents(){

    }

    private String getRandomPlayer(){
        Random rand = new Random();
        String randomPlayer = players.get(rand.nextInt(players.size()));
        return randomPlayer;
    }


}
