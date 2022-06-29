package it.polimi.ingsw.messages.ClientMessages;

import it.polimi.ingsw.messages.Message;

import java.util.ArrayList;

/**
 * This message is sent after the player has used personality card 7
 */

public class Card7EffectMessage implements Message {
    ArrayList<Integer> studentsFromCard;
    ArrayList<Integer> studentsFromLobby;

    /**
     * @param studentsFromCard students that player wants to move from card to lobby
     * @param studentsFromLobbyPosition position in the lobby of the students that player wants to move from lobby to card
     */
    public Card7EffectMessage(ArrayList<Integer> studentsFromCard, ArrayList<Integer> studentsFromLobbyPosition) {
        this.studentsFromCard = studentsFromCard;
        this.studentsFromLobby = studentsFromLobbyPosition;
    }

    public ArrayList<Integer> getStudentsFromCard() {
        return studentsFromCard;
    }

    public ArrayList<Integer> getStudentsFromLobby() {
        return studentsFromLobby;
    }
}
