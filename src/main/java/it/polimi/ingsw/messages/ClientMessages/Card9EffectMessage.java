package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.Color;

public class Card9EffectMessage implements Message {

    Color banned;

    public Card9EffectMessage(Color banned) {
        this.banned = banned;
    }

    public Color getBanned() {
        return banned;
    }
}
