package model;

/**
 * Représente une route pour acheminer les minerais.
 * Une route hérite de Batiment car elle peut contenir un minerai en transit.
 * Chaque route possède une direction pour déplacer les minerais.
 */
public class Route extends Batiment {
    /** Direction dans laquelle la route achemine les minerais */
    private final Direction direction;

    /**
     * Crée une nouvelle route avec la direction spécifiée.
     * La capacité est fixée à 1 (un minerai en transit à la fois).
     * Note : La direction ne peut pas être nulle.
     * 
     * @param direction la direction d'acheminement des minerais, ne doit pas être null
     * @throws AssertionError si direction est null
     */
    public Route(Direction direction) {
        super(1);
        // Validation : la direction ne peut pas être nulle
        assert direction != null : "direction=null";
        this.direction = direction;
    }

    /**** GETTERS ****/

    /** Renvoie la direction d'acheminement des minerais sur cette route */
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public TypeBatiment type() {
        return TypeBatiment.ROUTE;
    }
}
