package model.unite.commande;

import model.*;
import model.unite.Unite;

public class CommandeRetrieve extends Commande {
    private static final int TEMPS = 500;  
    private int progression = 0; 

    private Batiment batiment; // bâtiment où récupérer le minerai

    public CommandeRetrieve(Batiment batiment) {
        this.batiment = batiment;

    }

    @Override
    public boolean executer(Unite unite, double dt) {
        if (batiment.getStockage() == 0 || unite.StockagePlein()) {
            return true; // pas de minerai à récupérer ou on est plein
                         // on considère la commande terminée
        }
        if (progression < TEMPS) {
            progression += dt;
            return false; // pas encore terminé
        }
        if (batiment.getStockage() > 0) {
            unite.addStockage(); // retire un minerai de l'unité
            batiment.retirerMinerai(1); // retire un minerai du bâtiment
            progression = 0; // reset pour le prochain minerai
        }
        return false;
    }

    @Override
    public String getNom() {
        return "Récupérer";
    }
}
