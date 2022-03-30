package it.polimi.ingsw;

/**
 * It represents the game's student pieces.
 * Each teacher is uniquely identified by its color.
 */
public class Student {
    private final Color color;

    /**
     * Constructor creates a student istance.
     * @param color: color of the teacher.
     */
    public Student(Color color){
        this.color = color;
    }

    /**
     * Method getColor returns the student's color.
     * @return color of the student's istance.
     */
    public Color getColor() {
        return color;
    }

}
