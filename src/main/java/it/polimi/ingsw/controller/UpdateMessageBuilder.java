package it.polimi.ingsw.controller;

import it.polimi.ingsw.client.CLI.ClientBoard;
import it.polimi.ingsw.client.CLI.ClientCloud;
import it.polimi.ingsw.client.CLI.ClientIsland;
import it.polimi.ingsw.client.CLI.ClientPersonality;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.UpdateMessages.ActivePersonalityMessage;
import it.polimi.ingsw.messages.UpdateMessages.*;
import it.polimi.ingsw.model.*;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Class UpdateMessageBuilder is responsible for the assembly of an UpdateMessage carrying the information about changes in the model state.
 * According to the update type a specific Message is built and passed off to GameController class. Each Message is going to be broadcast to all players in game.
 */
public class UpdateMessageBuilder {

    /**
     * Method buildGameInstantiationMessage builds a message carrying the information about newly instantiated game elements.
     * @param game Game instance whose elements have been newly instantiated.
     * @return Message instance holding the information about the model update.
     */
    public static Message buildGameInstantiationMessage(Game game) {
        ArrayList<ClientIsland> clientIslands = new ArrayList<>();
        int towersNumber = game.getNumOfPlayers() == 2 ? 8 : 6;
        for (Island modelIsland : game.getIslands()) {
            ClientIsland newIsland = new ClientIsland(modelIsland.getIslandIndex());
            newIsland.setMotherNature(modelIsland.isMotherNature());
            newIsland.setStudents(modelIsland.getStudents());
            clientIslands.add(newIsland);
        }
        HashMap<String, ClientBoard> clientBoards = new HashMap<>();
        for (Player modelPlayer : game.getPlayers()) {
            Board modelBoard = modelPlayer.getBoard();
            ClientBoard newBoard = new ClientBoard(towersNumber, modelPlayer.getNickname());
            newBoard.setTeacherTable(new ArrayList<>(modelBoard.getTeacherTable()));
            newBoard.setLobby(new ArrayList<>(modelBoard.getLobby()));
            newBoard.setStudentsTable(new HashMap<>(modelBoard.getStudentsTable()));
            clientBoards.put(modelPlayer.getNickname(), newBoard);

        }
        if (game instanceof ExpertGame) {
            ArrayList<ClientPersonality> personalities = new ArrayList<>();
            for (Personality personality : ((ExpertGame) game).getPersonalities()) {
                ClientPersonality clientPersonality = new ClientPersonality(personality.getCharacterId(), false, personality.getCost());
                if (personality instanceof BanPersonality)
                    clientPersonality.setBans(Constants.MAX_BANS_NUMBER);
                if(personality instanceof LobbyPersonality)
                    clientPersonality.setStudents(((LobbyPersonality) personality).getStudents());
                personalities.add(clientPersonality);
            }
            for (String player : clientBoards.keySet()) {
                clientBoards.get(player).setCoins(1);
            }
            return new GameInstantiationMessage(clientIslands, clientBoards, personalities);
        }
        return new GameInstantiationMessage(clientIslands, clientBoards);
    }

    /**
     * Method buildMotherNatureMessage builds a message carrying the update about a Mother Nature movement communicated through a PropertyChangeEvent.
     * @param event PropertyChangeEvent carrying the information about Mother Nature's movement.
     * @return Message instance holding the information about the model update.
     */
    public static Message buildMotherNatureMessage(PropertyChangeEvent event) {
        int motherNaturePosition = (int) event.getNewValue();
        return new MotherNatureMovementUpdateMessage(motherNaturePosition);
    }

    /**
     * Method buildMergeMessage builds a message carrying the update on islands' merges communicated through a PropertyChangeEvent.
     * @param event PropertyChangeEvent notifying a merge event.
     * @return Message instance holding the information about the model update.
     */
    public static Message buildMergeMessage(PropertyChangeEvent event) {
        ArrayList<Island> modelIslands = (ArrayList<Island>) event.getNewValue();
        ArrayList<ClientIsland> clientIslands = new ArrayList<>();
        for (Island modelIsland : modelIslands) {
            ClientIsland newIsland = new ClientIsland(modelIsland.getIslandIndex());
            newIsland.setTowers(modelIsland.getTowers());
            newIsland.setStudents(modelIsland.getStudents());
            newIsland.setMotherNature(modelIsland.isMotherNature());
            newIsland.setNumMergedIslands(modelIsland.getNumMergedIslands());
            clientIslands.add(newIsland);
        }

        return new IslandMergeUpdateMessage(clientIslands);
    }

