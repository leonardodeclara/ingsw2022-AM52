package it.polimi.ingsw.client;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.exceptions.EndGameException;
import it.polimi.ingsw.exceptions.QuitException;
import it.polimi.ingsw.messages.ClientState;
import it.polimi.ingsw.messages.EndGameMessage;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;

public class InputParser {
    private ArrayList<Object> data;
    private String nickname;
    private boolean isExpert;
    private int numOfPlayers;

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
            case CHOOSE_STUDENT_FOR_CARD_1:
                parsePersonality1(input);
                break;
            case CHOOSE_ISLAND_FOR_CARD_3:
                parsePersonality3(input);
                break;
            case CHOOSE_ISLAND_FOR_CARD_5:
                parsePersonality5(input);
                break;
            case SWAP_STUDENTS_FOR_CARD_7:
                parsePersonality7(input);
                break;
            case CHOOSE_COLOR_FOR_CARD_9:
                parsePersonality9(input);
                break;
            case CHOOSE_STUDENTS_FOR_CARD_10:
                parsePersonality10(input);
                break;
            case CHOOSE_STUDENT_FOR_CARD_11:
                parsePersonality11(input);
                break;
            case CHOOSE_COLOR_FOR_CARD_12:
                parsePersonality12(input);
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
                    this.numOfPlayers=numberOfPlayers;
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
            data.add(chosenDeckID);
        }catch(NumberFormatException e){
            e.printStackTrace();
        }
    }

    private void parseSetUpTowerPhaseString(String input){
        input = input.replaceAll("\s","");
        if (input.equalsIgnoreCase("grey"))
            data.add(Tower.GREY);
        else if (input.equalsIgnoreCase("black"))
            data.add(Tower.BLACK);
        else if (input.equalsIgnoreCase("white"))
            data.add(Tower.WHITE);
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
                    e.printStackTrace();
                }
            }
        }
    }
    //farei scrivere 1,2,3 ai giocatori per scegliere le pedine con indice 0,1,2
    //quindi ho modificato il metodo
    private void parseMoveStudentsFromLobby(String input){ //move studentID1,studentID2,studentID3 in table,2,3
        String[] words = input.split("\\s+");

        if(words.length==4){
            if(words[0].equalsIgnoreCase("move")){
                String[] studentIDs = words[1].split(",");
                ArrayList<Integer> studentIDsInteger = decrementIndexes(convertStringsToNumberArray(studentIDs));
                if(studentIDsInteger!=null && studentIDsInteger.size()==numOfPlayers+1){
                    if(words[2].equalsIgnoreCase("in")){
                        String[] destIDs = words[3].split(",");
                        ArrayList<Integer> destIDsInteger = convertStringsToNumberArray(destIDs);
                        if(destIDsInteger!=null && destIDsInteger.size()==numOfPlayers+1){
                            if(destIDsInteger.size() == studentIDsInteger.size()){
                                data.add(studentIDsInteger);
                                data.add(destIDsInteger);
                            }
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
            if(words[0].equalsIgnoreCase("move")){
                if(words[1].equalsIgnoreCase("mn") || words[1].equalsIgnoreCase("mothernature")){
                    try{
                        steps = Integer.parseInt(words[2]);
                        data.add(steps);
                        return;
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                }
            }
        }

        if(isExpert)
            parsePlayPersonalityCard(words);
    }


    private void parseCloudSelection(String input){
        int cloudIndex = 0;
        String[] words = input.split("\\s+");
        if (words.length==3 ){
            if (words[0].equalsIgnoreCase("empty") && words[1].equalsIgnoreCase("cloud"))
                try{
                    cloudIndex=Integer.parseInt(words[2]);
                    data.add(cloudIndex);
                    return;
                } catch (NumberFormatException e){
                    e.printStackTrace();
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
                }catch(NumberFormatException e){
                    e.printStackTrace();
                }
        }
    }

    private void parsePersonality1(String input){
        int studentIndex = 0, islandId = 0;
        String[] words = input.split("\\s+");
        if(words.length == 4){
            if(words[0].equalsIgnoreCase("move") && words[2].equalsIgnoreCase("in")){
                try{
                    studentIndex = Integer.parseInt(words[1])-1;
                    islandId= Integer.parseInt(words[3]);
                    data.add(studentIndex);
                    data.add(islandId);
                    return;
                }
                catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void parsePersonality3(String input){
        int islandId = 0;
        String[] words = input.split("\\s+");
        if(words.length == 3){
            if(words[0].equalsIgnoreCase("influence") && words[1].equals("on")){
                try{
                    islandId = Integer.parseInt(words[2]);
                    data.add(islandId);
                }
                catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void parsePersonality5(String input){
        int islandId = 0;
        String[] words = input.split("\\s+");
        if(words.length == 2){
            if(words[0].equalsIgnoreCase("ban")){
                try{
                    islandId = Integer.parseInt(words[1]);
                    data.add(islandId);
                }
                catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void parsePersonality7(String input){ //swap 1,2,3 (card) with 4,5,6 (lobby)
        String[] words = input.split("\\s+");

        if(words.length==4){
            if(words[0].equalsIgnoreCase("swap")){
                String[] cardIDs = words[1].split(",");
                ArrayList<Integer> cardIDsInteger =  decrementIndexes(convertStringsToNumberArray(cardIDs));
                if(cardIDsInteger!=null){
                    if(words[2].equalsIgnoreCase("with")){
                        String[] lobbyIDs = words[3].split(",");
                        ArrayList<Integer> lobbyIDsInteger = decrementIndexes(convertStringsToNumberArray(lobbyIDs));
                        if(lobbyIDsInteger!=null){
                            if(cardIDsInteger.size() == lobbyIDsInteger.size()){
                                data.add(cardIDsInteger);
                                data.add(lobbyIDsInteger);
                        }
                    }
                }
            }
        }
    }
    }

    private void parsePersonality9(String input){
        String[] words = input.split("\\s+");
        if(words.length == 2){
            if(words[0].equalsIgnoreCase("ban")){
                if (words[1].equalsIgnoreCase("pink"))
                    data.add(Color.PINK);
                else if (words[1].equalsIgnoreCase("red"))
                    data.add(Color.RED);
                else if (words[1].equalsIgnoreCase("blue")||words[1].equalsIgnoreCase("blu"))
                    data.add(Color.BLUE);
                else if (words[1].equalsIgnoreCase("green"))
                    data.add(Color.GREEN);
                else if (words[1].equalsIgnoreCase("yellow"))
                    data.add(Color.YELLOW);
            }
        }
    }

    private void parsePersonality10(String input){ //swap red,green (table) with 1,2 (lobby)
        String[] words = input.split("\\s+");

        if(words.length==4){
            if(words[0].equalsIgnoreCase("swap")){
                String[] colorsString = words[1].split(",");
                ArrayList<Color> colors = convertStringsToColorArray(colorsString);
                if(colors!=null){
                    if(words[2].equalsIgnoreCase("with")){
                        String[] lobbyIDs = words[3].split(",");
                        ArrayList<Integer> lobbyIDsInteger =  decrementIndexes(convertStringsToNumberArray(lobbyIDs));
                        if(lobbyIDsInteger!=null){
                            if(lobbyIDsInteger.size() == colors.size()){
                                data.add(colors);
                                data.add(lobbyIDsInteger);
                            }
                        }
                    }
                }
            }
        }
    }

    private ArrayList<Color> convertStringsToColorArray(String[] array){
        ArrayList<Color> returnArray = new ArrayList<>();

        for(String name : array){
            if (name.equalsIgnoreCase("pink"))
                returnArray.add(Color.PINK);
            else if (name.equalsIgnoreCase("red"))
                returnArray.add(Color.RED);
            else if (name.equalsIgnoreCase("blue"))
                returnArray.add(Color.BLUE);
            else if (name.equalsIgnoreCase("green"))
                returnArray.add(Color.GREEN);
            else if (name.equalsIgnoreCase("yellow"))
                returnArray.add(Color.YELLOW);
            else
                return null;
        }
        return returnArray;
    }
    private void parsePersonality11(String input){
        int studentIndex = 0;
        String[] words = input.split("\\s+");
        if(words.length == 2){
            if(words[0].equalsIgnoreCase("move")){
                try{
                    studentIndex = Integer.parseInt(words[1])-1;
                    data.add(studentIndex);
                    return;
                }
                catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void parsePersonality12(String input){
        String[] words = input.split("\\s+");
        if(words.length == 2){
            if(words[0].equalsIgnoreCase("steal")){
                if (words[1].equalsIgnoreCase("pink"))
                    data.add(Color.PINK);
                else if (words[1].equalsIgnoreCase("red"))
                    data.add(Color.RED);
                else if (words[1].equalsIgnoreCase("blue") || words[1].equalsIgnoreCase("blu"))
                    data.add(Color.BLUE);
                else if (words[1].equalsIgnoreCase("green"))
                    data.add(Color.GREEN);
                else if (words[1].equalsIgnoreCase("yellow"))
                    data.add(Color.YELLOW);
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
            }
            catch(NumberFormatException e){
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
    //utility function per passare da indici human readable (da 1 in poi) a indici computer readable (da 0 in poi)
    private ArrayList<Integer> decrementIndexes(ArrayList<Integer> indexes){
        if (indexes==null) return null;
        ArrayList<Integer> decrementedIndexes = new ArrayList<>();
        for (int index: indexes){
            decrementedIndexes.add(index-1);
            System.out.println("indice aggiornato in modo che lo legga il server: "+ (index-1));
        }
        return decrementedIndexes;
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
