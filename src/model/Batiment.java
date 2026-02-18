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

    /** Renvoie le type de bâtiment (USINE, FOREUSE, STOCKAGE, BATIMENT_MAITRE, ROUTE) */
    public abstract TypeBatiment type();


    /****** GETTERS ******/

    /** Renvoie la quantité de minerai stockée actuellement */
    public int getStockage() {
        return stockage;
    }

    /** Renvoie la capacité maximale de stockage du bâtiment */
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
        assert getStockage() + quantite <= getCapaciteMax()
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
        assert getStockage() >= quantite 
            : "Bâtiment vide: stockage=" + stockage + ", retirer quantite=" + quantite;
        stockage -= quantite;
    }

}
