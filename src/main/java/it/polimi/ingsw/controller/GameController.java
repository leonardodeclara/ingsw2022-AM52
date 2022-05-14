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
import java.util.prefs.PreferenceChangeListener;

//aggiorna model
public class GameController implements PropertyChangeListener {
    Game game;
    String currentPlayer;
    ArrayList<String> players;
    PropertyChangeSupport listener;
    UpdateMessageBuilder updateMessageBuilder;
    private ArrayList<Tower> availableTowers;
    private ArrayList<Integer> availableWizards; //poi sta cosa va tolta da game
    private HashMap<String, Integer> playerToWizardMap;

    //risoluzione stupida al problema del tipo statico di Game GM: cast esplicito in base al boolean isExpert
    public GameController(boolean isExpert) {
        updateMessageBuilder = new UpdateMessageBuilder();
        listener = new PropertyChangeSupport(this);
        game = (isExpert) ? new ExpertGame() : new Game();
        //game.instantiateGameElements(); //va inizializzato il model, ma non so se questa chiamata va qui
        availableWizards = new ArrayList<>();
        for (int i = 0; i< 4; i++){
            availableWizards.add(i);
        }
        availableTowers = new ArrayList<>();
        availableTowers.addAll(Arrays.asList(Tower.values()));
        playerToWizardMap = new HashMap<>();

    }


    //creo l'associazione giocatore-wizard, mi servirà dopo per fare game.giveAssistantDeck()
    public Message updateWizardSelection(String player, Integer wizard){
        System.out.println("Maghi disponibili lato server:"+availableWizards);
        if (availableWizards.contains(wizard)){
            playerToWizardMap.put(player, wizard);
            availableWizards.remove(wizard);
            System.out.println("GameController: ho aggiornato i mazzi disponibili togliendo il deck " + wizard);
            return new AvailableTowerMessage(availableTowers);
        }
        else
            return new ErrorMessage(ErrorKind.INVALID_INPUT);

        //game.giveAssistantDeck(); //assegna il deck
        //questo lo posso fare solo dopo
    }

    //creo l'assocazione giocatore-torre, mi serve per poter aggiungere i giocatori alla partita
    //potrei mettere qui dentro l'assegnamento del deck, ma posso anche farlo a parte
    public Message updateTowerSelection(String player, Tower tower){
        System.out.println("Torri disponibili lato server:"+availableTowers);
        if (availableTowers.contains(tower)){
            game.addPlayer(new Player(game.getPlayers().size(),player, tower)); //rivedere l'assegnamento dell'indice
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


    public void assignAssistantDeck(){

    }

    public void assignInitialStudents(){

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
        String eventName = event.getPropertyName();
        Message toSend = null;
        switch (eventName) {
            case "MotherNature":
                toSend = updateMessageBuilder.buildMotherNatureMessage(event);
            case "Merge":
                toSend = updateMessageBuilder.buildMergeMessage(event);
            case "LastRound":
                toSend = updateMessageBuilder.buildLastRoundMessage(event);
            case "Gameover":
                toSend = updateMessageBuilder.buildGameOverMessage(event);
            case "CloudsRefill":
                toSend = updateMessageBuilder.buildCloudsRefillMessage(event);
            case "CurrentAssistantCards": //vedere cosa mandare effettivamente nel messaggio
                toSend = updateMessageBuilder.buildCurrentTurnAssistantCardsMessage(event);
            case "Deck": //non se è messaggio broadcast, forse in gameHandler va gestito in maniera diversa
                toSend = updateMessageBuilder.buildDeckUpdateMessage(event);
            case "IslandTowers":
                toSend = updateMessageBuilder.buildIslandTowersMessage(event);
            case "IslandStudents":
                toSend = updateMessageBuilder.buildIslandStudentsMessage(event);
            case "PickedCloud":
                toSend = updateMessageBuilder.buildPickedCloudMessage(event);
            case "Board":
                toSend = updateMessageBuilder.buildBoardUpdateMessage(event);
            case "ActivePersonality":
                toSend = updateMessageBuilder.buildActivePersonalityMessage(event);
            case "NoLongerActivePersonality":
                toSend = updateMessageBuilder.builNoLongerActivePersonalityMessage(event);
            case "NotOwnedCoins": //vedere se effettivamente è utile
            case "Bans": //vedere se effettivamente è utile
            case "SelectedPersonality":
            //dovrebbero mancare listener per gli effetti "istant" delle carte personaggio, vedere quelle a parte
            //si potrebbe mettere come listener in quel caso cardController
        }
        listener.firePropertyChange("UpdateMessage", null, toSend);
        //in propertyChange di GameHandler bisogna fare il controllo oldValue-newValue perché se
        // la generazione dei messaggi restituisce null non devo inviare nulla (tipo nel caso LastRound)

    }
}
