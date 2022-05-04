package it.polimi.ingsw.messages;

public class CloudUpdateMessage implements Message{
    int cloudIndex;

    public CloudUpdateMessage(int cloudIndex) {
        this.cloudIndex = cloudIndex;
    }

    public int getCloudIndex() {
        return cloudIndex;
    }
}
