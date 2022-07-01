package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

/**
 * This message is going to be broadcast to all players in game
 * and notifies that a cloud has been emptied
 */

public class CloudUpdateMessage implements UpdateMessage {
    int cloudIndex;

    /**
     * @param cloudIndex index of the cloud that has been emptied
     */

    public CloudUpdateMessage(int cloudIndex) {
        this.cloudIndex = cloudIndex;
    }

    public int getCloudIndex() {
        return cloudIndex;
    }

    @Override
    public void update(GameBoard GB) {
        GB.emptyCloud(cloudIndex);
        GB.print();
    }
}
