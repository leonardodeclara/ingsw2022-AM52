package it.polimi.ingsw.GUI;

import javafx.scene.control.TextField;

public class GUIController implements GUIControllerInterface{
    GUI gui;

    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }
}
