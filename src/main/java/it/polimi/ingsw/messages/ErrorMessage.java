package it.polimi.ingsw.messages;

public class ErrorMessage implements Message{
    private final ErrorKind errorType;

    public ErrorMessage(ErrorKind errorType){
        this.errorType = errorType;
    }

    public ErrorKind getErrorType() {
        return errorType;
    }
}
