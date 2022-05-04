package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Color;

import java.util.HashMap;

public class BoardUpdateMessage implements Message{
    HashMap<Color, Integer> updatedBoard;

    public BoardUpdateMessage(HashMap<Color, Integer> updatedBoard) {
        this.updatedBoard = updatedBoard;
    }

    public HashMap<Color, Integer> getUpdatedBoard() {
        return updatedBoard;
    }
}
