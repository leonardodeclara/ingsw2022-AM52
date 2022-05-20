package it.polimi.ingsw.GUI;

import it.polimi.ingsw.messages.Message;

import java.util.ArrayList;

public interface UI {

    public void handleMessageFromServer(Message receivedMessage);

    public void prepareView(ArrayList<Object> data);
}
