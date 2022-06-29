package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;
import static it.polimi.ingsw.Constants.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class ActionParser handles players' input received as a result of clicks on GUI elements.
 * According to the client state and the clicked element it performs different actions, from input validation to selections registry.
 * Clicked elements are memorised inside parameters attribute, from which a Message to server will be built.
 */
public class ActionParser {
    private HashMap<ClientState,ArrayList<Clickable>> stateToClickableList;
    private ArrayList<Object> parameters;
    private int playersNumber;

    /**
     * Constructor ActionParser creates a new ActionParser instance. A HashMap containing the list of Clickable elements for each client state is added to the new instance.
     */
    public ActionParser(){
        stateToClickableList = new HashMap<>();
        for(ClientState state : ClientState.values()){
            stateToClickableList.put(state,state.getClickableList());
        }
        parameters= new ArrayList<>();
    }

    /**
     * Method canClick verifies if the Clickable element received as input can be clicked according to current client state.
     * @param state ClientState instance defining the actions that can be performed by the client.
     * @param clickedElement Clickable element selected by the client through a click.
     * @return true if the element can be clicked on, false otherwise.
     */
    public boolean canClick(ClientState state,Clickable clickedElement){
        try{
            return stateToClickableList.get(state).contains(clickedElement);
        }
        catch(NullPointerException e){
            return false;
        }
    }

    /**
     * Method canDrag verifies if the Clickable element received as input can be dragged from its current position according to current client state.
     * @param state ClientState instance defining the actions that can be performed by the client.
     * @param clickedElement Clickable element selected by the client through a click.
     * @return true if the element can be clicked on, false otherwise.
     */
    public boolean canDrag(ClientState state, Clickable clickedElement){
        if (canClick(state, clickedElement)){
            switch (state){
                case MOVE_FROM_LOBBY -> {
                    int maxMovements = playersNumber==2?
                            MOVE_FROM_LOBBY_STUDENTS_NUMBER_FOR_2_PLAYERS:
                            MOVE_FROM_LOBBY_STUDENTS_NUMBER_FOR_3_PLAYERS;
                    if (parameters.size()==0 ||
                            (((ArrayList<Integer>) parameters.get(0)).size()<maxMovements))
                        return true;
                }
                case CHOOSE_STUDENT_FOR_CARD_1 ->{
                    if (parameters.size()==0) return true;
                }
            }
        }
        return false;
    }

    /**
     * Method handleSelectionEvent receives a list of clicked elements and the client's current state, according to which selections are memorized differently.
     * @param selection ArrayList of selected elements.
     * @param state  ClientState instance defining the actions that can be performed by the client.
     */
    public void handleSelectionEvent(ArrayList<Object> selection, ClientState state){
        switch (state){
            case MOVE_FROM_LOBBY -> {
                if ((parameters.size() == 0 || (parameters.get(0) instanceof Boolean))){
                    clearSelectedParameters();
                    ArrayList<Integer> studentIDs = new ArrayList<>();
                    studentIDs.add((Integer)selection.get(0));
                    ArrayList<Integer> studentDestinations = new ArrayList<>();
                    studentDestinations.add((Integer)selection.get(1));
                    parameters.add(0,studentIDs);
                    parameters.add(1,studentDestinations);
                } else {
                    ((ArrayList<Integer>) parameters.get(0)).add((Integer)selection.get(0));
                    ((ArrayList<Integer>) parameters.get(1)).add((Integer)selection.get(1));
                }
            }

            case CHOOSE_STUDENT_FOR_CARD_1 -> {
                if (parameters.size()>0)
                    clearSelectedParameters();
                parameters.add(selection.get(0));
                parameters.add(selection.get(1));
            }
        }

    }

