package it.polimi.ingsw.GUI;

import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.model.Tower;
import static it.polimi.ingsw.Constants.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionParser {
    private HashMap<ClientState,ArrayList<Clickable>> stateToClickableList;
    private ArrayList<Object> parameters;

    public ActionParser(){
        stateToClickableList = new HashMap<>();
        for(ClientState state : ClientState.values()){
            stateToClickableList.put(state,state.getClickableList());
        }
        parameters= new ArrayList<>();
    }

    public boolean canClick(ClientState state,Clickable clickedElement){
        try{
            return stateToClickableList.get(state).contains(clickedElement);
        }
        catch(NullPointerException e){
            return false;
        }
    }


    public boolean canDrag(ClientState state, Clickable clickedElement){
        if (canClick(state, clickedElement)){
            switch (state){
                case MOVE_FROM_LOBBY -> {
                    //alla fine significa fare un thickish client

                    if (parameters.size()==0 ||
                            (((ArrayList<Integer>) parameters.get(0)).size()<MOVE_FROM_LOBBY_STUDENTS_NUMBER))
                        return true;
                }
                case CHOOSE_STUDENT_FOR_CARD_1 ->{
                    if (parameters.size()==0) return true;
                }
            }
        }
        return false;
    }

    //metodo che si occupa di gestire l'accumulo dei dati inseriti in base allo stato del client
    //stato diverso implica modo diverso di gestire i click
    //in input prende ciò che è stato cliccato e lo stato del client
    //selection deve necessariamente essere un arrayList di Object perché certe azioni (drag and drop) restituiscono più elementi selezionati
    public void handleSelectionEvent(ArrayList<Object> selection,  ClientState state){
        //MANCA CONTROLLO CAN CLICK-CAN DRAG
        /*
        for (Clickable clickedElement: clickedElements){
            if (!canClick(state,clickedElement)){
                System.out.println("Non puoi cliccare "+clickedElement+" se sei in "+state);
                return;
            }
        }
        */
        switch (state){
            case MOVE_FROM_LOBBY -> {
                //pulisco l'array di parameters
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
                    //(ArrayList<Integer>) parameters.get(x)) può essere inserito in un metodo a parte che prende in input x
                }
            }

            case CHOOSE_STUDENT_FOR_CARD_1 -> {
                if (parameters.size()>0)
                    clearSelectedParameters();
                parameters.add(selection.get(0));
                parameters.add(selection.get(1));
            }
            case SWAP_STUDENTS_FOR_CARD_7 -> {}
            case CHOOSE_STUDENTS_FOR_CARD_10 -> {}
        }

    }

    //metodo che viene cliccato per eventi a click "singoli" (pick cloud, move mn,ecc)
    public void handleSelectionEvent(Object selection, Clickable clickedElement, ClientState state){
        /*
        switch (state){
            case PLAY_ASSISTANT_CARD, MOVE_MOTHER_NATURE -> {
                if(parameters.size()>0)
                    clearSelectedParameters();
                parameters.add(selection);
            }
            case PICK_CLOUD -> {}
            case END_TURN -> {}
            case CHOOSE_ISLAND_FOR_CARD_3 -> {}
            case CHOOSE_ISLAND_FOR_CARD_5 -> {}
            case CHOOSE_COLOR_FOR_CARD_9 -> {}
            case CHOOSE_STUDENT_FOR_CARD_11 -> {}
            case CHOOSE_COLOR_FOR_CARD_12 -> {}
        }*/
        if (canClick(state,clickedElement)){
            switch (state) {
                case MOVE_MOTHER_NATURE, PICK_CLOUD, END_TURN -> {
                    //POSSO SCEGLIERE UNA CARTA PERSONAGGIO SOLO SE NON HO GIA SCELTO QUALCOS'ALTRO (PEDINE,ISOLE,NUVOLE)
                    //PERÒ IN QUESTO MODO BISOGNA INSERIRE UN SISTEMA DI UNCLICK
                    if (clickedElement.equals(Clickable.PERSONALITY)){
                        System.out.println("ACTION PARSER: click sulla carta personaggio,pulisco i parametri ");
                        if ((parameters.size() > 0 && (parameters.get(0) instanceof Boolean)) || parameters.size() == 0) {
                            clearSelectedParameters();
                            parameters.add(0, true);
                            parameters.add(1, selection);
                        }
                    }
                    else //selezione normale di isole, o nuvole
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
                //default: CHOOSE_STUDENT_FOR_CARD_11,CHOOSE_ISLAND_FOR_CARD_3,CHOOSE_ISLAND_FOR_CARD_5
                //vedere come gestire carte 9,12
                default -> {
                    if (parameters.size() > 0)
                        clearSelectedParameters();
                    parameters.add(selection);
                }
            }
        }
        else
            System.out.println("Non puoi cliccare "+clickedElement+" se sei in "+state);

    }

    //chiamato dopo aver mandato un messaggio
    public void clearSelectedParameters(){
        parameters.clear();
    }


    public ArrayList<Object> parse(ClientState state,ArrayList<Integer> clickedIDs){
            switch(state){
                case PLAY_ASSISTANT_CARD -> {
                    return parseAssistantCard(clickedIDs);
                }
            }
            return null;
    }

    public ArrayList<Object> parseAssistantCard(ArrayList<Integer> clickedIDs){
        ArrayList<Object> data = new ArrayList<>();
        data.add(clickedIDs.get(0));
        return data;
    }

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

    public ArrayList<Object> getParameters() {
        return parameters;
    }
}
