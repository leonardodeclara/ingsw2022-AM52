package it.polimi.ingsw.client;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.exceptions.QuitException;
import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.GameParametersMessage;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Tower;

import java.sql.Array;
import java.util.ArrayList;

public class InputParser {
    ArrayList<Object> data;
    String nickname;


    public InputParser(){
    }

    public ArrayList<Object> parse(String input, ClientState state) throws NumberFormatException{
        if (parseQuitInput(input))
            throw new QuitException(); //creata eccezione ad hoc
        data = new ArrayList<>();
        switch(state){
            case CONNECT_STATE:
                parseConnectString(input);
                break;
            case INSERT_NEW_GAME_PARAMETERS:
                parseNewGameParametersString(input);
                break;
            case WAIT_IN_LOBBY:
            case WAIT_TURN:
                return data;
            case SET_UP_WIZARD_PHASE:
                parseSetUpWizardPhaseString(input);
                break;
            case SET_UP_TOWER_PHASE:
                parseSetUpTowerPhaseString(input);
                break;
            case PLAY_ASSISTANT_CARD:
                parseAssistantCardString(input);
                break;
            case MOVE_FROM_LOBBY:
                parseMoveStudentsFromLobby(input);
                break;
        }
        return data;
    }


    private void parseConnectString(String input){
        if(input.length() <= 16 && !input.equals("") && !input.equals(" ")){
            data.add(input);
            nickname = input; //salva il nickname così poi lo passiamo alla CLI quando ci arriva conferma dal server che va bene
        }

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
            if(words[0].equalsIgnoreCase("play")&& words[1].equalsIgnoreCase("card")){
                try{
                    cardID = Integer.parseInt(words[2]);
                    if(cardID >= 1 && cardID <= 10)
                        data.add(cardID);
                }catch(NumberFormatException e){

                }
            }
        }
    }

    private void parseMoveStudentsFromLobby(String input){ //move studentID1,studentID2,studentID3 in table,2,3
        String[] words = input.split(" ");

        if(words.length==4){
            if(words[0].equalsIgnoreCase("move")){
                String[] studentIDs = words[1].split(",");
                ArrayList<Integer> studentIDsInteger = convertStringsToNumberArray(studentIDs);
                if(studentIDsInteger!=null){
                    if(words[2].equalsIgnoreCase("in")){
                        String[] destIDs = words[3].split(",");
                        ArrayList<Integer> destIDsInteger = convertStringsToNumberArray(destIDs);
                        if(destIDsInteger!=null){
                            data.add(studentIDsInteger);
                            data.add(destIDsInteger);
                        }
                    }
                }
            }
        }
    }


    private ArrayList<Integer> convertStringsToNumberArray(String[] array){
        ArrayList<Integer> arrayInt = new ArrayList<>();
        for(String s : array){
            try{
                if(s.equalsIgnoreCase("table"))
                    arrayInt.add(Constants.ISLAND_ID_NOT_RECEIVED);
                else{
                    Integer.parseInt(s);
                    arrayInt.add(Integer.parseInt(s));
                }
            }catch(NumberFormatException e){
                return null;
            }
        }
        return arrayInt;
    }

    private Integer isStringNumber(String s){
        try{
            return Integer.parseInt(s);
        }catch(NumberFormatException e){
            return -1;
        }
    }

    private boolean parseQuitInput(String input){
        if (input.equalsIgnoreCase("quit")){
            System.out.println("Ha scritto quit!");
            return true;
        }
        return false;
    }

    public String getNickname() {
        return nickname;
    }

}
