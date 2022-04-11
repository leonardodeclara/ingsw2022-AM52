package it.polimi.ingsw.model;

public class Assistant {
    private final int numMoves;
    private final int priority;
    private final int wizard;

    /**
     * Constructor creates an Assistant instance.
     *
     */

    public Assistant(int numMoves, int priority, int wizard) {
        this.numMoves=numMoves;
        this.priority=priority;
        this.wizard=wizard;
    }

    public int getWizard() {
        return wizard;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public int getPriority() {
        return priority;
    }
}
