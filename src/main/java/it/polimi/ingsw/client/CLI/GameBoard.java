package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;

import java.io.PrintStream;
import java.util.*;

/**
 * Class GameBoard holds basic information about the game state in order to render its content through CLI and GUI interfaces.
 * If carries information about game parameters as the players' number and the game mode and game elements such as islands, clouds and boards.
 */
public class GameBoard {
    private int numberOfPlayers;
    private boolean expertGame;
    private String nickname;
    private String winner;
    private ArrayList<Integer> availableWizards; //sono 4 e a ogni propagazione diminuiscono
    private ArrayList<Tower> availableTowers;
    private ArrayList<ClientIsland> islands;
    private ArrayList<ClientCloud> clouds;
    private HashMap<String,ClientBoard> clientBoards;
    private ArrayList<ClientPersonality> personalities;
    private ClientPersonality activePersonality;
    private PrintStream outputStream;
    private int coins;

    /**
     * Constructor GameBoard creates a GameBoard instance by receiving as input a PrintStream object.
     * @param outputStream PrintStream instance to which game elements are printed on CLI interfaces.
     */
    public GameBoard(PrintStream outputStream){
        availableWizards = new ArrayList<>();
        availableTowers = new ArrayList<>();
        islands = new ArrayList<>();
        clouds = new ArrayList<>();
        clientBoards = new HashMap<>();
        personalities = new ArrayList<>();
        availableWizards.add(1);
        availableWizards.add(2);
        availableWizards.add(3);
        availableWizards.add(4);
        this.outputStream = outputStream;
    }

    /**
     * Constructor GameBoard creates a GameBoard instance.
     */
    public GameBoard(){
        availableWizards = new ArrayList<>();
        availableTowers = new ArrayList<>();
        islands = new ArrayList<>();
        clouds = new ArrayList<>();
        clientBoards = new HashMap<>();
        personalities = new ArrayList<>();
        availableWizards.add(0);
        availableWizards.add(1);
        availableWizards.add(2);
        availableWizards.add(3);
    }

    /**
     * Method instantiateGameElements adds newly instantiated game elements to GameBoard in order to show them on GUI and CLI.
     * @param newIslands ClientIsland instances representing Island tiles.
     * @param boards ClientBoard instances representing School boards.
     * @param personalities ClientPersonality instances representing Personality cards.
     */
    public void instantiateGameElements(ArrayList<ClientIsland> newIslands,
                                        HashMap<String,ClientBoard> boards,
                                        ArrayList<ClientPersonality> personalities){
        islands.addAll(newIslands);
        for (String player: boards.keySet()){
            boards.get(player).setGB(this);
            clientBoards.put(player, boards.get(player));
        }

        for(int i = 0; i < numberOfPlayers; i++){
            clouds.add(new ClientCloud(i));
        }
        if(expertGame){
            this.personalities = new ArrayList<>(personalities);
            for (ClientPersonality card: this.personalities)
                System.out.println("GameBoard: una delle carte personaggio è "+card.getCardID());
            coins=20-numberOfPlayers;
        }
    }

    /**
     * Method print shows the entirety of game elements on CLI interfaces.
     */
    public void print(){
        if(outputStream!=null){
            outputStream.print("\033[H\033[2J");
            outputStream.flush();
            printClientBoards();
            printClouds();
            printIslands();
            if (isExpertGame()){
                outputStream.println("BANK'S COINS: " + coins);
                printPersonalityCards(); //sistemare, deve stampare solo le carte estratte, non tutte
            }
        }
    }

    /**
     * Method printClientBoards prints the school boards' content on CLI interfaces.
     */
    private void printClientBoards(){ //sarebbe meglio se ogni componente avesse un metodo print e qui venisse chiamato solo quello
        outputStream.println("*****************************************BOARDS DEI GIOCATORI*****************************************************");
        for(ClientBoard clientBoard : clientBoards.values()){
            clientBoard.print();
        }
        outputStream.println();
    }

    /**
     * Method printIslands prints islands' content on CLI interfaces.
     */
    private void printIslands() {
        outputStream.println("*****************************************ISOLE*****************************************");
        for (ClientIsland island : islands) {
            island.print();
        }
        outputStream.println("\n");
    }

    /**
     * Method printClouds prints clouds' content on CLI interfaces.
     */
    private void printClouds(){
        outputStream.println("*****************************************NUVOLE*****************************************");
        for (ClientCloud cloud : clouds){
            cloud.print();
        }
        outputStream.println("\n");
    }

    /**
     * Method printPersonalityCards prints personalities' content on CLI interfaces.
     */
   private void printPersonalityCards(){
        outputStream.println("AVAILABLE PERSONALITY CARDS:");
        for(ClientPersonality personality : personalities){
            personality.print();
        }
       outputStream.println();
        if(activePersonality!=null)
            outputStream.println("ACTIVE PERSONALITY CARD:"+activePersonality.getCardID());
    }

