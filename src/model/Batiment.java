package model;

/**
 * Représente un bâtiment sur la grille.
 * Les bâtiments peuvent stocker des minerais (foreuse, bâtiment maître, route).
 */
public abstract class Batiment {
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

    public abstract TypeBatiment type();

    /** 
     * Retourne la quantité de minerai actuellement stockée dans le bâtiment.
     * 
     * @return la quantité de minerai stockée
     */
    public int getQuantiteStockee() {
        return stockage;
    }

    /**
     * Retourne la capacité maximale de stockage du bâtiment.
     * 
     * @return la capacité maximale de stockage
     */
    public int getCapaciteMax() {
        return capacite;
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
     * Précondition : le stokage actuel plus la quantité ajoutée 
     * ne doit pas dépasser la capacité maximale du bâtiment.
     * 
     * @throws AssertionError si le bâtiment est déjà plein
     */

    public void ajouterMinerai(int quantite) {
        assert getQuantiteStockee() + quantite <= getCapaciteMax()
            : "Bâtiment plein: stockage=" + stockage + ", ajouter quantite=" + quantite + ", capacite=" + capacite;
        stockage += quantite;
    }
    
    /**
     * Retire un minerai du stockage.
     * Précondition : le stockage actuel doit être suffisant pour retirer la quantité demandée.
     * 
     * @throws AssertionError si le bâtiment est déjà vide
     */
    public void retirerMinerai(int quantite) {
        assert getQuantiteStockee() >= quantite 
            : "Bâtiment vide: stockage=" + stockage + ", retirer quantite=" + quantite;
        stockage -= quantite;
    }

}
