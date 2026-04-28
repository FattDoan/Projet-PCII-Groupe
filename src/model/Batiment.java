package model;

import common.Validation;
import java.util.Objects;

/**
 * Représente un bâtiment sur la grille.
 * Les bâtiments peuvent stocker des minerais (foreuse, bâtiment maître, route).
 */
public abstract class Batiment{
    /** Coordonnées du bâtiment sur la grille (peuvent être utilisées pour la position du bâtiment) */
    private final int x;
    private final int y;
    /** Quantité actuelle de minerai stocké */
    private int stockage;
    /** Capacité maximale de stockage du bâtiment */
    private final int capacite;
    private final Terrain terrain;

    /** bool finir de construitr */
    private boolean fini = false;

    /** hp */
    private final int hpMax;
    private int hp;

    /**
     * Crée un nouveau bâtiment avec une capacité de stockage définie.
     * Note : La capacité doit être positive.
     * 
     * @param capacite la capacité maximale de stockage, doit être >= 0
     * @throws IllegalArgumentException si capacite < 0 (en validation stricte)
     */
    protected Batiment(int capacite, int x, int y, Terrain terrain, int hpMax, boolean fini) {
        // Validation : la capacité doit être positive
        Validation.requireArgument(capacite >= 0, "capacite=" + capacite);
        Validation.requireArgument(x >= 0 && y >= 0, "Position batiment invalide: x=" + x + ", y=" + y);
        this.capacite = capacite;
        this.stockage = 0;
        this.terrain = Objects.requireNonNull(terrain, "terrain=null");
        this.x = x;
        this.y = y;
        this.hpMax = hpMax;
        this.fini = fini;
        
        if (fini) {
            this.hp = hpMax; // batiment déjà construit, donc hp au max
        } else {
            this.hp = 1; // construction en cours, hp initial à 1 (peut être ajusté selon les besoins)
        }
        terrain.notifyBatimentUpdated(this);
    }


    /****** GETTERS ******/

    /** Renvoie le type de bâtiment (USINE, FOREUSE, STOCKAGE, BATIMENT_MAITRE, ROUTE) */
    public abstract TypeBatiment getType();

    /** Renvoie le coût de construction du bâtiment */
    public abstract int getCost();

    /** Renvoie la quantité de minerai stockée actuellement */
    public synchronized int getStockage() {
        return stockage;
    }

    /** Renvoie la capacité maximale de stockage du bâtiment */
    public int getCapaciteMax() {
        return capacite;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /** Getter pour le terrain */
    public Terrain getTerrain() {
        return terrain;
    }

    /** Le nombre de PV actuel du bâtiment */
    public int getHP() {
        return hp;
    }
    /** Le nombre de PV maximum du bâtiment */
    public int getHPMax() {
        return hpMax;
    }
    /** Renvoie vrai si le bâtiment est à son maximum de PV */
    public boolean atFullHP() {
        return hp == hpMax;
    }

    /** Vérifie si le bâtiment est détruit (points de vie à zéro ou moins). */
    public boolean isDestroyed() {
        return hp <= 0;
    }

    /** Applique des dégâts au bâtiment, réduisant ses points de vie. */
    public synchronized void receiveDamage(int degats) {
        hp -= degats;
        terrain.notifyBatimentUpdated(this);
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

    /** Renvoie vrai si la construction du batiment est terminee. */
    public synchronized boolean estFini() {
        return fini;
    }
    /** Renvoie vrai si le bâtiment est détruit (points de vie à zéro). */
    public synchronized boolean estDetruit() {
        return hp <= 0;
    }

    /** Renvoie vrai si le bâtiment est en construction (pas fini mais pas détruit). */
    public synchronized boolean estEnConstruction() {
        return !fini && hp > 0;
    }

    /***** SETTERS *****/

    /** Marque la construction du batiment comme terminee. */
    public synchronized void terminerConstruction() {
        this.fini = true;
    }

    /** Effectue les actions nécessaires pour détruire le bâtiment */
    public abstract void detruire();

    /** Vide entièrement le stockage du bâtiment */
    protected synchronized void viderStockage() {
        this.stockage = 0;
    }

    /**
     * Ajoute un minerai au stockage.
     * Précondition : le stokage actuel plus la quantité ajoutée 
     * ne doit pas dépasser la capacité maximale du bâtiment.
     * 
     * @throws IllegalStateException si le bâtiment est déjà plein (en validation stricte)
     */
    public synchronized void ajouterMinerai(int quantite) {
        Validation.requireArgument(quantite >= 0, "quantite=" + quantite);
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
        Validation.requireArgument(quantite >= 0, "quantite=" + quantite);
        Validation.requireState(
            getStockage() >= quantite,
            "Bâtiment vide: stockage=" + stockage + ", retirer quantite=" + quantite
        );
        stockage -= quantite;
    }


    public synchronized boolean essayerAjouterMinerai(int quantite) {
        if (quantite < 0) {
            return false;
        }
        if (getStockage() + quantite <= getCapaciteMax()) {
            stockage += quantite;
            return true;
        }
        return false;
    }

    public synchronized boolean essayerRetirerMinerai(int quantite) {
        if (quantite < 0) {
            return false;
        }
        if (getStockage() >= quantite) {
            stockage -= quantite;
            return true;
        }
        return false;
    }

    public synchronized void ajouterHP() {
        if (hp < hpMax) {
            hp += 1;
            terrain.notifyBatimentUpdated(this);
            if (hp == hpMax) {
                fini = true; // construction terminée quand hp atteint le max
            }
        }
    }
}
