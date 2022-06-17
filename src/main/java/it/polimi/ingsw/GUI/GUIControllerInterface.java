package it.polimi.ingsw.GUI;

import it.polimi.ingsw.client.Client;

public interface GUIControllerInterface { //viene implementata da GUIController, così si possono dichiarare i metodi solo dove li si vuole nelle sottoclassi
    public void setGUI(GUI gui);
    public void setClient(Client client);
    public void setActionParser(ActionParser actionParser);
    public void handleErrorMessage(boolean fromServer);
}
