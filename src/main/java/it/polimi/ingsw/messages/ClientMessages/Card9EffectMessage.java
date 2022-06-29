package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Color;

/**
 * This message is sent after the player has used personality card 9
 */

public class Card9EffectMessage implements Message {

    Color banned;

    /**
     * @param banned is the color that is not considered in the nifluence calculations
     */
    public Card9EffectMessage(Color banned) {
        this.banned = banned;
    }

    public Color getBanned() {
        return banned;
    }
}