    /**
     * Method buildLastRoundMessage builds a message notifying player that current round is the last game round.
     * @param event PropertyChangeEvent notifying a last round event.
     * @return Message instance holding the information about the game status.
     */
    public static Message buildLastRoundMessage(PropertyChangeEvent event) {
        boolean oldLastRound = (boolean) event.getOldValue();
        boolean newLastRound = (boolean) event.getNewValue();
        if (oldLastRound != newLastRound)
            return new LastRoundMessage("Questo è l'ultimo round della partita!");
        else return null;
    }

    /**
     * Method buildCloudsRefillMessage builds a message carrying the update on cloud tiles' student refills, communicated through a PropertyChangeEvent.
     * @param event PropertyChangeEvent notifying a cloud refill event.
     * @return Message instance holding the information about the model update.
     */
    public static Message buildCloudsRefillMessage(PropertyChangeEvent event) {
        ArrayList<Cloud> modelClouds = (ArrayList<Cloud>) event.getNewValue();
        ArrayList<ClientCloud> clientClouds = new ArrayList<>();
        for (Cloud modelCloud : modelClouds) {
            ArrayList<Color> modelCloudStudents = modelCloud.getStudents();
            ClientCloud cloud = new ClientCloud(modelCloud.getCloudIndex());
            cloud.setStudents(modelCloudStudents);
            clientClouds.add(cloud);
        }
        return new CloudsRefillMessage(clientClouds);
    }

    /**
     * Method buildCurrentTurnAssistantCardsMessage builds a message carrying the update on selected assistant cards, communicated through a PropertyChangeEvent.
     * @param event PropertyChangeEvent notifying that current assistantCards have changed.
     * @return Message instance holding the information about the model update.
     */
    public static Message buildCurrentTurnAssistantCardsMessage(PropertyChangeEvent event) {
        HashMap<String, Assistant> cards = (HashMap<String, Assistant>) event.getNewValue();
        HashMap<String, Integer> playerToCardMap = new HashMap<>();
        int cardPriority;
        for (String name : cards.keySet()) {
            cardPriority = cards.get(name).getPriority();
            playerToCardMap.put(name, cardPriority);
        }
        return new CurrentTurnAssistantCardsUpdateMessage(playerToCardMap);
    }

    /**
     * Method buildDeckUpdateMessage builds a message carrying the update on a player's assistant deck, communicated through a PropertyChangeEvent.
     * @param event PropertyChangeEvent notifying a player's deck change.
     * @return Message instance holding the information about the model update.
     */
    public static Message buildDeckUpdateMessage(PropertyChangeEvent event) {
        Player updatedPlayer = (Player) event.getNewValue();
        ArrayList<Assistant> updatedDeck = updatedPlayer.getDeck();
        HashMap<Integer, Integer> availableCards = new HashMap<>();
        for (Assistant card : updatedDeck) {
            availableCards.put(card.getPriority(), card.getNumMoves());
        }
        return new AssistantDeckUpdateMessage(updatedPlayer.getNickname(), availableCards);
    }

    /**
     * Method buildIslandTowersMessage builds a message carrying the update on an island's towers number, communicated through a PropertyChangeEvent.
     * @param event PropertyChangeEvent notifying an island change.
     * @return Message instance holding the information about the model update.
     */
    public static Message buildIslandTowersMessage(PropertyChangeEvent event) {
        Island updatedIsland = (Island) event.getNewValue();
        ArrayList<Tower> towers = updatedIsland.getTowers();
        int index = updatedIsland.getIslandIndex();
        return new IslandTowersUpdateMessage(index, towers);
    }

    /**
     * Method buildIslandStudentsMessage builds a message carrying the update on an island's students number, communicated through a PropertyChangeEvent.
     * @param event PropertyChangeEvent notifying an island change.
     * @return Message instance holding the information about the model update.
     */
    public static Message buildIslandStudentsMessage(PropertyChangeEvent event) {
        Island updatedModelIsland = (Island) event.getNewValue();
        ArrayList<Color> students = updatedModelIsland.getStudents();
        int index = updatedModelIsland.getIslandIndex();
        return new IslandStudentsUpdateMessage(index, students);
    }

