package it.polimi.ingsw.client.GUI.GUIControllers;

import it.polimi.ingsw.client.GUI.ActionParser;
import it.polimi.ingsw.client.GUI.GUI;
import it.polimi.ingsw.client.ClientMessageBuilder;

public interface GUIControllerInterface {
    public void setGUI(GUI gui);
    public void setClient(ClientMessageBuilder clientMessageBuilder);
    public void setActionParser(ActionParser actionParser);
    public void handleErrorMessage(boolean fromServer);
}
