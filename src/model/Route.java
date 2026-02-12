package model;

/**
 * Représente une route pour acheminer les minerais.
 * Une route hérite de Batiment car elle peut contenir un minerai en transit.
 * Chaque route possède une direction pour déplacer les minerais.
 */
public class Route extends Batiment {
    /** Direction dans laquelle la route achemine les minerais */
    private Direction direction;

    /**
     * Crée une nouvelle route avec la direction spécifiée.
     * La capacité est fixée à 1 (un minerai en transit à la fois).
     * 
     * @param direction la direction d'acheminement des minerais
     */
    public Route(Direction direction) {
        super(1);
        this.direction = direction;
    }
}
