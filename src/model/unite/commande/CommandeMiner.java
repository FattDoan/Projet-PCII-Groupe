package model.unite.commande;

import model.*;
import model.unite.Unite;

/**
 * Commande pour miner un minerai. L'unité doit être positionnée sur une case contenant du minerai pour que la commande soit exécutée.
 * L'unité mine pendant un certain temps (TEMPS) et ajoute le minerai à son stockage. 
 * Si le stockage est plein, l'unité retourne au bâtiment maître pour déposer le minerai avant de continuer à miner.
 */
public class CommandeMiner extends Commande {
    private static final int TEMPS = 1000;
    private int progression = 0; // nombre de ticks déjà passés à miner le minerai actuel

    private Batiment batimentMaitre; // bâtiment maître où déposer le minerai
    public CommandeMiner(Batiment batimentMaitre) {
        this.batimentMaitre = batimentMaitre;
    }

    @Override
    public boolean executer(Unite unite, double dt) {
        int gx = unite.getGX();
        int gy = unite.getGY();
        Terrain terrain = unite.getTerrain();
        if (gx < 0 || gy < 0 || gx >= terrain.getTaille() || gy >= terrain.getTaille()) {
            return true;
        }
        Case currentCase = terrain.getCase(gx, gy);
        if (currentCase == null || !currentCase.aMinerai()) {
            return true; // ne mine pas hors d'un gisement
        }
        if (unite.StockagePlein()) {
            unite.ajouterCommande(new CommandeDeplacement(batimentMaitre.getX() * Case.TAILLE, 
                                                          batimentMaitre.getY() * Case.TAILLE)); // retourne au bâtiment maître pour déposer
            unite.ajouterCommande(new CommandeDeposit(batimentMaitre, unite.getPX(), unite.getPY())); // ajoute une commande de dépôt après le minage
            return true; // déjà plein, on considère la commande terminée
        }
        if (progression < TEMPS) {
            progression += dt;
            return false; // pas encore terminé
        }
        unite.addStockage();
        progression = 0; // reset pour le prochain minerai
        return false;
    }

    @Override 
    public String getNom() {
        return "Miner";
    }
}
