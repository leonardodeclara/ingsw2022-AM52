package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Color;

public class Card1EffectMessage implements Message{

    Color Student;
    int IslandID;

    public Card1EffectMessage(Color student, Integer islandID) {
        Student = student;
        IslandID = islandID;
    }

    public Color getStudent() {
        return Student;
    }

    public Integer getIslandID() {
        return IslandID;
    }
}
