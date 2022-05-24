package it.polimi.ingsw.controller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.function.*;


public class GameController implements PropertyChangeListener {
    private final Game game;
    private String currentPlayer;
    private final ArrayList<String> players;
    private final PropertyChangeSupport listener;
    private final UpdateMessageBuilder updateMessageBuilder;
    private final ArrayList<Tower> availableTowers;
    private final ArrayList<Integer> availableWizards; //poi sta cosa va tolta da game
    private final HashMap<String, Integer> playerToWizardMap;
    private BiPredicate<String,Integer> moveMotherNature;
    private BiFunction<Island,Object,HashMap<String,String>> calculateInfluence;
    private Consumer<String> updateTeachersOwnership;


    public GameController(boolean isExpert, ArrayList<String> players) {
        updateMessageBuilder = new UpdateMessageBuilder();
        listener = new PropertyChangeSupport(this);
        game = (isExpert) ? new ExpertGame(players.size()) : new Game(players.size());
        this.players = new ArrayList<>();
        this.players.addAll(players);
        availableWizards = new ArrayList<>();
        for (int i = 0; i< 4; i++){
            availableWizards.add(i);
        }
        availableTowers = new ArrayList<>();
        availableTowers.addAll(Arrays.asList(Tower.values()));
        playerToWizardMap = new HashMap<>();


        changeGameRulesForPersonalityCard(0); //settiamo le default rules

        System.out.println("GameController: mi sono istanziato");
    }

    public Message handleGameInstantiation(){
        game.instantiateGameElements(players);
        game.setPropertyChangeListeners(this);
        System.out.println("GC: ho impostato correttamente i primi gameElements e ho settato i PropertyChange");
        return updateMessageBuilder.buildGameInstantiationMessage(game);
    }

