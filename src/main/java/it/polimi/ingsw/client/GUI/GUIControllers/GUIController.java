package it.polimi.ingsw.client.GUI.GUIControllers;

import it.polimi.ingsw.client.GUI.ActionParser;
import it.polimi.ingsw.client.GUI.GUI;
import it.polimi.ingsw.client.ClientMessageBuilder;

public class GUIController implements GUIControllerInterface {
    protected GUI gui; //main class
    protected ActionParser actionParser; //array of raw data builder
    protected ClientMessageBuilder clientMessageBuilder; //message builder

    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void setClient(ClientMessageBuilder clientMessageBuilder) {
        this.clientMessageBuilder = clientMessageBuilder;
    }

    @Override
    public void setActionParser(ActionParser actionParser){this.actionParser = actionParser;}

    @Override
    public void handleErrorMessage(boolean fromServer) {}
}
