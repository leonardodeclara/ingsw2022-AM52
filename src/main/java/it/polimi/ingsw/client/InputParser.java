package it.polimi.ingsw.client;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.exceptions.EndGameException;
import it.polimi.ingsw.exceptions.QuitException;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;

/**
 * Class InputParser handles players' input from CLI and interprets it accordingly to the Client State.
 * The object created from it are transferred to ClientMessageBuilder in order to build a message to Server.
 */
public class InputParser {
    private ArrayList<Object> data;
    private String nickname;
    private boolean isExpert;
    private int numOfPlayers;

    public InputParser(){}

    /**
     * Method parse receives the players' input and dispatches it to the proper method in order to interpret its meaning.
     * @param input player's textual input.
     * @param state client's state information used to properly translate the textual information.
     * @return ArrayList carrying the object interpretation of the player's input.
     */
    public ArrayList<Object> parse(String input, ClientState state){
        if (parseQuitInput(input))
            throw new QuitException();
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

    /**
     * Method parseConnectString parses the player's nickname choice.
     * @param input player's textual input.
     */
    private void parseConnectString(String input){
        input = input.replaceAll("\s","");
        if(input.length() <= 16 && !input.equals("") && !input.equals("\s")){
            data.add(input);
            nickname = input;
        }

    }

    /**
     * Method parseNewGameParametersString parses the player's game parameters choice.
     * @param input player's textual input.
     */
    private void parseNewGameParametersString(String input){
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

    /**
     * Method parseSetUpWizardPhaseString parses the player's wizard choice.
     * @param input player's textual input.
     */
    private void parseSetUpWizardPhaseString(String input){
        int chosenDeckID = 0;
        input = input.replaceAll("\s","");
        try{
            chosenDeckID = Integer.parseInt(input);
            data.add(chosenDeckID);
        }catch(NumberFormatException ignored){}
    }

    /**
     * Method parseSetUpTowerPhaseString parses the player's tower choice.
     * @param input player's textual input.
     */
    private void parseSetUpTowerPhaseString(String input){
        input = input.replaceAll("\s","");
        if (input.equalsIgnoreCase("grey"))
            data.add(Tower.GREY);
        else if (input.equalsIgnoreCase("black"))
            data.add(Tower.BLACK);
        else if (input.equalsIgnoreCase("white"))
            data.add(Tower.WHITE);
    }

    /**
     * Method parseAssistantCardString parses the player's assistant card choice.
     * @param input player's textual input.
     */
    private void parseAssistantCardString(String input){
        int cardID = 0;
        String[] words = input.split("\\s+");

        if(words.length == 3){
            if(words[0].equalsIgnoreCase("play")&& words[1].equalsIgnoreCase("card")){
                try{
                    cardID = Integer.parseInt(words[2]);
                    if(cardID >= 1 && cardID <= 10)
                        data.add(cardID);
                }catch(NumberFormatException ignored){}
            }
        }
    }

    /**
     * Method parseMoveStudentsFromLobby parses the player's choice for students movement from the board's table.
     * @param input player's textual input.
     */
    private void parseMoveStudentsFromLobby(String input){
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

    /**
     * Method parseMoveMotherNature parses the player's choice for Mother Nature movement.
     * @param input player's textual input.
     */
    private void parseMoveMotherNature(String input){
        int steps = 0;
        String[] words = input.split("\\s+");
        if(words.length==3){
            if(words[0].equalsIgnoreCase("move")){
                if(words[1].equalsIgnoreCase("mn") || words[1].equalsIgnoreCase("mothernature")){
                    try{
                        steps = Integer.parseInt(words[2]);
                        data.add(steps);
                        return;
                    }catch(NumberFormatException ignored){}
                }
            }
        }
        if(isExpert)
            parsePlayPersonalityCard(words);
    }

    /**
     * Method parseCloudSelection parses the player's choice for a cloud to pick.
     * @param input player's textual input.
     */
    private void parseCloudSelection(String input){
        int cloudIndex = 0;
        String[] words = input.split("\\s+");
        if (words.length==3 ){
            if (words[0].equalsIgnoreCase("empty") && words[1].equalsIgnoreCase("cloud"))
                try{
                    cloudIndex=Integer.parseInt(words[2]);
                    data.add(cloudIndex);
                    return;
                } catch (NumberFormatException ignored){}
        }

        if(isExpert)
            parsePlayPersonalityCard(words);
    }

    /**
     * Method parseClosingTurn parses the player's input during the END_TURN phase.
     * @param input player's textual input.
     */
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

    /**
     * Method parsePlayPersonalityCard parses the player's personality choice.
     * @param words String array containing the player's personality choice.
     */
    private void parsePlayPersonalityCard(String[] words){
        int cardID=0;
        if(words.length==3){
            if(words[0].equalsIgnoreCase("play") && words[1].equalsIgnoreCase("personality"))
                try{
                    cardID = Integer.parseInt(words[2]);
                    data.add(true);
                    data.add(cardID);
                }catch(NumberFormatException ignored){}
        }
    }

    /**
     * Method parsePersonality1 parses the player's selection for the application of Personality 1's effect.
     * @param input player's textual input.
     */
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
                catch (NumberFormatException ignored){}
            }
        }
    }

