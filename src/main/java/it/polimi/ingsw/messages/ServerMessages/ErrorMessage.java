package it.polimi.ingsw.messages.ServerMessages;

import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.ServerMessages.ErrorKind;

public class ErrorMessage implements Message {
    private final ErrorKind errorType;

    public ErrorMessage(ErrorKind errorType){
        this.errorType = errorType;
    }

    public ErrorKind getErrorType() {
        return errorType;
    }
}
