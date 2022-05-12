package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.ExpertGame;
import it.polimi.ingsw.model.Game;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Random;
import java.util.prefs.PreferenceChangeListener;

//aggiorna model
public class GameController implements PropertyChangeListener {
    Game game;
    String currentPlayer;
    ArrayList<String> players;
    PropertyChangeSupport listener;
    UpdateMessageBuilder updateMessageBuilder;

    //risoluzione stupida al problema del tipo statico di Game GM: cast esplicito in base al boolean isExpert
    public GameController(boolean isExpert) {
        updateMessageBuilder = new UpdateMessageBuilder();
        listener = new PropertyChangeSupport(this);
        game = (isExpert) ? new ExpertGame() : new Game();
        //game.instantiateGameElements(); //va inizializzato il model, ma non so se questa chiamata va qui
    }

    /*
    public void updateTeamAndWizard(){
        game.giveAssistantDeck(); //assegna il deck
    }
*/
    public void assignAssistantDeck(){

    }

    public void assignInitialStudents(){

    }

    private String getRandomPlayer(){
        Random rand = new Random();
        String randomPlayer = players.get(rand.nextInt(players.size()));
        return randomPlayer;
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
            case "NotOwnedCoins":
            case "Bans":
            case "SelectedPersonality":

        }
        listener.firePropertyChange("UpdateMessage", null, toSend);
        //in propertyChange di GameHandler bisogna fare il controllo oldValue-newValue perché se
        // la generazione dei messaggi restituisce null non devo inviare nulla (tipo nel caso LastRound)

    }
}
