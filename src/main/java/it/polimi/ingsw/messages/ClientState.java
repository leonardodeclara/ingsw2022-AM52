package it.polimi.ingsw.messages;

public enum ClientState {
    CONNECT_STATE(0),INSERT_NEW_GAME_PARAMETERS(1),WAIT_IN_LOBBY(2)
    ,WAIT_TURN(3),SET_UP_PHASE(4),PLAY_ASSISTANT_CARD(5),
    MOVE_FROM_LOBBY(6);



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
