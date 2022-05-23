package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.ClientPersonality;

import java.util.ArrayList;

public class ExtractedPersonalitiesMessage implements Message{
    private ArrayList<ClientPersonality> personalities;

    public ExtractedPersonalitiesMessage(ArrayList<ClientPersonality> personalities){
        this.personalities.addAll(personalities);
    }

    public ArrayList<ClientPersonality> getPersonalities() {
        return personalities;
    }
}