    /**
     * Method handleSelectionEvent receives a clicked element and the client's current state, according to which selection is handled differently.
     * @param selection selected element.
     * @param state  ClientState instance defining the actions that can be performed by the client.
     */
    public void handleSelectionEvent(Object selection, Clickable clickedElement, ClientState state){
        if (canClick(state,clickedElement)){
            switch (state) {
                case MOVE_MOTHER_NATURE, PICK_CLOUD, END_TURN -> {
                    if (clickedElement.equals(Clickable.PERSONALITY)){
                        System.out.println("ACTION PARSER: click sulla carta personaggio,pulisco i parametri ");
                        if ((parameters.size() > 0 && (parameters.get(0) instanceof Boolean)) || parameters.size() == 0) {
                            clearSelectedParameters();
                            parameters.add(0, true);
                            parameters.add(1, selection);
                        }
                    }
                    else
                    {
                        if (parameters.size() > 0)
                            clearSelectedParameters();
                        parameters.add(selection);
                    }
                }
                case MOVE_FROM_LOBBY ->{
                    if (clickedElement.equals(Clickable.PERSONALITY)){
                        System.out.println("ACTION PARSER: click sulla carta personaggio,pulisco i parametri ");
                        if ((parameters.size() > 0 && (parameters.get(0) instanceof Boolean)) || parameters.size() == 0) {
                            clearSelectedParameters();
                            parameters.add(0, true);
                            parameters.add(1, selection);
                        }
                    }
                }
                case CHOOSE_ISLAND_FOR_CARD_3,CHOOSE_ISLAND_FOR_CARD_5,CHOOSE_STUDENT_FOR_CARD_11,CHOOSE_COLOR_FOR_CARD_9,CHOOSE_COLOR_FOR_CARD_12 -> {
                    if (parameters.size() > 0)
                        clearSelectedParameters();
                    parameters.add(selection);
                }
                case SWAP_STUDENTS_FOR_CARD_7 -> {
                    if (parameters.size()==0){
                        ArrayList<Integer> studentCardIDs = new ArrayList<>();
                        ArrayList<Integer> studentLobbyIDs = new ArrayList<>();
                        parameters.add(0,studentCardIDs);
                        parameters.add(1,studentLobbyIDs);
                    }
                    if (clickedElement.equals(Clickable.LOBBY_STUDENT)) {
                        System.out.println("ACTION PARSER: sono in stato carta 7 e ho selezionato uno studente della lobby");
                        ((ArrayList<Integer>) parameters.get(1)).add((Integer) selection);
                    }
                    else
                    {
                        System.out.println("ACTION PARSER: sono in stato carta 7 e ho selezionato uno studente della carta");
                        ((ArrayList<Integer>) parameters.get(0)).add((Integer) selection);
                    }
                }
                case CHOOSE_STUDENTS_FOR_CARD_10 -> {
                    if (parameters.size()==0){
                        ArrayList<Color> tableStudentColors = new ArrayList<>();
                        ArrayList<Integer> lobbyStudentIDs = new ArrayList<>();
                        parameters.add(0,tableStudentColors);
                        parameters.add(1,lobbyStudentIDs);
                    }
                    if (clickedElement.equals(Clickable.TABLE_STUDENT)) {
                        System.out.println("ACTION PARSER: sono in stato carta 7 e ho selezionato uno studente della lobby");
                        ((ArrayList<Color>) parameters.get(0)).add((Color) selection);
                    }
                    else
                    {
                        System.out.println("ACTION PARSER: sono in stato carta 7 e ho selezionato uno studente della carta");
                        ((ArrayList<Integer>) parameters.get(1)).add((Integer) selection);
                    }
                }
            }
        }
        else
            System.out.println("Non puoi cliccare "+clickedElement+" se sei in "+state);

    }

    /**
     * Method clearSelectedParameters deletes clicked elements that have been memorized in order for a message to be built.
     */
    public void clearSelectedParameters(){
        parameters.clear();
    }

    /**
     * Method parseNickname receives a String representing a nickname and adds it to an ArrayList in order for a message to be built.
     * @param nickname chosen player name.
     * @return ArrayList where the player's selection is memorized.
     */
    public ArrayList<Object> parseNickname(String nickname){
        ArrayList<Object> data = new ArrayList<>();
        data.add(nickname);
        return data;
    }

    /**
     * Method parseNickname receives the player's choice for game parameters and adds them to an ArrayList in order for a message to be built.
     * @param numOfPlayers chosen number of players.
     * @param expertGame chosen game mode.
     * @return ArrayList where the player's selection is memorized.
     */
    public ArrayList<Object> parseNewGameParameters(int numOfPlayers, boolean expertGame){
        ArrayList<Object> data = new ArrayList<>();
        data.add(numOfPlayers);
        data.add(expertGame);
        return data;
    }

    /**
     * Method parseNickname receives the player's choice for a wizard deck and adds it to an ArrayList in order for a message to be built.
     * @param wizard chosen wizard deck identification number.
     * @return ArrayList where the player's selection is memorized.
     */
    public ArrayList<Object> parseWizardChoice(int wizard){
        ArrayList<Object> data = new ArrayList<>();
        data.add(wizard);
        return data;
    }

    /**
     * Method parseNickname receives the player's choice for a tower team and adds it to an ArrayList in order for a message to be built.
     * @param tower chosen tower team.
     * @return ArrayList where the player's selection is memorized.
     */
    public ArrayList<Object> parseTowerChoice(Tower tower){
        ArrayList<Object> data = new ArrayList<>();
        data.add(tower);
        return data;
    }

    /**
     * Method getParameters returns the list of elements which have been legally selected by the player.
     * @return ArrayList of selected elements.
     */
    public ArrayList<Object> getParameters() {
        return parameters;
    }

    /**
     * Method setPlayersNumber sets playersNumber attribute, a necessary element in order to properly verify players' selections.
     * @param playersNumber game's number of players.
     */
    public void setPlayersNumber(int playersNumber){
        this.playersNumber=playersNumber;
    }
}
