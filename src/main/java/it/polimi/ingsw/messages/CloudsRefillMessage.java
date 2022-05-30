package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.ClientCloud;
import it.polimi.ingsw.CLI.GameBoard;

import java.util.ArrayList;

public class CloudsRefillMessage implements UpdateMessage{
    ArrayList<ClientCloud> clouds;

    public CloudsRefillMessage(ArrayList<ClientCloud> newClouds) {
        clouds = new ArrayList<>(newClouds);
    }

    public ArrayList<ClientCloud> getClouds() {
        return new ArrayList<>(clouds);
    }

    @Override
    public void update(GameBoard GB) {
        GB.setClouds(clouds);
        GB.print();
    }
}
