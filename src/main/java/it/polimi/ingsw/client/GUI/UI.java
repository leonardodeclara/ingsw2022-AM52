package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.messages.Message;

import java.util.ArrayList;

public interface UI {

    public void handleMessageFromServer(Message receivedMessage);

    public void prepareView(ArrayList<Object> data);

    public void handleClosingServer();
}
