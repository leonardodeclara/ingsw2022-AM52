package it.polimi.ingsw.client;

import it.polimi.ingsw.CLI.GameBoard;
import it.polimi.ingsw.GUI.Clickable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum ClientState {
    CONNECT_STATE(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Inserisci il tuo nickname:");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            return null;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Il nickname scelto è già stato scelto! Scegline un altro");
            return texts;
        }



        @Override
        public ArrayList<Clickable> getClickableList() {
            return null;
        }
    },INSERT_NEW_GAME_PARAMETERS(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Inserisci il numero di giocatori (2/3) e la tipologia di partita (base o expert) per dare via al matchmaking:");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            return null;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Inserisci il tuo nickname:");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return null;
        }
    },WAIT_IN_LOBBY(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Resta in attesa di altri giocatori...");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            return null;
        }
        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Inserisci il tuo nickname:");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return null;
        }
    }
    ,WAIT_TURN(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Resta in attesa del tuo turno");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            return getCLIContextMessage(GB);
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Inserisci il tuo nickname:");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return null;
        }
    },SET_UP_WIZARD_PHASE(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Inserisci il deck che vuoi utilizzare");
            texts.add("Deck disponibili:"+GB.getAvailableWizards());
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            return null;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Il wizard scelto appartiene già ad un altro giocatore! Scegline un altro");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return null;
        }
    },SET_UP_TOWER_PHASE(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli il colore della tua squadra!");
            texts.add("Torri disponibili:"+GB.getAvailableTowers());
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            return null;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("La torre scelta appartiene già ad un altro giocatore! Scegline un'altra");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return null;
        }
    },PLAY_ASSISTANT_CARD(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli una carta da giocare!");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            return getCLIContextMessage(GB);
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("La carta scelta non è disponibile! Scegline un altro");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.ASSISTANT));
        }
    },
    MOVE_FROM_LOBBY(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli tre studenti da spostare nella table o su un'isola.");
            texts.add("Per esempio digita move studentID1,studentID2,studentID3 in table,2,3 per muovere il primo studente nella table,il secondo sull'isola 2, il terzo sull'isola 3.");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("Puoi anche scegliere di giocare una carta personalità! Digita play personality 5 per giocare la carta 5 ad esempio.");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli tre studenti da spostare nella table o su un'isola.");
            texts.add("Fai drag and drop dall'ingresso alla destinazione: quando hai terminato premi CONFIRM.");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("Puoi anche scegliere di giocare una carta personalità! Fai click sulla carta che vuoi giocare e premi CONFIRM.");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scelta non valida! Riprova");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList<>(List.of(Clickable.LOBBY_STUDENT, Clickable.PERSONALITY));
        }
    },MOVE_MOTHER_NATURE(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Puoi far compiere a Madre Natura fino a X passi.");
            texts.add("Per esempio digita move mn 5 per spostarla di 5 isole.");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("Puoi anche scegliere di giocare una carta personalità! Digita play personality 5 per giocare la carta 5 ad esempio");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Muovi Madre Natura facendo click sull'isola di destinazione e poi CONFIRM.");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("Puoi anche scegliere di giocare una carta personalità! Fai click sulla carta che vuoi giocare e premi CONFIRM.");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scelta non valida! Riprova");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.ISLAND, Clickable.PERSONALITY));
        }
    }, PICK_CLOUD(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli una nuvola! I suoi studenti passeranno sulla tua lobby ");
            texts.add("Per esempio digita empty cloud 3 per scegliere la nuvola 3");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("Puoi anche scegliere di giocare una carta personalità! Digita play personality 5 per giocare la carta 5 ad esempio");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli una nuvola! I suoi studenti passeranno sulla tua lobby.");
            texts.add("Fai click sull'isola scelta e poi CONFIRM");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("Puoi anche scegliere di giocare una carta personalità! Fai click sulla carta che vuoi giocare e premi CONFIRM.");
            return texts;
        }


        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scelta non valida! Riprova");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.CLOUD, Clickable.PERSONALITY));
        }
    }, END_TURN(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Sei alla fine del tuo turno! Per chiudere il turno scrivi end");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("Puoi anche scegliere di giocare una carta personalità! Digita play personality 5 per giocare la carta 5 ad esempio");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Sei alla fine del tuo turno! Fai click su END TURN per chiudere il turno");
            if (GB.isExpertGame() && !GB.isPersonalityCardBeenPlayed())
                texts.add("Sei ancora in tempo per scegliere di giocare una carta personalità! Fai click sulla carta che vuoi giocare e premi END TURN.");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scelta non valida! Riprova");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.PERSONALITY));
        }
    },CHOOSE_STUDENT_FOR_CARD_1(1){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli uno studente! Scrivi move 1 in 1 per spostare il primo studente della carta sull'isola 1");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli uno studente da spostare! Fai drag and drop sull'isola di destinazione e premi CONFIRM.");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scelta non valida");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.PERSONALITY_CARD_STUDENT));
        }
    },CHOOSE_ISLAND_FOR_CARD_3(3){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli un'isola! Scrivi influence on 1 per calcolare l'influenza sull'isola 1");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Fai click sull'isola dove vuoi calcolare l'influenza scelta e poi CONFIRM.");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Non hai scelto un'isola valida");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.ISLAND));
        }
    },
    CHOOSE_ISLAND_FOR_CARD_5(5){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli un'isola! Per imporre il divieto sull'isola 1 scrivi ban 1");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Fai click sull'isola dove vuoi imporre il divieto e poi CONFIRM");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Non hai scelto un'isola valida");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.ISLAND));
        }
    },SWAP_STUDENTS_FOR_CARD_7(7){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli un massimo di 3 studenti dalla carta per scambiarli con 3 della lobby");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
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

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.PERSONALITY_CARD_STUDENT,Clickable.LOBBY_STUDENT));
        }
    },CHOOSE_COLOR_FOR_CARD_9(9){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli un colore da bandire! Per bandire red scrivi ban red");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli un colore da bandire facendo click sul rispettivo professore");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Il colore selezionato non può essere scelto");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.COLOR));
        }
    },CHOOSE_STUDENTS_FOR_CARD_10(10){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli un massimo di 2 studenti da scambiare tra la tua sala e l'ingresso");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
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

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.LOBBY_STUDENT,Clickable.TABLE_STUDENT));
        }
    },
    CHOOSE_STUDENT_FOR_CARD_11(11){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli uno studente da spostare! Per spostare il primo studente della carta scrivi move 1");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli uno studente da spostare nella sala cliccandolo e poi CONFIRM.");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Lo studente selezionato non è valido!");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.PERSONALITY_CARD_STUDENT));
        }
    },CHOOSE_COLOR_FOR_CARD_12(12){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli un colore da rubare! Scrivi steal pink per togliere 3 pedine rosa a tutti");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Scegli un colore da bandire facendo click sul rispettivo professore e poi CONFIRM");
            return texts;

        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("Il colore selezionato non è valido!");
            return texts;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
            return new ArrayList(List.of(Clickable.COLOR));
        }
    }, END_GAME(0){
        @Override
        public ArrayList<String> getCLIContextMessage(GameBoard GB){
            ArrayList<String> texts = new ArrayList<>();
            texts.add("La partita si è conclusa! Per chiudere il gioco scrivi close");
            return texts;
        }

        @Override
        public ArrayList<String> getGUIContextMessage(GameBoard GB) {
            ArrayList<String> texts = new ArrayList<>();
            texts.add("La partita si è conclusa!");
            return texts;
        }

        @Override
        public ArrayList<String> getServerErrorMessage(){
            return null;
        }

        @Override
        public ArrayList<Clickable> getClickableList() {
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

    public abstract ArrayList<String> getCLIContextMessage(GameBoard GB);
    public abstract ArrayList<String> getGUIContextMessage(GameBoard GB);
    public abstract ArrayList<String> getServerErrorMessage();
    public ArrayList<String> getInputErrorMessage(){
        ArrayList<String> texts = new ArrayList<>();
        texts.add("I dati inseriti non sono corretti! Riprova di nuovo");
        return texts;
    }
    public abstract ArrayList<Clickable> getClickableList();

}