    public Message updateWizardSelection(String player, Integer wizard){
        System.out.println("Maghi disponibili lato server: "+availableWizards);
        if (availableWizards.contains(wizard)){
            game.giveAssistantDeck(player, wizard);
            playerToWizardMap.put(player, wizard);
            availableWizards.remove(wizard);
            System.out.println("GameController: ho aggiornato i mazzi disponibili togliendo il deck " + wizard);
            return new AvailableTowerMessage(availableTowers);
        }
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    //creo l'assocazione giocatore-torre, mi serve per poter aggiungere i giocatori alla partita
    //potrei mettere qui dentro l'assegnamento del deck, ma posso anche farlo a parte
    public Message updateTowerSelection(String player, Tower tower){
        System.out.println("Torri disponibili lato server: "+availableTowers);
        if (availableTowers.contains(tower)){
            game.getPlayerByName(player).setTeam(tower);
            availableTowers.remove(tower);
            System.out.println("GameController: ho aggiornato le torri disponibili togliendo " + tower.toString());
            return new ClientStateMessage(ClientState.WAIT_TURN);
        }
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    public void updateCloudsStudents(){
        game.refillClouds();
    }

    public Message updateAssistantCards(String player, int cardID){
        if(game.playAssistantCard(player,cardID)==-1){ //se returna -1 la carta non può essere giocata
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
        }else{ //altrimenti
            return new ClientStateMessage(ClientState.WAIT_TURN);
        }
    }


    public Message moveStudentsFromLobby(String player, ArrayList<Integer> studentIDs, ArrayList<Integer> destIDs){
        if(game.moveStudentsFromLobby(player,studentIDs,destIDs)){
            updateTeachersOwnership.accept(player);
            return new ClientStateMessage(ClientState.MOVE_MOTHER_NATURE);
        }
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    public Message moveMotherNature(String player, int steps){
        if(moveMotherNature.test(player,steps)){
            //qui va aggiunto tutta la gestione del calcolo influenza, spostamento torri, merge isole ecc
            calculateInfluence.apply(game.getCurrentMotherNatureIsland(),null);
            if (game.checkGameOver())
                return new EndGameMessage(game.getWinner()==null? Constants.TIE: game.getWinner().getNickname());

            if (game.isLastRound() && !game.areCloudsFull())
                //svuotamento delle nuvole viene saltato se siamo all'ultimo round e non ci sono abbastanza pedine studente per tutti
                //soluzione temporanea perché questo tipo di controllo va bene solo per il primo giocatore del turno
                //infatti se sono state riempite tutte le nuvole e quindi tutti possono pescare
                //dopo che il primo giocatore ha fatto pick_cloud questo metodo restituirà false, il che è sbagliato
                return new ClientStateMessage(ClientState.END_TURN);
            else
                return new ClientStateMessage(ClientState.PICK_CLOUD);
        }
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    public Message refillLobby(String player, int cloudIndex){
        if(game.moveStudentsToLobby(player, cloudIndex))
            return new ClientStateMessage(ClientState.END_TURN);
            //soluzione temporanea che va cambiata:
            //se sono in expert Game e non ho giocato la carta + ho i coins devo essere in grado di giocarla
            //intanto usiamo END_TURN
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    //questo deve costruire i messaggi degli stati ad hoc delle carte che fanno fare ai giocatori qualcosa
    //se invece modifica un input viene fatto tutto internmente a setActivePersonality

    public Message playPersonalityCard(String player, int cardID,ClientState currentClientState){
        if(((ExpertGame) game).setActivePersonality(cardID)) {
            changeGameRulesForPersonalityCard(cardID);
            Optional<ClientState> clientState = ClientState.valueOf(cardID);
            return clientState.map(ClientStateMessage::new).orElseGet(() -> new ClientStateMessage(currentClientState));
        }else{
            return new ErrorMessage(ErrorKind.ILLEGAL_MOVE);
        }
    }

    public void resetPersonalityCard(){
        ((ExpertGame) game).resetActivePersonality();
        changeGameRulesForPersonalityCard(0);
    }

    private void changeGameRulesForPersonalityCard(int cardID){
        switch(cardID){
            case 0: //resetta gli effetti sulle regole di gioco
                moveMotherNature = game::moveMotherNature;
                updateTeachersOwnership = game::updateTeachersOwnership;
                calculateInfluence = game::calculateInfluence;
                break;
            case 2: //in teoria quando chiamiamo case != 0, è già stato resettato tutto, ma per sicurezza li mettiamo tutti i metodi
                moveMotherNature = game::moveMotherNature;
                updateTeachersOwnership = ((ExpertGame)game)::updateTeachersOwnershipForCard2;
                calculateInfluence = game::calculateInfluence;
                break;
            case 4:
                moveMotherNature = ((ExpertGame)game)::moveMotherNatureForCard4;
                updateTeachersOwnership = game::updateTeachersOwnership;
                calculateInfluence = game::calculateInfluence;
                break;
            case 6:
                moveMotherNature = game::moveMotherNature;
                updateTeachersOwnership = game::updateTeachersOwnership;
                calculateInfluence = ((ExpertGame)game)::calculateInfluenceForCard6;
                break;
            case 8:
                moveMotherNature = game::moveMotherNature;
                updateTeachersOwnership = game::updateTeachersOwnership;
                calculateInfluence = ((ExpertGame)game)::calculateInfluenceForCard8;
                break;
            case 9:
                moveMotherNature = game::moveMotherNature;
                updateTeachersOwnership = game::updateTeachersOwnership;
                calculateInfluence = ((ExpertGame)game)::calculateInfluenceForCard9;
                break;
        }
    }


    //return true se la partita è finita, false otherwise
    public Message closeCurrentRound(){
        //bisogna gestire la fine partita nel caso fosse il lastRound
        if (game.isLastRound()){
            game.calculateWinner();
            //in game manca tutto il calcolo del vincitore in caso la partita finisca al termine del lastRound
            System.out.println("Finita la partita");
            return new EndGameMessage(game.getWinner()==null? Constants.TIE: game.getWinner().getNickname());
        }
        game.resetCurrentTurnAssistantCards();
        return null;
    }

    public ArrayList<String> getActionPhaseTurnOrder(){
        return game.getActionPhasePlayerOrder();
    }


    private String getRandomPlayer(){
        Random rand = new Random();
        String randomPlayer = players.get(rand.nextInt(players.size()));
        return randomPlayer;
    }

    public ArrayList<Integer> getAvailableWizards() {
        return availableWizards;
    }

    public ArrayList<Tower> getAvailableTowers() {
        return availableTowers;
    }

    public void setUpdateListener(GameHandler gameHandler){
        listener.addPropertyChangeListener("UpdateMessage", gameHandler);
        //si potrebbe mettere anche il listener per il messaggio di errore,
        // o magari quello lo gestisco in maniera diversa. rivedere
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        System.out.println("GC: è stato invocato il mio propertyChange");
        String eventName = event.getPropertyName();
        Message toSend = null;
        switch (eventName) {
            case "MotherNature":
                toSend = updateMessageBuilder.buildMotherNatureMessage(event);
                break;
            case "Merge":
                toSend = updateMessageBuilder.buildMergeMessage(event);
                break;
            case "LastRound":
                toSend = updateMessageBuilder.buildLastRoundMessage(event);
                break;
            //case "Gameover":
            //    toSend = updateMessageBuilder.buildGameOverMessage(event);
            //    break;
            case "CloudsRefill":
                toSend = updateMessageBuilder.buildCloudsRefillMessage(event);
                break;
            case "CurrentTurnAssistantCards": //vedere cosa mandare effettivamente nel messaggio
                toSend = updateMessageBuilder.buildCurrentTurnAssistantCardsMessage(event);
                break;
            case "Deck": //non se è messaggio broadcast, forse in gameHandler va gestito in maniera diversa
                toSend = updateMessageBuilder.buildDeckUpdateMessage(event);
                break;
            case "IslandTowers":
                toSend = updateMessageBuilder.buildIslandTowersMessage(event);
                break;
            case "IslandStudents":
                toSend = updateMessageBuilder.buildIslandStudentsMessage(event);
                System.out.println("GC: il messaggio di update degli studenti dell'isola è pronto");
                break;
            case "PickedCloud":
                toSend = updateMessageBuilder.buildPickedCloudMessage(event);
                break;
            case "Board":
                toSend = updateMessageBuilder.buildBoardUpdateMessage(event);
                break;
            case "ActivePersonality":
                toSend = updateMessageBuilder.buildActivePersonalityMessage(event);
                break;
            case "NoLongerActivePersonality":
                toSend = updateMessageBuilder.buildNoLongerActivePersonalityMessage(event);
                break;
            case "NotOwnedCoins": //vedere se effettivamente è utile
            case "Bans": //vedere se effettivamente è utile
            case "SelectedPersonality":
            //dovrebbero mancare listener per gli effetti "istant" delle carte personaggio, vedere quelle a parte
            //si potrebbe mettere come listener in quel caso cardController
        }
        if (toSend!=null){
            System.out.println("GC: ho generato un messaggio di update valido: ora lo passo a GH");
            listener.firePropertyChange("UpdateMessage", null, toSend);
        }

        //in propertyChange di GameHandler bisogna fare il controllo oldValue-newValue perché se
        // la generazione dei messaggi restituisce null non devo inviare nulla (tipo nel caso LastRound)

    }

    public Game getGame() {
        return game;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
        game.setCurrentPlayer(currentPlayer);
    }
}
