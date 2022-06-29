package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;
import it.polimi.ingsw.model.Color;

import java.util.ArrayList;

/**
 * This message is going to be broadcast to all players in game
 * after updates about a personality card usage
 */

public class PersonalityUpdateMessage implements UpdateMessage {
    private int cardId;
    private ArrayList<Color> students;
    private int bans;

    /**
     * @param cardId ID of the Personality card that has been updated
     * @param students ArrayList of students added on a Personality card or removed from its
     */
    public PersonalityUpdateMessage(int cardId, ArrayList<Color> students) {
        this.cardId=cardId;
        this.students = students;
        this.bans=-1;
    }

    public PersonalityUpdateMessage(int cardId, int bans) {
        this.cardId=cardId;
        this.bans=bans;
        students=null;
    }

    public int getCardId() {
        return cardId;
    }

    public ArrayList<Color> getStudents() {
        return students;
    }

    public int getBans() {
        return bans;
    }

    @Override
    public void update(GameBoard GB) {
        GB.updatePersonality(cardId,students,bans);
        GB.print();
    }
}
