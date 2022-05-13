package it.polimi.ingsw.client;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.GameParametersMessage;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Game;

import java.util.ArrayList;

public class InputParser {
    ArrayList<Object> data;
    GameBoard GB;

    public InputParser(GameBoard GB){
        this.GB = GB;
    }
    public ArrayList<Object> parse(String input, ClientState state) throws NumberFormatException{
        data = new ArrayList<>();
        switch(state){
            case CONNECT_STATE:
                parseConnectString(input);
            case INSERT_NEW_GAME_PARAMETERS:
                parseNewGameParametersString(input);
            case SET_UP_PHASE:
                parseSetUpPhaseString(input);
        }
        return data;
    }


    private void parseConnectString(String input){
        if(input.length() <= 16 && !input.equals("") && !input.equals(" "))
            data.add(input);}

    private void parseNewGameParametersString(String input){ //gestiamo base/expert case insensitive e consideriamo separatore spazio
        int numberOfPlayers = 0;
        boolean expert = false;
        String[] words = input.split(" ");

        if(words.length == 2){
            if(words[0].equals("2")|| words[0].equalsIgnoreCase("3")){
                if(words[1].equalsIgnoreCase("base") || words[1].equalsIgnoreCase("expert")){
                    numberOfPlayers = Integer.parseInt(words[0]);
                    expert = words[1].equalsIgnoreCase("expert");
                    data.add(numberOfPlayers);
                    data.add(expert);
                }
            }
        }
    }

    private void parseSetUpPhaseString(String input){
        int deckID = 0;

        try{
            deckID = Integer.parseInt(input);
            if(GB.getAvailableWizards().contains(deckID))
                data.add(deckID);
        }catch(NumberFormatException e){

        }
    }

}
