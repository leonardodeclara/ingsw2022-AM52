package it.polimi.ingsw;

/**
 * It represents the game's teacher pieces.
 * Each teacher is uniquely identified by its color.
 */
public class Teacher {
    private final Color color;

    /**
     * Constructor creates a teacher istance.
     * @param color: color of the teacher.
     */
    public Teacher(Color color) {
        this.color = color;
    }

    /**
     * Method getColor returns the teacher's color.
     * @return color of the teacher's istance.
     */
    public Color getColor() {
        return color;
    }
}
