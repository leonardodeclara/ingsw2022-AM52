package it.polimi.ingsw.messages;

public enum ClientState {
    WAIT_TURN(0),PLAY_ASSISTANT_CARD(1),MOVE_FROM_LOBBY(2),MOVE_MOTHER_NATURE(3),MOVE_TO_LOBBY(4),
    INSERT_NEW_GAME_PARAMETERS(5);

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
