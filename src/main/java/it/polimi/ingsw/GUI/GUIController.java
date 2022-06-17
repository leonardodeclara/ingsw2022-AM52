package it.polimi.ingsw.GUI;

import it.polimi.ingsw.client.Client;
import javafx.scene.control.TextField;

public class GUIController implements GUIControllerInterface{
    GUI gui; //main class
    ActionParser actionParser; //array of raw data builder
    Client client; //message builder

    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void setActionParser(ActionParser actionParser){this.actionParser = actionParser;}

    @Override
    public void handleErrorMessage(boolean fromServer) {}
}
