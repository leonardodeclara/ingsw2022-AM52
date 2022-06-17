package it.polimi.ingsw.model;

/**
 * Type of towers which can be chosen
 */
public enum Tower {
    BLACK("NERE"), WHITE("BIANCHE"), GREY("GRIGIE");

    private final String translation;

    Tower(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }
}
