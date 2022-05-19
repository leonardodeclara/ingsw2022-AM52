package it.polimi.ingsw.messages;

public enum ClientState {
    CONNECT_STATE(0),INSERT_NEW_GAME_PARAMETERS(1),WAIT_IN_LOBBY(2)
    ,WAIT_TURN(3),SET_UP_WIZARD_PHASE(4),SET_UP_TOWER_PHASE(5),PLAY_ASSISTANT_CARD(6),
    MOVE_FROM_LOBBY(7),MOVE_MOTHER_NATURE(8), PICK_CLOUD(9), END_TURN(10);



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