    /**
     * Method buildIslandBansMessage builds a message carrying the update on an island's bans count, communicated through a PropertyChangeEvent.
     * @param event PropertyChangeEvent notifying an island change.
     * @return Message instance holding the information about the model update.
     */
    public static Message buildIslandBansMessage(PropertyChangeEvent event){
        Island updatedModelIsland = (Island) event.getNewValue();
        int index = updatedModelIsland.getIslandIndex();
        int updatedBans = updatedModelIsland.getBans();
        return new IslandBanUpdateMessage(index, updatedBans);
    }

    /**
     * Method buildPickedCloudMessage builds a message carrying the update on a cloud tile's students number, communicated through a PropertyChangeEvent.
     * @param event PropertyChangeEvent notifying a cloud change.
     * @return Message instance holding the information about the model update.
     */
    public static Message buildPickedCloudMessage(PropertyChangeEvent event) {
        int cloudIndex = (int) event.getNewValue();
        return new CloudUpdateMessage(cloudIndex);
    }

    /**
     * Method buildBoardUpdateMessage builds a message carrying the update on a player's board content, communicated through a PropertyChangeEvent.
     * @param event PropertyChangeEvent notifying a board change.
     * @return Message instance holding the information about the model update.
     */
    public static Message buildBoardUpdateMessage(PropertyChangeEvent event) {
        Player updatedPlayer = (Player) event.getNewValue();
        Board updatedBoard = updatedPlayer.getBoard();
        String updatedOwner = updatedPlayer.getNickname();
        ClientBoard clientBoard = new ClientBoard(updatedOwner);
        clientBoard.setTeam(updatedPlayer.getTeam());
        clientBoard.setTowers(updatedBoard.getTowers());
        clientBoard.setStudentsTable(updatedBoard.getStudentsTable());
        clientBoard.setTeacherTable(updatedBoard.getTeacherTable());
        clientBoard.setLobby(updatedBoard.getLobby());
        clientBoard.setWizardID(updatedBoard.getWizard());
        return new BoardUpdateMessage(updatedOwner, clientBoard);
    }

    /**
     * Method buildActivePersonalityMessage builds a message carrying the update on a Personality card's activation, communicated through a PropertyChangeEvent.
     * @param event PropertyChangeEvent notifying a Personality activation.
     * @return Message instance holding the information about the model update.
     */
    public static Message buildActivePersonalityMessage(PropertyChangeEvent event) {
        int activeCardId = (int) event.getNewValue();
        return new ActivePersonalityMessage(activeCardId);
    }

    /**
     * Method buildNoLongerActivePersonalityMessage builds a message carrying the update on a Personality card's deactivation, communicated through a PropertyChangeEvent.
     * @param event PropertyChangeEvent notifying a Personality deactivation.
     * @return Message instance holding the information about the model update.
     */
    public static Message buildNoLongerActivePersonalityMessage(PropertyChangeEvent event) {
        int inactiveCardId = (int) event.getNewValue();
        return new InactivePersonalityMessage(inactiveCardId);
    }

    /**
     * Method buildCoinsUpdateMessage builds a message carrying the update on coins allocation, communicated through a PropertyChangeEvent.
     * @param event PropertyChangeEvent notifying the exchange of coins.
     * @return Message instance holding the information about the model update.
     */
    public static Message buildCoinsUpdateMessage(PropertyChangeEvent event) {
        try {
            ArrayList<Object> coinsChange = (ArrayList<Object>) event.getNewValue();
            int coins = (int) coinsChange.get(0);
            String player = (String) coinsChange.get(1);
            int reserveCoins = (int) coinsChange.get(2);
            return new CoinsUpdateMessage(coins, player,reserveCoins);
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Method buildPersonalityUsageMessage  builds a message carrying the update on a Personality card's usage, communicated through a PropertyChangeEvent.
     * @param event PropertyChangeEvent notifying the usage of a Personality card.
     * @return Message instance holding the information about the model update.
     */
    public static Message buildPersonalityUsageMessage(PropertyChangeEvent event) {
        Personality modelPersonality = (Personality) event.getNewValue();
        int cardId = modelPersonality.getCharacterId();
        if (modelPersonality instanceof LobbyPersonality)
            return new PersonalityUpdateMessage(cardId, ((LobbyPersonality) modelPersonality).getStudents());
        else if (modelPersonality instanceof BanPersonality)
            return new PersonalityUpdateMessage(cardId, ((BanPersonality) modelPersonality).getBans());
        else return null;
    }
}
