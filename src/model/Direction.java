package model;

/**
 * Énumération des directions possibles pour une route.
 * Les routes acheminent les minerais dans une direction spécifique.
 */
public enum Direction {
    /** Direction vers le haut */
    NORD,
    
    /** Direction vers la droite */
    EST,
    
    /** Direction vers le bas */
    SUD,
    
    /** Direction vers la gauche */
    OUEST
    ;

    /** Renvoie une chaine de caractère correspondant à la direction (ex: "Nord ↑") */
    @Override
    public String toString() {
        return switch (this) { 
            case NORD -> "Nord ↑"; 
            case SUD -> "Sud ↓"; 
            case EST -> "Est →"; 
            case OUEST -> "Ouest ←"; 
        };
    }

    public int toInt() {
        return switch (this) { 
            case NORD -> 0; 
            case SUD -> 1; 
            case EST -> 2; 
            case OUEST -> 3; 
        };
    }
}
