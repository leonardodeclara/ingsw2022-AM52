package it.polimi.ingsw.GUI.GUIControllers;

import it.polimi.ingsw.GUI.ActionParser;
import it.polimi.ingsw.GUI.GUI;
import it.polimi.ingsw.client.ClientMessageBuilder;

public interface GUIControllerInterface { //viene implementata da GUIController, così si possono dichiarare i metodi solo dove li si vuole nelle sottoclassi
    public void setGUI(GUI gui);
    public void setClient(ClientMessageBuilder clientMessageBuilder);
    public void setActionParser(ActionParser actionParser);
    public void handleErrorMessage(boolean fromServer);
}
