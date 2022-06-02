package it.polimi.ingsw.GUI;

import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;

public class ActionParser {
    //a differenza di InputParser non ci serve lo stato perchè le chiamate vengono fatte
    //Da controller diversi e non ci serve wrappare tutto in un metodo switch

    //riceve oggetti e li elabora per far arrivare a client i parametri con cui costruire i messaggi
    //avere metodi separati e indipendenti ci permette di passar ein ingresso cose differenti

    public ArrayList<Object> parseNickname(String nickname){
        ArrayList<Object> data = new ArrayList<>();
        data.add(nickname);
        return  data;
    }

    public ArrayList<Object> parseNewGameParameters(int numOfPlayers, boolean expertGame){
        ArrayList<Object> data = new ArrayList<>();
        data.add(numOfPlayers);
        data.add(expertGame);
        return data;
    }

    public ArrayList<Object> parseWizardChoice(int wizard){
        ArrayList<Object> data = new ArrayList<>();
        data.add(wizard);
        return data;
    }

    public ArrayList<Object> parseTowerChoice(Tower tower){
        ArrayList<Object> data = new ArrayList<>();
        data.add(tower);
        return data;
    }

}
