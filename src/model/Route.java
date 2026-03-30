package model;

import common.Validation;

/**
 * Représente une route pour acheminer les minerais.
 * Une route hérite de Batiment car elle peut contenir un minerai en transit.
 * Chaque route possède une direction pour déplacer les minerais.
 */
public class Route extends Batiment {
    public static final int COUT_CONSTRUCTION = 1;

    /** Direction dans laquelle la route achemine les minerais */
    private final Direction direction;

    /**
     * Crée une nouvelle route avec la direction spécifiée.
     * La capacité est fixée à 1 (un minerai en transit à la fois).
     * Note : La direction ne peut pas être nulle.
     * 
     * @param direction la direction d'acheminement des minerais, ne doit pas être null
     * @throws IllegalArgumentException si direction est null (en validation stricte)
     */
    public Route(Direction direction, int x, int y, Terrain terrain) {
        super(1, x, y, terrain, COUT_CONSTRUCTION);
        // Validation : la direction ne peut pas être nulle
        Validation.requireArgument(direction != null, "direction=null");
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

    @Override
    public void detruire() {
        viderStockage(); // Vide le stockage avant de détruire la route (pour l'instant sert pas à grand chose mais peut éviter des bugs)
        // les minerais sur la route sont détruits atomatiquement quand ils se rendent compte qu'ils ne sont sur rien (voir classe Minerai)
    }
}
