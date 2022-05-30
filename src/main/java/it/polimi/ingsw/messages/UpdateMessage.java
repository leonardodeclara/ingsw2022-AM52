package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.GameBoard;

public interface UpdateMessage extends Message{
    public void update(GameBoard GB);
}
