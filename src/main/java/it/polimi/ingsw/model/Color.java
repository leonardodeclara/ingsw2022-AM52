package it.polimi.ingsw.model;

import java.util.HashMap;

/**
 * Colors which identifies students and teachers' objects.
 */
public enum Color {
    PINK (0,"ROSA"), GREEN (1,"VERDE"), BLUE(2,"BLU"), YELLOW(3,"GIALLO"), RED(4,"ROSSO");
    private final int index;
    private final String translation;

    /**
     * Constructor creates a new color instance.
     * @param index: number representative for the color's index.
     */
    Color(int index,String translation) {
        this.index=index;
        this.translation = translation;
    }

    /**
     * @return the index of a specific color
     */
    public int getIndex() {
        return index;
    }

    public static Color getById(int index) {
        for(Color c : values()) {
            if(c.index == index) return c;
        }
        return null;
    }

    public String translateToItalian(){
        return translation;
    }
}
