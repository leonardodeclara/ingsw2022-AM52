package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Color;

/**
 * This message is sent after the player has used personality card 12
 */

public class Card12EffectMessage implements Message {
    Color chosenColor;

    /**
     * @param chosenColor each player must puts 3 students of this color in the bag
     */
    public Card12EffectMessage(Color chosenColor) {
        this.chosenColor = chosenColor;
    }

    public Color getChosenColor() {
        return chosenColor;
    }
}
