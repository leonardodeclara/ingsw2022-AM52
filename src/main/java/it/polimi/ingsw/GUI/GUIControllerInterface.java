package it.polimi.ingsw.GUI;

import it.polimi.ingsw.client.Client;

public interface GUIControllerInterface {
    public void setGUI(GUI gui);
    public void setClient(Client client);
    public void setActionParser(ActionParser actionParser);
}
