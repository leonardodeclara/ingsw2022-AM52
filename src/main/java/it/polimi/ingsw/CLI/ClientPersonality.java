package it.polimi.ingsw.CLI;

import it.polimi.ingsw.model.Color;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientPersonality implements Serializable {
    static final long serialVersionUID = 42L;
    private Integer CardID;
    private Boolean hasBeenUsed;
    private Integer cost;
    private int bans;
    private String description; //descrizione da visualizzare quando si printa la board
    private ArrayList<Color> students;

    public ClientPersonality(Integer cardID, Boolean hasBeenUsed, Integer cost) {
        CardID = cardID;
        this.hasBeenUsed = hasBeenUsed;
        this.cost = cost;
        students= new ArrayList<>();
        bans=0;
    }

    public void getDescription(Integer cardID){
        switch (cardID){
            case 1:
                System.out.print("Prendi 1 Studente dalla carta e piazzalo\n");
                System.out.print("su un'Isola a tua scelta. Poi, pesca 1 Studente dal\n");
                System.out.print("sacchetto e mettilo su questa carta.)\n");
                break;
            case 2:
                System.out.print("Durante questo turno, prendi il controllo dei\n");
                System.out.print("Professori anche se nella tua Sala hai lo stesso numero\n");
                System.out.print("di Studenti del giocatore che li controlla in quel\n");
                System.out.print("momento.)\n");
                break;
            case 3:
                System.out.print("Scegli un'isola e calcola la maggioranza\n");
                System.out.print("come se Madre Natura avesse terminato il suo\n");
                System.out.print("movimento lì. In questo turno Madre Natura si muoverà\n");
                System.out.print("come di consueto e nell'Isola dove terminerà il suo\n");
                System.out.print("movimento la maggioranza verrà normalmente calcolata.)\n");
                break;
            case 4:
                System.out.print("Puoi muovere Madre Natura fino a 2 Isole\n");
                System.out.print("addizionali rispetto a quanto indicato sulla carta\n");
                System.out.print("assistente che hai giocato.)\n");
                break;
            case 5:
                System.out.print("Piazza una tessera Divieto su un'Isola a tua\n");
                System.out.print("scelta. La prima volta che Madre Natura termina il suo\n");
                System.out.print("movimento lì, rimettete la tessera DIvieto sulla carta\n");
                System.out.print("SENZA calcolare l'influenza su quell'Isola nè piazzare Torri.)\n");
                break;
            case 6:
                System.out.print("Durante il conteggio dell'influenza su\n");
                System.out.print("un'Isola (o su un gruppo di Isole), le Torri presenti non\n");
                System.out.print("vengono calcolate.)\n");
                break;
            case 7:
                System.out.print("Puoi prendere fino a 3 Studenti da qusta\n");
                System.out.print("carta e scambiarli con altrettanti Studenti presenti nel\n");
                System.out.print("tuo Ingresso.)\n");
                break;
            case 8:
                System.out.print("In questo turno, durante il calcolo\n");
                System.out.print("dell'influenza hai 2 punti di influenza addizionali.)\n");
                break;
            case 9:
                System.out.print("Scegli un colore di Studente; in questo turno,\n");
                System.out.print("durante il calcolo dell'influenza quel colore non fornisce\n");
                System.out.print("influenza.)\n");
                break;
            case 10:
                System.out.print("Puoi scambiare fra loro fino a 2 Studenti\n");
                System.out.print("presenti nella tua Sala e nel tuo Ingresso.)\n");
                break;
            case 11:
                System.out.print("Prendi 1 Studente da questa carta e piazzalo\n");
                System.out.print("nella tua Sala. Poi pesca un nuovo Studente dal\n");
                System.out.print("sacchetto e posizionalo su questa carta.)\n");
                break;
            case 12:
                System.out.print("Scegli un colore di Studente; ogni giocatore\n");
                System.out.print("(incluso te) deve rimettere nel sacchetto 3 Studenti di\n");
                System.out.print("quel colore presenti nella sua Sala. Chi avesse meno di\n");
                System.out.print("3 Studenti di quel colore, rimetterà tutti quelli che ha.)\n");

        }
    }

    public void updateCost(){
        if(!hasBeenUsed){
            cost+=1;
            setHasBeenUsed(true);
        }
    }
    public Integer getCardID() {
        return CardID;
    }

    public Boolean getHasBeenUsed() {
        return hasBeenUsed;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCardID(Integer cardID) {
        CardID = cardID;
    }

    public void setHasBeenUsed(Boolean hasBeenUsed) {
        this.hasBeenUsed = hasBeenUsed;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public void setStudents(ArrayList<Color> students){
        this.students.clear();
        this.students.addAll(students);
    }

    public void setBans(int bans) {
        this.bans = bans;
    }
}