    /**
     * Method setTurnCard sets the content of current turn assistant cards in order to show them on interfaces.
     * @param playersCards player-card associations.
     */
    public void setTurnCard(HashMap<String,Integer> playersCards){
        if (playersCards.isEmpty())
            for (String player: clientBoards.keySet())
                clientBoards.get(player).setCurrentCard(0);
        else
            for (Map.Entry<String,Integer> playerToCard: playersCards.entrySet()){
                clientBoards.get(playerToCard.getKey()).setCurrentCard(playerToCard.getValue());
                if (clientBoards.get(playerToCard.getKey()).getDeck().containsKey(playerToCard.getValue()))
                    clientBoards.get(playerToCard.getKey()).getDeck().remove(playerToCard.getValue());
                }
    }

    /**
     * Method setTurnCard sets selected players' assistant deck content.
     * @param cards priority-number of moves association of player's cards.
     */
    public void setPlayerDeck(String player, HashMap<Integer, Integer> cards){
        clientBoards.get(player).setDeck(cards);
    }

    /**
     * Method setIslandStudents sets students tiles for the selected island
     * @param islandIndex island's identification number.
     * @param students island's new student tiles.
     */
    public void setIslandStudents(int islandIndex, ArrayList<Color> students){
        getIslandByIndex(islandIndex).setStudents(students);
    }

    /**
     * Method setIslandTowers sets the selected island's towers.
     * @param islandIndex island's identification number.
     * @param towers island's new towers count.
     */
    public void setIslandTowers(int islandIndex, ArrayList<Tower> towers){
        getIslandByIndex(islandIndex).setTowers(towers);
    }

    /**
     * Method emptyCloud clears a cloud's students content.
     * @param cloudIndex cloud's identification number.
     */
    public void emptyCloud(int cloudIndex){
        getCloudByIndex(cloudIndex).getStudents().clear();

    }

    /**
     * Method changeMNPosition changes the island's Mother Nature flag, denoting its presence.
     * @param islandIndex island's identification number.
     */
    public void changeMNPosition(int islandIndex){
        islands.stream().filter(ClientIsland::isMotherNature).findFirst().ifPresent(oldMNIsland -> oldMNIsland.setMotherNature(false));
        getIslandByIndex(islandIndex).setMotherNature(true);
    }

    /**
     * Method setUpdatedClientBoard updates a player's board content.
     * @param player name of the player whose board is being updated.
     * @param clientBoard board instance carrying the updated information.
     */
    public void setUpdatedClientBoard(String player, ClientBoard clientBoard){
        ClientBoard board = clientBoards.get(player);
        board.setStudentsTable(clientBoard.getStudentsTable());
        board.setLobby(clientBoard.getLobby());
        board.setTeacherTable(clientBoard.getTeacherTable());
        board.setTowers(clientBoard.getTowers());
        board.setTeam(clientBoard.getTeam());
    }

    /**
     * Method updateCoins updates a player's coins count and the bank's coins count.
     * @param coins updated player's coins count.
     * @param player player whose coins count is being updated.
     * @param reserveCoins updated bank's coins count.
     */
    public void updateCoins(int coins, String player, int reserveCoins){
        clientBoards.get(player).setCoins(coins);
        this.coins = reserveCoins;
    }

    /**
     * Method setIslandBans sets the selected island's bans count.
     * @param islandId island's identification number.
     * @param bansCount island' bans count.
     */
    public void setIslandBans(int islandId, int bansCount){
        getIslandByIndex(islandId).setBans(bansCount);
    }

    /**
     * Method getIslandByIndex returns the island whose identification number is equal to the input parameter.
     * @param index island's identification number.
     * @return ClientIsland instance whose identification number is equal to the input parameter, null if not found.
     */
    public ClientIsland getIslandByIndex(int index){
        for (ClientIsland island : islands){
            if (island.getIslandIndex()==index)
                return island;
        }
        return null;
    }

    /**
     * Method getCloudByIndex returns the island whose identification number is equal to the input parameter.
     * @param index cloud's identification number.
     * @return ClientCloud instance whose identification number is equal to the input parameter, null if not found.
     */
    public ClientCloud getCloudByIndex(int index){
        for (ClientCloud cloud: clouds){
            if (cloud.getCloudIndex()==index)
                return cloud;
        }
        return null;
    }

    /**
     * Method getPlayersNicknames returns the list of players' names.
     * @return list containg the names of the players taking part in the game.
     */
    public List<String> getPlayersNicknames() {
        List<String> players = new ArrayList<>();
        players.addAll(clientBoards.keySet());
        return players;
    }

    /**
     * Method getPersonalityById returns the Personality whose identification number is equal to the input parameter.
     * @param cardId Personality's identification number.
     * @return ClientPersonality instance whose identification number is equal to the input parameter, null if not found.
     */
    public ClientPersonality getPersonalityById(int cardId){
        for (ClientPersonality personality: personalities)
            if (personality.getCardID()==cardId)
                return personality;
        return null;
    }

