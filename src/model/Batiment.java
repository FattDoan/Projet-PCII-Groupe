package model;

import common.Validation;

/**
 * Représente un bâtiment sur la grille.
 * Les bâtiments peuvent stocker des minerais (foreuse, bâtiment maître, route).
 */
public abstract class Batiment{
    /** Coordonnées du bâtiment sur la grille (peuvent être utilisées pour la position du bâtiment) */
    protected int x;
    protected int y;
    /** Quantité actuelle de minerai stocké */
    private int stockage;
    
    /** Capacité maximale de stockage du bâtiment */
    private final int capacite;
    private Terrain terrain;
    /** Getter pour le terrain */
    public Terrain getTerrain() {
        return terrain;
    }

    /**
     * Crée un nouveau bâtiment avec une capacité de stockage définie.
     * Note : La capacité doit être positive.
     * 
     * @param capacite la capacité maximale de stockage, doit être >= 0
        * @throws IllegalArgumentException si capacite < 0 (en validation stricte)
     */
    protected Batiment(int capacite, int x, int y, Terrain terrain) {
        // Validation : la capacité doit être positive
        Validation.requireArgument(capacite >= 0, "capacite=" + capacite);
        this.capacite = capacite;
        this.stockage = 0;
        this.terrain = terrain; // Le terrain est fourni directement au constructeur.
        this.x = x;
        this.y = y;
    }

    /** Renvoie le type de bâtiment (USINE, FOREUSE, STOCKAGE, BATIMENT_MAITRE, ROUTE) */
    public abstract TypeBatiment type();


    /****** GETTERS ******/

    /** Renvoie la quantité de minerai stockée actuellement */
    public synchronized int getStockage() {
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
    public synchronized boolean estVide() {
        return stockage == 0;
    }

    /**
     * Vérifie si le bâtiment est plein (capacité maximale atteinte).
     * 
     * @return true si la capacité maximale est atteinte, false sinon
     */
    public synchronized boolean estPlein() {
        return stockage >= capacite;
    }




    /**
     * Ajoute un minerai au stockage.
     * Précondition : le stokage actuel plus la quantité ajoutée 
     * ne doit pas dépasser la capacité maximale du bâtiment.
     * 
    * @throws IllegalStateException si le bâtiment est déjà plein (en validation stricte)
     */

    public synchronized void ajouterMinerai(int quantite) {
        Validation.requireState(
            getStockage() + quantite <= getCapaciteMax(),
            "Bâtiment plein: stockage=" + stockage + ", ajouter quantite=" + quantite + ", capacite=" + capacite
        );
        stockage += quantite;
    }

    /**
     * Retire un minerai du stockage.
     * Précondition : le stockage actuel doit être suffisant pour retirer la quantité demandée.
     * 
    * @throws IllegalStateException si le bâtiment est déjà vide (en validation stricte)
     */
    public synchronized void retirerMinerai(int quantite) {
        Validation.requireState(
            getStockage() >= quantite,
            "Bâtiment vide: stockage=" + stockage + ", retirer quantite=" + quantite
        );
        stockage -= quantite;
    }


    public synchronized boolean essayerAjouterMinerai(int quantite) {
        if (getStockage() + quantite <= getCapaciteMax()) {
            stockage += quantite;
            return true;
        }
        return false;
    }

     public synchronized boolean essayerRetirerMinerai(int quantite) {
        if (getStockage() >= quantite) {
            stockage -= quantite;
            return true;
        }
        return false;
    }
}
