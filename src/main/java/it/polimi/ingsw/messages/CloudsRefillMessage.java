package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.ClientCloud;

import java.util.ArrayList;

public class CloudsRefillMessage implements Message{
    ArrayList<ClientCloud> clouds;

    public CloudsRefillMessage(ArrayList<ClientCloud> newClouds) {
        clouds = new ArrayList<>(newClouds);
    }

    public ArrayList<ClientCloud> getClouds() {
        return new ArrayList<>(clouds);
    }
}
