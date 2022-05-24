package it.polimi.ingsw.GUI;

import it.polimi.ingsw.client.Client;
import javafx.scene.control.TextField;

public class GUIController implements GUIControllerInterface{
    GUI gui;
    Client client;

    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }
}
