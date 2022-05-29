package it.polimi.ingsw.messages;

import it.polimi.ingsw.CLI.GameBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public enum ClientState {
    CONNECT_STATE(0){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Inserisci il tuo nickname:");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Il nickname scelto è già stato scelto! Scegline un altro");
            return texts;
        }
    },INSERT_NEW_GAME_PARAMETERS(0){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Inserisci il numero di giocatori (2/3) e la tipologia di partita (base o expert) per dare via al matchmaking:");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Inserisci il tuo nickname:");
            return texts;
        }
    },WAIT_IN_LOBBY(0){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Resta in attesa di altri giocatori...");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Inserisci il tuo nickname:");
            return texts;
        }
    }
    ,WAIT_TURN(0){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Resta in attesa del tuo turno");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Inserisci il tuo nickname:");
            return texts;
        }
    },SET_UP_WIZARD_PHASE(0){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Inserisci il deck che vuoi utilizzare");
            texts.add("Deck disponibili:"+GB.getAvailableWizards());
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Il wizard scelto appartiene già ad un altro giocatore! Scegline un altro");
            return texts;
        }
    },SET_UP_TOWER_PHASE(0){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli il colore della tua squadra!");
            texts.add("Torri disponibili:"+GB.getAvailableTowers());
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("La torre scelta appartiene già ad un altro giocatore! Scegline un'altra");
            return texts;
        }
    },PLAY_ASSISTANT_CARD(0){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli una carta da giocare!");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("La carta scelta non è disponibile! Scegline un altro");
            return texts;
        }
    },
    MOVE_FROM_LOBBY(0){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli tre studenti da spostare nella table o su un'isola");
            texts.add("Per esempio digita move studentID1,studentID2,studentID3 in table,2,3 per muovere il primo studente nella table,il secondo sull'isola 2, il terzo sull'isola 3");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("Puoi anche scegliere di giocare una carta personalità! Digita play personality 5 per giocare la carta 5 ad esempio");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scelta non valida! Riprova");
            return texts;
        }
    },MOVE_MOTHER_NATURE(0){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Puoi far compiere a Madre Natura fino a X passi");
            texts.add("Per esempio digita move mn 5 per spostarla di 5 isole");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("Puoi anche scegliere di giocare una carta personalità! Digita play personality 5 per giocare la carta 5 ad esempio");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Non puoi spostare lì Madre Natura!");
            return texts;
        }
    }, PICK_CLOUD(0){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli una nuvola! I suoi studenti passeranno sulla tua lobby ");
            texts.add("Per esempio digita empty cloud 3 per scegliere la nuvola 3");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("Puoi anche scegliere di giocare una carta personalità! Digita play personality 5 per giocare la carta 5 ad esempio");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Non puoi scegliere quella nuvola! Riprova");
            return texts;
        }
    }, END_TURN(0){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Sei alla fine del tuo turno! Per chiudere il turno scrivi end");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("Puoi anche scegliere di giocare una carta personalità! Digita play personality 5 per giocare la carta 5 ad esempio");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Non è stato possibile terminare il turno! Riprova");
            return texts;
        }
    },CHOOSE_STUDENT_FOR_CARD_1(1){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli uno studente! Scrivi move 1 per spostare il primo studente della carta");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Non hai scelto uno studente valido");
            return texts;
        }
    },CHOOSE_ISLAND_FOR_CARD_3(3){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli un'isola! Scrivi influence on 1 per calcolare l'influenza sull'isola 1");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Non hai scelto un'isola valida");
            return texts;
        }
    },
    CHOOSE_ISLAND_FOR_CARD_5(5){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli un'isola! Per imporre il divieto sull'isola 1 scrivi ban 1");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Non hai scelto un'isola valida");
            return texts;
        }
    },SWAP_STUDENTS_FOR_CARD_7(7){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli un massimo di 3 studenti dalla carta per scambiarli con 3 della lobby");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Lo scambio non è valido!");
            return texts;
        }
    },CHOOSE_COLOR_FOR_CARD_9(9){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli un colore da bandire! Per bandire red scrivi ban red");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Il colore selezionato non può essere scelto");
            return texts;
        }
    },CHOOSE_STUDENTS_FOR_CARD_10(10){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli un massimo di 2 studenti da scambiare tra la tua sala e l'ingresso");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Lo scambio non è valido!");
            return texts;
        }
    },
    CHOOSE_STUDENT_FOR_CARD_11(11){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli uno studente da spostare! Per spostare il primo studente della carta scrivi move 1");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Lo studente selezionato non è valido!");
            return texts;
        }
    },CHOOSE_COLOR_FOR_CARD_12(12){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli un colore da rubare! Scrivi steal pink per togliere 3 pedine rosa a tutti");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Il colore selezionato non è valido!");
            return texts;
        }
    }, END_GAME(0){
        @Override
        public ArrayList<String> getContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("La partita si è conclusa! Per chiudere il gioco scrivi close");
            return texts;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            return null;
        }
    };



    private final int optionalID;

    /**
     * Constructor creates a ClientState.
     * @param optionalID: ID given to the state: IDs different from zero are relative to expert mode.
     */
    ClientState(int optionalID) {
        this.optionalID=optionalID;
    }

    /**
     * @return the optional ID of the state.
     */
    public int getOptionalID() {
        return optionalID;
    }


    public static Optional<ClientState> valueOf(int optionalID){
        return Arrays.stream(values())
                .filter(clientState -> clientState.optionalID == optionalID)
                .findFirst();
    }

    public abstract ArrayList<String> getContextMessage(GameBoard GB);
    public abstract ArrayList<String> getServerErrorMessage();
    public ArrayList<String> getInputErrorMessage(){
        ArrayList<String> texts = new ArrayList<>();
        texts.add("I dati inseriti non sono corretti! Riprova di nuovo");
        return texts;
    }
}
