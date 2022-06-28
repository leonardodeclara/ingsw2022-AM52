package it.polimi.ingsw.messages.UpdateMessages;

import it.polimi.ingsw.client.CLI.GameBoard;
import it.polimi.ingsw.messages.UpdateMessage;

//messaggio che notifica che una nuvola è stata svuotata
public class CloudUpdateMessage implements UpdateMessage {
    int cloudIndex;

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
