package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Color;

public class Card12EffectMessage implements Message {
    Color chosenColor;

    public Card12EffectMessage(Color chosenColor) {
        this.chosenColor = chosenColor;
    }

    public Color getChosenColor() {
        return chosenColor;
    }
}
