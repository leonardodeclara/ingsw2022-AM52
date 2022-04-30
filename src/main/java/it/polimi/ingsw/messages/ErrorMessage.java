package it.polimi.ingsw.messages;

public class ErrorMessage extends Message{
    private final ErrorKind errorType;

    public ErrorMessage(ErrorKind errorType){
        this.errorType = errorType;
    }

    public ErrorKind getErrorType() {
        return errorType;
    }
}
