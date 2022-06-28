package it.polimi.ingsw.model;

/**
 * Colors which identifies students and teachers' objects.
 */
public enum Color {
    PINK (0), GREEN (1), BLUE(2), YELLOW(3), RED(4);
    private final int index;

    /**
     * Constructor creates a new color instance.
     * @param index: number representative for the color's index.
     */
    Color(int index) {
        this.index=index;
    }

    /**
     * @return the index of a specific color
     */
    public int getIndex() {
        return index;
    }

}
