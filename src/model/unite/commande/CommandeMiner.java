package model.unite.commande;

import model.*;
import model.unite.Unite;

//TODO
public class CommandeMiner extends Commande {
    private static final int TEMPS = 1000;
    private int progression = 0; // nombre de ticks déjà passés à miner le minerai actuel

    public CommandeMiner() {
    }

    @Override
    public boolean executer(Unite unite, double dt) {
        if (unite.StockagePlein()) {
            return true; // déjà plein, on considère la commande terminée
        }
        if (progression != TEMPS) {
            progression += dt;
            return false; // pas encore terminé
        }
        unite.addStockage();
        progression = 0; // reset pour le prochain minerai
        return false;
    }

}
