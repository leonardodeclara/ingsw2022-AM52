package it.polimi.ingsw.messages;

public class CloudSelectionMessage implements Message{
    int cloudIndex;

    public CloudSelectionMessage(int cloudIndex) {
        this.cloudIndex = cloudIndex;
    }

    public int getCloudIndex() {
        return cloudIndex;
    }
}
