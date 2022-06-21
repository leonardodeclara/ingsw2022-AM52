package it.polimi.ingsw.CLI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Color;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientPersonality implements Serializable {
    static final long serialVersionUID = 42L;
    private final int cardId;
    private boolean hasBeenUsed;
    private int cost;
    private int bans;
    private ArrayList<Color> students;
    private String description;

    public ClientPersonality(Integer cardID, Boolean hasBeenUsed, Integer cost) {
        cardId = cardID;
        this.hasBeenUsed = hasBeenUsed;
        this.cost = cost;
        students= new ArrayList<>();
        bans=0;
        createDescription();
    }

    public void createDescription(){
        description="";
        switch (cardId){
            case 1:
                description+=("Prendi 1 Studente dalla carta e piazzalo\n");
                description+=("su un'Isola a tua scelta. Poi, pesca 1 Studente dal\n");
                description+=("sacchetto e mettilo su questa carta.\n");
                break;
            case 2:
                description+=("Durante questo turno, prendi il controllo dei\n");
                description+=("Professori anche se nella tua Sala hai lo stesso numero\n");
                description+=("di Studenti del giocatore che li controlla in quel\n");
                description+=("momento.\n");
                break;
            case 3:
                description+=("Scegli un'isola e calcola la maggioranza\n");
                description+=("come se Madre Natura avesse terminato il suo\n");
                description+=("movimento lì. In questo turno Madre Natura si muoverà\n");
                description+=("come di consueto e nell'Isola dove terminerà il suo\n");
                description+=("movimento la maggioranza verrà normalmente calcolata.\n");
                break;
            case 4:
                description+=("Puoi muovere Madre Natura fino a 2 Isole\n");
                description+=("addizionali rispetto a quanto indicato sulla carta\n");
                description+=("assistente che hai giocato.\n");
                break;
            case 5:
                description+=("Piazza una tessera Divieto su un'Isola a tua\n");
                description+=("scelta. La prima volta che Madre Natura termina il suo\n");
                description+=("movimento lì, rimettete la tessera DIvieto sulla carta\n");
                description+=("SENZA calcolare l'influenza su quell'Isola nè piazzare Torri.\n");
                break;
            case 6:
                description+=("Durante il conteggio dell'influenza su\n");
                description+=("un'Isola (o su un gruppo di Isole), le Torri presenti non\n");
                description+=("vengono calcolate.\n");
                break;
            case 7:
                description+=("Puoi prendere fino a 3 Studenti da qusta\n");
                description+=("carta e scambiarli con altrettanti Studenti presenti nel\n");
                description+=("tuo Ingresso.\n");
                break;
            case 8:
                description+=("In questo turno, durante il calcolo\n");
                description+=("dell'influenza hai 2 punti di influenza addizionali.\n");
                break;
            case 9:
                description+=("Scegli un colore di Studente; in questo turno,\n");
                description+=("durante il calcolo dell'influenza quel colore non fornisce\n");
                description+=("influenza.\n");
                break;
            case 10:
                description+=("Puoi scambiare fra loro fino a 2 Studenti\n");
                description+=("presenti nella tua Sala e nel tuo Ingresso.\n");
                break;
            case 11:
                description+=("Prendi 1 Studente da questa carta e piazzalo\n");
                description+=("nella tua Sala. Poi pesca un nuovo Studente dal\n");
                description+=("sacchetto e posizionalo su questa carta.\n");
                break;
            case 12:
                description+=("Scegli un colore di Studente; ogni giocatore\n");
                description+=("(incluso te) deve rimettere nel sacchetto 3 Studenti di\n");
                description+=("quel colore presenti nella sua Sala. Chi avesse meno di\n");
                description+=("3 Studenti di quel colore, rimetterà tutti quelli che ha.\n");

        }
    }

    private void printDescription(){
        System.out.print(description);
    }

    public void print(){
        System.out.print("ID: "+cardId + " " +"Costo: "+cost+ " ");
        if(students.size() > 0){
            System.out.print("STUDENTI: ");
            for (Color student : students) {
                System.out.print(Constants.getStudentsColor(student) + "■ ");
                System.out.print(Constants.RESET);
            }
            System.out.println();
        }
        if (bans>0){
            System.out.println("BANS: " + bans);
        }
        System.out.println("DESCRIZIONE: ");
        printDescription();
    }

    public void updateCost(){
        if(!hasBeenUsed){
            cost+=1;
            setHasBeenUsed(true);
        }
    }
    public int getCardID() {
        return cardId;
    }

    public void setHasBeenUsed(Boolean hasBeenUsed) {
        this.hasBeenUsed = hasBeenUsed;
    }

    public void setStudents(ArrayList<Color> students){
        this.students =students;
    }

    public void setBans(int bans) {
        this.bans = bans;
    }

    public ArrayList<Color> getStudents() {
        return students;
    }

    public int getBans() {
        return bans;
    }

    public boolean isHasBeenUsed() {
        return hasBeenUsed;
    }

    public String getDescription(){
        return description;
    }
}

