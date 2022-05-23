package it.polimi.ingsw.client;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.exceptions.EndGameException;
import it.polimi.ingsw.exceptions.QuitException;
import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.EndGameMessage;
import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;

public class InputParser {
    ArrayList<Object> data;
    String nickname;
    boolean isExpert;

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
            case MOVE_MOTHER_NATURE:
                parseMoveMotherNature(input);
                break;
            case PICK_CLOUD:
                parseCloudSelection(input);
                break;
            case END_TURN:
                parseClosingTurn(input);
                break;
            case END_GAME:
                parseEndGame(input);
                break;
        }
        return data;
    }


    private void parseConnectString(String input){
        input = input.replaceAll("\s","");
        if(input.length() <= 16 && !input.equals("") && !input.equals("\s")){
            data.add(input);
            nickname = input; //salva il nickname così poi lo passiamo alla CLI quando ci arriva conferma dal server che va bene
        }

    }

    private void parseNewGameParametersString(String input){ //gestiamo base/expert case insensitive e consideriamo separatore spazio
        int numberOfPlayers = 0;
        boolean expert = false;
        String[] words = input.split("\\s+");

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
        input = input.replaceAll("\s","");
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
        input = input.replaceAll("\s","");
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
        String[] words = input.split("\\s+");

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
    //
    private void parseMoveStudentsFromLobby(String input){ //move studentID1,studentID2,studentID3 in table,2,3
        String[] words = input.split("\\s+");

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
                            return;
                        }
                    }
                }
            }
        }

        if(isExpert)
            parsePlayPersonalityCard(words);

    }

    //bug: se si scrolla e poi si preme invio va a capo ma non invia. se lo si preme di nuovo invia ma a volte manda carattere vuoto
    private void parseMoveMotherNature(String input){ //comando: move mn 5
        int steps = 0;
        String[] words = input.split("\\s+");
        if(words.length==3){
            if(words[0].equalsIgnoreCase("move"))
                if(words[1].equalsIgnoreCase("mn") || words[1].equalsIgnoreCase("mothernature")){
                    try{
                        steps = Integer.parseInt(words[2]);
                        data.add(steps);
                        return;
                    }catch(NumberFormatException e){

                    }
            }
        }

        if(isExpert)
            parsePlayPersonalityCard(words);
    }


    private void parseCloudSelection(String input){ //comando empty cloud 3
        int cloudIndex = 0;
        String[] words = input.split("\\s+");
        if (words.length==3 ){
            if (words[0].equalsIgnoreCase("empty") && words[1].equalsIgnoreCase("cloud"))
                try{
                    cloudIndex=Integer.parseInt(words[2]);
                    data.add(cloudIndex);
                    return;
                } catch (NumberFormatException e){
                }
        }

        if(isExpert)
            parsePlayPersonalityCard(words);
    }

    private void parseClosingTurn(String input){
        String[] words = input.split("\\s+");
        if(words.length==1){
            if (words[0].equalsIgnoreCase("end")){
                data.add(words[0]);
                return;
            }
        }

        if(isExpert)
            parsePlayPersonalityCard(words);
    }

    private void parsePlayPersonalityCard(String[] words){
        int cardID=0;
        boolean hasPlayedPersonality = true;
        if(words.length==3){
            if(words[0].equalsIgnoreCase("play") && words[1].equalsIgnoreCase("personality"))
                try{
                    cardID = Integer.parseInt(words[2]);
                    data.add(hasPlayedPersonality); //serve per segnalare al client che nell'array c'è l'id di una carta
                    data.add(cardID);
                    return;
                }catch(NumberFormatException e){

                }
        }
    }

    private void parseEndGame(String input){
        String[] words = input.split("\\s+");
        if (words.length==1){
            if (words[0].equalsIgnoreCase("close"))
                throw new EndGameException();
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

    public boolean getIsExpert(){
        return isExpert;
    }

    public void setIsExpert(boolean isExpert){
        this.isExpert = isExpert;
    }

}
