package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.GameBoard;

//messaggio che notifica che una nuvola è stata svuotata
public class CloudUpdateMessage implements UpdateMessage{
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
