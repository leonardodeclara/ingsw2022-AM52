package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.ExpertGame;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tower;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


//aggiorna model
public class GameController implements PropertyChangeListener {
    private Game game;
    private String currentPlayer;
    private ArrayList<String> players;
    private PropertyChangeSupport listener;
    private UpdateMessageBuilder updateMessageBuilder;
    private ArrayList<Tower> availableTowers;
    private ArrayList<Integer> availableWizards; //poi sta cosa va tolta da game
    private HashMap<String, Integer> playerToWizardMap;

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

        System.out.println("GameController: mi sono istanziato");
    }

    public Message handleGameInstantiation(){
        game.instantiateGameElements(players);
        game.setPropertyChangeListeners(this);
        System.out.println("GC: ho impostato correttamente i primi gameElements e ho settato i PropertyChange");
        return updateMessageBuilder.buildGameInstantiationMessage(game);
    }

    public Message updateWizardSelection(String player, Integer wizard){
        System.out.println("Maghi disponibili lato server:"+availableWizards);
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
        System.out.println("Torri disponibili lato server:"+availableTowers);
        if (availableTowers.contains(tower)){
            //game.addPlayer(new Player(game.getPlayers().size(),player, tower),playerToWizardMap.get(player)); //rivedere l'assegnamento dell'indice
            //game.setPlayerPropertyChangeListener(player, this); //setto il listener per questo player
            game.getPlayerByName(player).setTeam(tower);
            availableTowers.remove(tower);
            System.out.println("GameController: ho aggiornato le torri disponibili togliendo " + tower.toString());
            return new ClientStateMessage(ClientState.WAIT_TURN);
        }
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
    }

    public Message updateAssistantCards(String player, int cardID){
        if(game.playAssistantCard(player,cardID)==-1){ //se returna -1 la carta non può essere giocata
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
        }else{ //altrimenti
            return new ClientStateMessage(ClientState.WAIT_TURN);
        }
    }


    public Message moveStudentsFromLobby(String player, ArrayList<Integer> studentIDs, ArrayList<Integer> destIDs){
        if(game.moveStudentsFromLobby(player,studentIDs,destIDs))
            return new ErrorMessage(ErrorKind.INVALID_INPUT);
        else
            return new ClientStateMessage(ClientState.WAIT_TURN);
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


    public Message buildPlayerTowerAssociation(){
        HashMap<String,Tower> associations = new HashMap<>();
        for (Player player: game.getPlayers()){
            associations.put(player.getNickname(), player.getTeam());
        }
        return new GameStartMessage(associations);
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
            case "Gameover":
                toSend = updateMessageBuilder.buildGameOverMessage(event);
                break;
            case "CloudsRefill":
                toSend = updateMessageBuilder.buildCloudsRefillMessage(event);
                break;
            case "CurrentAssistantCards": //vedere cosa mandare effettivamente nel messaggio
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
}
