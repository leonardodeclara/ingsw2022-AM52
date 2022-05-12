package it.polimi.ingsw.messages;

import java.util.HashMap;

public class AssistantDeckUpdateMessage implements Message{
    private String owner;
    private HashMap<Integer, Integer> cards;

    public AssistantDeckUpdateMessage(String owner, HashMap<Integer, Integer> cards) {
        this.owner = owner;
        this.cards = new HashMap<>(cards);
    }

    public String getOwner() {
        return owner;
    }

    public HashMap<Integer, Integer> getCards() {
        return cards;
    }
}
