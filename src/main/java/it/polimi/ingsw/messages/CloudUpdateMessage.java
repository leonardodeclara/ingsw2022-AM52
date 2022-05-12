package it.polimi.ingsw.messages;
//messaggio che notifica che una nuvola è stata svuotata
public class CloudUpdateMessage implements Message{
    int cloudIndex;

    public CloudUpdateMessage(int cloudIndex) {
        this.cloudIndex = cloudIndex;
    }

    public int getCloudIndex() {
        return cloudIndex;
    }
}
