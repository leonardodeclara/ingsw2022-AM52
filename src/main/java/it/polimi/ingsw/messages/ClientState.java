package it.polimi.ingsw.messages;

import java.util.Arrays;
import java.util.Optional;

public enum ClientState {
    CONNECT_STATE(0),INSERT_NEW_GAME_PARAMETERS(0),WAIT_IN_LOBBY(0)
    ,WAIT_TURN(0),SET_UP_WIZARD_PHASE(0),SET_UP_TOWER_PHASE(0),PLAY_ASSISTANT_CARD(0),
    MOVE_FROM_LOBBY(0),MOVE_MOTHER_NATURE(0), PICK_CLOUD(0), END_TURN(0),CHOOSE_STUDENT_FOR_CARD_1(1),CHOOSE_ISLAND_FOR_CARD_3(3),
    CHOOSE_ISLAND_FOR_CARD_5(5),SWAP_STUDENTS_FOR_CARD_7(7),CHOOSE_COLOR_FOR_CARD_9(9),CHOOSE_STUDENTS_FOR_CARD_10(10),
    CHOOSE_STUDENT_FOR_CARD_11(11),CHOOSE_COLOR_FOR_CARD_12(12), END_GAME(0);



    private final int optionalID;

    /**
     * Constructor creates a ClientState.
     * @param optionalID: ID given to the state: IDs different from zero are relative to expert mode.
     */
    ClientState(int optionalID) {
        this.optionalID=optionalID;
    }

    /**
     * @return the optional ID of the state.
     */
    public int getOptionalID() {
        return optionalID;
    }


    public static Optional<ClientState> valueOf(int optionalID){
        return Arrays.stream(values())
                .filter(clientState -> clientState.optionalID == optionalID)
                .findFirst();
    }
}
