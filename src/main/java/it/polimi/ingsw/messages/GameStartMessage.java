package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Tower;

import java.util.HashMap;

public class GameStartMessage implements Message{
    HashMap<String, Tower> ChosenTeam;

    public GameStartMessage(HashMap<String, Tower> chosenTeam) {
        ChosenTeam = chosenTeam;
    }

    public HashMap<String,Tower> getChosenTeam(){
        return ChosenTeam;
    }
}
