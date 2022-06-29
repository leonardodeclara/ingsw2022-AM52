package it.polimi.ingsw.messages.ServerMessages;

/**
 * Type of error that can be notified from Server to Client
 */

public enum ErrorKind {
    INVALID_NICKNAME(0),INVALID_INPUT(1),ILLEGAL_MOVE(2);

    private final int index;

    /**
     * @param index is the index that identifies the type of the error
     */

    ErrorKind(int index) {
        this.index=index;
    }
    public int getIndex() {
        return index;
    }
}
