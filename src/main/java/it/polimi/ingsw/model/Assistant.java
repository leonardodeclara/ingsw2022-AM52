package it.polimi.ingsw.model;

/**
 * This class represents the Assistant card
 * Each Player has a deck of 12 assistant cards
 */

public class Assistant {
    private final int numMoves;
    private final int priority;
    private final int wizard;

    /**
     * Constructor creates an Assistant instance.
     * @param numMoves: number of moves that Mother Nature can take when a card is played
     * @param priority: number representing the priority in playing the next round
     * @param wizard: number that identifies the wizard behind each cards' deck
     */

    public Assistant(int numMoves, int priority, int wizard) {
        this.numMoves=numMoves;
        this.priority=priority;
        this.wizard=wizard;
    }

    /**
     * This method returns a number which identifies the cards' deck
     * @return the number that identifies the wizard
     */

    public int getWizard() {
        return wizard;
    }

    /**
     * This method returns the number of moves that Mother Nature can do according to the card played
     * @return the number of permissed moves
     */
    public int getNumMoves() {
        return numMoves;
    }

    /**
     * This method returns the priority associated with the card
     * @return a number that represents the priority
     */
    public int getPriority() {
        return priority;
    }
}