    /**
     * Method isPersonalityCardBeenPlayed checks whether a Personality Card has been actived during current turn.
     * @return true if a card has been actived, false otherwise.
     */
    public boolean isPersonalityCardBeenPlayed(){
        return activePersonality != null;
    }

    /**
     * Method setActivePersonality sets the Personality card with identification number activatedPersonality to active.
     * It also updates its cost in order to make it visible to all players.
     * @param activatedPersonality newly activated card identification number.
     */
    public void setActivePersonality(int activatedPersonality) {
        Optional<ClientPersonality> activeCard = personalities.stream()
                                                .filter(clientPersonality -> clientPersonality.getCardID() == activatedPersonality)
                                                .findFirst();
        if(activeCard.isPresent()){
            activePersonality = activeCard.get();
            activePersonality.updateCost();
            activePersonality.setActive(true);
        }
        else
            new Throwable().printStackTrace();
        //TODO: rivedere
        // non dovrebbe mai accadere quindi mettiamo eccezione così nel caso in runtime salta fuori un bug
    }

    /**
     * Method visualizeLastRoundMessage shows a message communicating players that current round is the final one.
     * @param message LastRoundMessage content.
     */
    public void visualizeLastRoundMessage(String message){
        if (outputStream!=null)
            outputStream.println(message);
    }

    /**
     * Method resetActivePersonality updates personalities status in order to let playrs know that a card is no longer active.
     * @param inactivePersonality newly deactivated card identification number.
     */
    public void resetActivePersonality(int inactivePersonality){
        activePersonality.setActive(false);
        this.activePersonality = null;
    }

    /**
     * Method updatePersonality updates a Personality card features, such as student tiles and bans.
     * @param cardId updated Personality's identification number.
     * @param students updated Personality's students tiles.
     * @param bans updated Personality's bans count.
     */
    public void updatePersonality(int cardId, ArrayList<Color> students, int bans){
        for (ClientPersonality card: personalities)
            if (cardId==card.getCardID()){
                if (students!=null)
                    card.setStudents(students);
                else if (bans!=-1)
                    card.setBans(bans);
            }
    }

    /**
     * Method getMotherNatureDistance computes the distance as number of steps from current Mother Nature's island and
     * the island with identification number islandId.
     * @param islandId island's identification number.
     * @return number of steps from current Mother Nature's island and islandId island.
     */
    public int getMotherNatureDistance(int islandId){
        int destinationIndex = islands.indexOf(getIslandByIndex(islandId));
        int fromIndex;
        for (ClientIsland island: islands)
            if (island.isMotherNature()){
                fromIndex =islands.indexOf(island);
                if (fromIndex<destinationIndex)
                    return destinationIndex-fromIndex;
                else if (fromIndex>destinationIndex)
                    return (islands.size()-(fromIndex-destinationIndex));
            }
        return -1;
    }

    /**
     * Method getTurnCards returns the association between players and their current round's Assistant card.
     * @return HashMap with player-card associations.
     */
    public HashMap<String, Integer> getTurnCards(){
        HashMap<String,Integer> playerToCards =new HashMap<>();
        for (Map.Entry<String,ClientBoard> playerToBoard: clientBoards.entrySet())
            playerToCards.put(playerToBoard.getKey(), playerToBoard.getValue().getCurrentCard());
        return playerToCards;
    }

    public ClientPersonality getActivePersonality() {
        return activePersonality;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public void setExpertGame(boolean expertGame) {
        this.expertGame = expertGame;
    }

    public void setAvailableWizards(ArrayList<Integer> availableWizards) {
        this.availableWizards = availableWizards;
    }

    public void setAvailableTowers(ArrayList<Tower> availableTowers) {
        this.availableTowers = availableTowers;
    }

    public void setIslands(ArrayList<ClientIsland> islands) {
        this.islands = islands;
    }

    public void setClouds(ArrayList<ClientCloud> clouds) {
        this.clouds = clouds;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getWinner() {
        return winner;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public boolean isExpertGame() {
        return expertGame;
    }

    public ArrayList<Integer> getAvailableWizards() {
        return availableWizards;
    }

    public ArrayList<Tower> getAvailableTowers() {
        return availableTowers;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getCoins() {
        return coins;
    }

    public ArrayList<ClientCloud> getClouds() {
        return clouds;
    }

    public HashMap<String,ClientBoard> getClientBoards() {
        return clientBoards;
    }

    public ClientBoard getOwningPlayerClientBoard(){return clientBoards.get(nickname);}

    public ArrayList<ClientPersonality> getPersonalities() {
        return personalities;
    }

    public ArrayList<ClientIsland> getIslands() {
        return islands;
    }

    public void setWinner(String winner){
        this.winner=winner;
    }
}


