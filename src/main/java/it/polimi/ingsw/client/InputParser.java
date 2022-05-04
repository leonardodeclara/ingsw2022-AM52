package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.GameParametersMessage;
import it.polimi.ingsw.messages.Message;

public class InputParser {
    //classe che si occupa del parsing delle stringhe scritte su CLI
    //estrae il contenuto e si occupa di chiamare i metodi necessari per l'invio di messaggi al server

    public Message parseGameParameters(String[] input) throws NumberFormatException{
        boolean mode;
        int numOfPlayers = Integer.parseInt(input[0]);
        if (input[1].equals("expert"))
            mode = true;
        else if (input[1].equals("base"))
            mode = false;
        else
            throw new IllegalArgumentException("Input non valido");
        return new GameParametersMessage(numOfPlayers, mode);
    }


}
