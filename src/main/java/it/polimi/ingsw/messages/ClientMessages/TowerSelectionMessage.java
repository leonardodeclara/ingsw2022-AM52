package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Tower;

/**
 * This message is sent from Client to Server to notify
 * which tower has been chosen by the player
 */

public class TowerSelectionMessage implements Message {
    Tower tower;

    /**
     * @param tower color of tower choosen by the player
     */

    public TowerSelectionMessage(Tower tower) {
        this.tower = tower;
    }

    public Tower getTower(){
        return tower;
    }
}
