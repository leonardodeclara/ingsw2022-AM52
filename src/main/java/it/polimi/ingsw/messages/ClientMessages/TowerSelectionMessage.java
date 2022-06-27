package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Tower;

public class TowerSelectionMessage implements Message {
    Tower tower;

    public TowerSelectionMessage(Tower tower) {
        this.tower = tower;
    }

    public Tower getTower(){
        return tower;
    }
}
