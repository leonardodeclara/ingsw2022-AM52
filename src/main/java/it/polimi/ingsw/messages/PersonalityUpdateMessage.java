package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.model.Color;

import java.util.ArrayList;

public class PersonalityUpdateMessage implements UpdateMessage{
    private int cardId;
    private ArrayList<Color> students;
    private int bans;

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
