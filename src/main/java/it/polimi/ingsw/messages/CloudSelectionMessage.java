package it.polimi.ingsw.messages;
//messaggio Client->Server per comunicare la scelta di nuvola da svuotare per riempire la lobby
public class CloudSelectionMessage implements Message{
    private int cloudIndex;

    public CloudSelectionMessage(int cloudIndex){
        this.cloudIndex=cloudIndex;
    }

    public int getCloudIndex() {
        return cloudIndex;
    }
}
