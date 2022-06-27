package it.polimi.ingsw.messages.ServerMessages;

public enum ErrorKind {
    INVALID_NICKNAME(0),INVALID_INPUT(1),ILLEGAL_MOVE(2);

    private final int index;

    ErrorKind(int index) {
        this.index=index;
    }
    public int getIndex() {
        return index;
    }
}
