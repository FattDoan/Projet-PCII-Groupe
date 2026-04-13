package model;

import common.Validation;

/**
 * Représente une route pour acheminer les minerais.
 * Une route hérite de Batiment car elle peut contenir un minerai en transit.
 * Chaque route possède une direction pour déplacer les minerais.
 */
public class Route extends Batiment {
    public static final int COUT_CONSTRUCTION = 1;
    public static final int HP_MAX = 5; // Points de vie maximum de la route

    /** Direction dans laquelle la route achemine les minerais */
    private Direction direction;

    /**
     * Crée une nouvelle route avec la direction spécifiée.
     * La capacité est fixée à 1 (un minerai en transit à la fois).
     * Note : La direction ne peut pas être nulle.
     * 
     * @param direction la direction d'acheminement des minerais, ne doit pas être null
     * @throws IllegalArgumentException si direction est null (en validation stricte)
     */
    public Route(Direction direction, int x, int y, Terrain terrain) {
        super(1, x, y, terrain, HP_MAX, false); // La route a une capacité de 1, un coût de construction défini, et des HP max de 100.
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

    public int getCost() {
        return COUT_CONSTRUCTION;
    }

    @Override
    public void detruire() {
        viderStockage(); // Vide le stockage avant de détruire la route (pour l'instant sert pas à grand chose mais peut éviter des bugs)
        // les minerais sur la route sont détruits atomatiquement quand ils se rendent compte qu'ils ne sont sur rien (voir classe Minerai)
    }

    /** 
     * Change la direction de la route à la valeur spécifiée. 
     * @param d la nouvelle direction. Si la direction est nulle, ne fait rien.
    */
    public void changeDirection(Direction d) {
        if (d == null || d == direction) return;
        else direction = d;
    }

}