    /**
     * Method parsePersonality3 parses the player's selection for the application of Personality 3's effect.
     * @param input player's textual input.
     */
    private void parsePersonality3(String input){
        int islandId = 0;
        String[] words = input.split("\\s+");
        if(words.length == 3){
            if(words[0].equalsIgnoreCase("influence") && words[1].equals("on")){
                try{
                    islandId = Integer.parseInt(words[2]);
                    data.add(islandId);
                }
                catch (NumberFormatException ignored){}
            }
        }
    }

    /**
     * Method parsePersonality5 parses the player's selection for the application of Personality 5's effect.
     * @param input player's textual input.
     */
    private void parsePersonality5(String input){
        int islandId = 0;
        String[] words = input.split("\\s+");
        if(words.length == 2){
            if(words[0].equalsIgnoreCase("ban")){
                try{
                    islandId = Integer.parseInt(words[1]);
                    data.add(islandId);
                }
                catch (NumberFormatException ignored){}
            }
        }
    }

    /**
     * Method parsePersonality7 parses the player's selection for the application of Personality 7's effect.
     * @param input player's textual input.
     */
    private void parsePersonality7(String input){
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

    /**
     * Method parsePersonality9 parses the player's selection for the application of Personality 9's effect.
     * @param input player's textual input.
     */
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

    /**
     * Method parsePersonality10 parses the player's selection for the application of Personality 10's effect.
     * @param input player's textual input.
     */
    private void parsePersonality10(String input){
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

    /**
     * Method parsePersonality11 parses the player's selection for the application of Personality 11's effect.
     * @param input player's textual input.
     */
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
                catch (NumberFormatException ignored){}
            }
        }
    }

    /**
     * Method parsePersonality12 parses the player's selection for the application of Personality 12's effect.
     * @param input player's textual input.
     */
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

    /**
     * Method parseEndGame parses the player's input during EndGame phase.
     * @param input player's textual input.
     */
    private void parseEndGame(String input){
        String[] words = input.split("\\s+");
        if (words.length==1){
            if (words[0].equalsIgnoreCase("close"))
                throw new EndGameException();
        }
    }

    /**
     * Method parseQuitInput verifies if a player is explicitly closing the game by writing a quit message.
     * @param input player's textual input.
     * @return true if the player has written "quit", false otherwise.
     */
    private boolean parseQuitInput(String input){
        if (input.equalsIgnoreCase("quit")){
            return true;
        }
        return false;
    }

    //utility function per passare da indici human readable (da 1 in poi) a indici computer readable (da 0 in poi)

    /**
     * Method decrementIndexes translates human-readable indexes (counting starts from 1) to computer-readable indexes (counting starts from 0).
     * @param indexes list of indexes that have to be decremented.
     * @return ArrayList of decremented indexes.
     */
    private ArrayList<Integer> decrementIndexes(ArrayList<Integer> indexes){
        if (indexes==null) return null;
        ArrayList<Integer> decrementedIndexes = new ArrayList<>();
        for (int index: indexes){
            decrementedIndexes.add(index-1);
        }
        return decrementedIndexes;
    }

    /**
     * Method convertStringsToNumberArray converts a string array to a number array.
     * @param array array of strings representing player's input text.
     * @return ArrayList of numbers from strings.
     */
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

    /**
     * Method convertStringsToColorArray converts an array of strings representing a Color instance to a Color instance.
     * @param array array of color names as strings.
     * @return ArrayList of Color instances representing the color names.
     */
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

    /**
     * Method getNickname returns the nickname associated to the client.
     * @return nickname of the client.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Method setIsExpert sets the boolean flag isExpert to true or false according to the game mode
     * @param isExpert true if expert mode is enabled, false otherwise.
     */
    public void setIsExpert(boolean isExpert){
        this.isExpert = isExpert;
    }

}
