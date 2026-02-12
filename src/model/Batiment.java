package model;

/**
 * Représente un bâtiment sur la grille.
 * Les bâtiments peuvent stocker des minerais (mine, stockage, bâtiment maître, route).
 */
public class Batiment {
    /** Quantité actuelle de minerai stocké */
    private int stockage;
    
    /** Capacité maximale de stockage du bâtiment */
    private final int capacite;

    /**
     * Crée un nouveau bâtiment avec une capacité de stockage définie.
     * Note : La capacité doit être positive.
     * 
     * @param capacite la capacité maximale de stockage, doit être >= 0
     */
    public Batiment(int capacite) {
        // Validation : la capacité doit être positive
        assert capacite >= 0 : "capacite=" + capacite;
        this.capacite = capacite;
        this.stockage = 0;
    }
}
