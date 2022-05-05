package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Color;

public class Card10EffectMessage implements Message{

    Color student1, student2;
    int lobbyposition1, lobbyposition2;


    public Card10EffectMessage(Color student1, int lobbyposition1) {
        this.student1 = student1;
        this.lobbyposition1 = lobbyposition1;
    }



    public Card10EffectMessage(Color student1, Color student2, int lobbyposition1, int lobbyposition2) {
        this.student1 = student1;
        this.student2 = student2;
        this.lobbyposition1 = lobbyposition1;
        this.lobbyposition2 = lobbyposition2;
    }

    public Color getStudent1() {
        return student1;
    }

    public Color getStudent2() {
        return student2;
    }

    public int getLobbyposition1() {
        return lobbyposition1;
    }

    public int getLobbyposition2() {
        return lobbyposition2;
    }
}
