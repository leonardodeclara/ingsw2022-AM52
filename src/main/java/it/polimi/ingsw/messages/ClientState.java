package it.polimi.ingsw.messages;

public enum ClientState {
    WAITTURN(0),PLAYASSISTANTCARD(1),MOVEFROMLOBBY(2),MOVEMOTHERNATURE(3),MOVETOLOBBY(4);

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
