package model.unite.commande;

import model.*;
import model.unite.Unite;
import view.Camera;


public class CommandeMiner extends Commande {
    private static final int TEMPS = 1000;
    private int progression = 0; // nombre de ticks déjà passés à miner le minerai actuel

    private Batiment batimentMaitre; // bâtiment maître où déposer le minerai
    public CommandeMiner(Batiment batimentMaitre) {
        this.batimentMaitre = batimentMaitre;
    }

    @Override
    public boolean executer(Unite unite, double dt) {
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

}
