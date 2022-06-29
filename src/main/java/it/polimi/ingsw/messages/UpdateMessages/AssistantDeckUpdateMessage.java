package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

import java.util.HashMap;

/**
 * This message is going to be broadcast to all players in game
 * when there's an update on a player's assistant deck
 */

public class AssistantDeckUpdateMessage implements UpdateMessage {
    private String owner;
    private HashMap<Integer, Integer> cards;

    /**
     * @param owner nicnkame of the deck's owner
     * @param cards cards that have been updated
     */

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

    @Override
    public void update(GameBoard GB) {
        GB.setPlayerDeck(owner, cards);
    }
}
