package it.polimi.ingsw.client;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.GameParametersMessage;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Tower;

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
            case WAIT_IN_LOBBY:
                return data;
            case WAIT_TURN:
                return data;
            case SET_UP_WIZARD_PHASE:
                parseSetUpWizardPhaseString(input);
            case SET_UP_TOWER_PHASE:
                parseSetUpTowerPhaseString(input);
            case PLAY_ASSISTANT_CARD:
                parseAssistantCardString(input);
        }
        return data;
    }


    private void parseConnectString(String input){
        if(input.length() <= 16 && !input.equals("") && !input.equals(" "))
            data.add(input);
    }

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

    private void parseSetUpWizardPhaseString(String input){
        int chosenDeckID = 0;

        try{
            chosenDeckID = Integer.parseInt(input);
            /* Questo controllo si dovrebbe fare lato server, vedere se eventualmente fare qualche controllo lato client. per ora no
            if(GB.getAvailableWizards().contains(chosenDeckID))
                data.add(chosenDeckID);
             */
            data.add(chosenDeckID);
        }catch(NumberFormatException e){
            /*
            TO DO
             */
        }
    }

    private void parseSetUpTowerPhaseString(String input){
        try{
            if (input.equalsIgnoreCase("grey"))
                data.add(Tower.GREY);
            else if (input.equalsIgnoreCase("black"))
                data.add(Tower.BLACK);
            else if (input.equalsIgnoreCase("white"))
                data.add(Tower.WHITE);
        }catch(NumberFormatException e){
        }

    }

    private void parseAssistantCardString(String input){ //il comando è play card x
        int cardID = 0; //priority
        String[] words = input.split(" ");

        if(words.length == 3){
            if(words[0].equals("play")&& words[1].equalsIgnoreCase("card")){
                cardID = Integer.parseInt(words[2]);
                if(cardID >= 1 && cardID <= 10){
                    data.add(cardID);
                }
            }
        }
    }
}
