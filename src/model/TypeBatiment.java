package model;

/** Les types des différents bâtiments */
public enum TypeBatiment {
    BATIMENT_MAITRE,
    ROUTE,
    FOREUSE,
    STOCKAGE, 
    USINE
    ;

    public int toInt() {
        return switch (this) { 
            case BATIMENT_MAITRE -> 0; 
            case ROUTE -> 1; 
            case FOREUSE -> 2; 
            case STOCKAGE -> 3; 
            case USINE -> 4; 
        };
    }
};