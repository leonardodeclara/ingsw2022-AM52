package it.polimi.ingsw.controller;

import it.polimi.ingsw.CLI.ClientCloud;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;

//v1 di UpdateMessageBuilder: la classe si occupa di generare il messaggio di update in base al contenuto dell'evento ricevuto
//questo messaggio viene restituito al controller, ascoltato da gameHandler che riceverà il messaggio da controller via firePropertyChange e ne gestirà l'invio ai client
public class UpdateMessageBuilder {

    public UpdateMessageBuilder(){
    }

    public Message buildMotherNatureMessage(PropertyChangeEvent event){
        int motherNaturePosition = (int) event.getNewValue();
        return new MotherNatureMovementUpdateMessage(motherNaturePosition);
    }

    public Message buildMergeMessage(PropertyChangeEvent event){
        int[] islandIndexes = (int[]) event.getNewValue();
        return new IslandMergeUpdateMessage(islandIndexes[0], islandIndexes[1]);
    }

    public Message buildLastRoundMessage(PropertyChangeEvent event){
        boolean oldLastRound = (boolean) event.getOldValue();
        boolean newLastRound = (boolean) event.getNewValue();
        if (oldLastRound != newLastRound)
            return new LastRoundMessage("Questo è l'ultimo round della partita!");
        else return null;
    }

    public Message buildGameOverMessage(PropertyChangeEvent event){
        String winnerName = (String) event.getNewValue();
        if (winnerName!=null)
            return new EndGameMessage("Abbiamo un vincitore", winnerName);
        else
            return new EndGameMessage("LA partita è finita in parità", null);
        //poi in gameHandler si può gestire il caso di mandare un messaggio di win al vincitore, di lose ai perdenti ecc
        //per il momento mandiamo in broadcast il nome del vincitore se ce n'è uno
    }

    public Message buildCloudsRefillMessage(PropertyChangeEvent event){
        ArrayList<Cloud> modelClouds = (ArrayList<Cloud>) event.getNewValue();
        ArrayList<ClientCloud> clientClouds = new ArrayList<>();
        int numClouds = modelClouds.size();
        for (Cloud modelCloud : modelClouds) {
            clientClouds.add(new ClientCloud(modelCloud.getCloudIndex(), modelCloud.getStudents()));
        }
        return new CloudsRefillMessage(clientClouds);
    }

    public Message buildCurrentTurnAssistantCardsMessage(PropertyChangeEvent event){
        HashMap<String, Assistant> cards = (HashMap<String, Assistant>) event.getNewValue();
        HashMap<String, Integer> playerToCardMap = new HashMap<>();
        int cardPriority;
        for (String name: cards.keySet()){
            cardPriority = cards.get(name).getPriority();
            playerToCardMap.put(name, cardPriority);
        }
        return new CurrentTurnAssistantCardsUpdateMessage(playerToCardMap); //ritorno solo l'associazione <Giocatore, Priorità>
    }

    public Message buildDeckUpdateMessage(PropertyChangeEvent event){
        Player updatedPlayer = (Player) event.getNewValue();
        ArrayList<Assistant> updatedDeck = updatedPlayer.getDeck();
        HashMap<Integer, Integer> availableCards = new HashMap<>();
        for (Assistant card: updatedDeck){
            availableCards.put(card.getPriority(), card.getNumMoves());
        }
        return new AssistantDeckUpdateMessage(updatedPlayer.getNickname(), availableCards);
    }

    //questo metodo e quello dopo potrebbero essere collassati. rivedere
    public Message buildIslandTowersMessage(PropertyChangeEvent event){
        Island updatedIsland = (Island) event.getNewValue();
        ArrayList<Tower> towers = updatedIsland.getTowers();
        int index = updatedIsland.getIslandIndex();
        return new IslandTowersUpdateMessage(index, towers);
    }

    public Message buildIslandStudentsMessage(PropertyChangeEvent event){
        Island updatedModelIsland = (Island) event.getNewValue();
        ArrayList<Color> students = updatedModelIsland.getStudents();
        int index = updatedModelIsland.getIslandIndex();
        return new IslandStudentsUpdateMessage(index, students);
    }

    public Message buildPickedCloudMessage(PropertyChangeEvent event){
        int cloudIndex = (int) event.getNewValue();
        return new CloudUpdateMessage(cloudIndex);
    }

    public Message buildBoardUpdateMessage(PropertyChangeEvent event){
        Player updatedPlayer = (Player) event.getNewValue();
        Board updatedBoard = updatedPlayer.getBoard();
        String updatedOwner = updatedPlayer.getNickname();
        return new BoardUpdateMessage(updatedBoard.getStudentsTable(), updatedBoard.getLobby(), updatedBoard.getTeacherTable(), updatedOwner);
    }

    public Message buildActivePersonalityMessage(PropertyChangeEvent event){
        int activeCardId = (int) event.getNewValue();
        return new ActiveCharacterCardMessage(activeCardId);
    }

    //poi vedere se effettivamente è utile o no o se posso inglobare in messaggi di fine round
    public Message builNoLongerActivePersonalityMessage(PropertyChangeEvent event){
        int inactiveCardId = (int) event.getNewValue();
        return new InactiveCharacterCardMessage(inactiveCardId);
    }


}
