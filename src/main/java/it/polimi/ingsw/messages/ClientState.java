package it.polimi.ingsw.messages;

public enum ClientState {
    CONNECT_STATE(0),INSERT_NEW_GAME_PARAMETERS(1),WAIT_IN_LOBBY(2)
    ,WAIT_TURN(3),SET_UP_WIZARD_PHASE(4),SET_UP_TOWER_PHASE(5),PLAY_ASSISTANT_CARD(6),
    MOVE_FROM_LOBBY(7),MOVE_MOTHER_NATURE(8), PICK_CLOUD(9), END_TURN(10),CHOOSE_ISLAND_FOR_CARD_3(19),
    CHOOSE_ISLAND_FOR_CARD_4(20),SWAP_STUDENTS_FOR_CARD_7(21),CHOOSE_COLOR_FOR_CARD_9(22),CHOOSE_STUDENTS_FOR_CARD_10(23),
    CHOOSE_STUDENT_FOR_CARD_11(24),CHOOSE_COLOR_FOR_CARD_12(25),CHOOSE_STUDENTS_TO_LOSE(26);



    private final int index;

    /**
     * Constructor creates a new color instance.
     * @param index: number representative for the color's index.
     */
    ClientState(int index) {
        this.index=index;
    }

    /**
     * @return the index of a specific color
     */
    public int getIndex() {
        return index;
    }

}
