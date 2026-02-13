package model;

/**
 * Représente un bâtiment sur la grille.
 * Les bâtiments peuvent stocker des minerais (foreuse, bâtiment maître, route).
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
     * @throws AssertionError si capacite < 0
     */
    protected Batiment(int capacite) {
        // Validation : la capacité doit être positive
        assert capacite >= 0 : "capacite=" + capacite;
        this.capacite = capacite;
        this.stockage = 0;
    }
    
    /**
     * Vérifie si le bâtiment est vide (aucun minerai stocké).
     * 
     * @return true si aucun minerai stocké, false sinon
     */
    public boolean estVide() {
        return stockage == 0;
    }
    
    /**
     * Vérifie si le bâtiment est plein (capacité maximale atteinte).
     * 
     * @return true si la capacité maximale est atteinte, false sinon
     */
    public boolean estPlein() {
        return stockage >= capacite;
    }
    
    /**
     * Ajoute un minerai au stockage.
     * Précondition : le bâtiment ne doit pas être plein.
     * 
     * @throws AssertionError si le bâtiment est déjà plein
     */
    public void ajouterMinerai() {
        assert !estPlein() : "Bâtiment plein: stockage=" + stockage + ", capacite=" + capacite;
        stockage++;
    }
    
    /**
     * Retire un minerai du stockage.
     * Précondition : le bâtiment ne doit pas être vide.
     * 
     * @throws AssertionError si le bâtiment est déjà vide
     */
    public void retirerMinerai() {
        assert !estVide() : "Bâtiment vide: stockage=" + stockage;
        stockage--;
    }
}
