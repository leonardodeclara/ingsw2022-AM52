package it.polimi.ingsw.messages.ServerMessages;

import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.ServerMessages.ErrorKind;

/**
 * This message is sent from Server to Client every time there's an error
 */

public class ErrorMessage implements Message {
    private final ErrorKind errorType;

    /**
     * @param errorType Type of the error
     */

    public ErrorMessage(ErrorKind errorType){
        this.errorType = errorType;
    }

    public ErrorKind getErrorType() {
        return errorType;
    }
}
