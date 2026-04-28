package model.unite.commande;

import model.*;
import model.unite.Unite;

public class CommandeDeposit extends Commande {
    private static final int TEMPS = 1000;  // chaque second on dépose une unité de minerai au batiment maitre
    private int progression = 0; // nombre de ticks déjà passés à miner le minerai actuel

    private Batiment batimentMaitre; // bâtiment maître où déposer le minerai
    private float minePX = 0; // coordonnées de la mine (à ajuster selon la carte)
    private float minePY = 0;
    public CommandeDeposit(Batiment batimentMaitre, float minePX, float minePY) {
        this.batimentMaitre = batimentMaitre;
        this.minePX = minePX;
        this.minePY = minePY;
    }

    @Override
    public boolean executer(Unite unite, double dt) {
        if (unite.StockageVide()) {
            unite.ajouterCommande(new CommandeDeplacement(minePX, minePY)); // retourne à la mine pour miner plus
            unite.ajouterCommande(new CommandeMiner(this.batimentMaitre)); // ajoute une commande de minage après le déplacement
            return true; // déjà plein, on considère la commande terminée
        }
        if (progression < TEMPS) {
            progression += dt;
            return false; // pas encore terminé
        }
        if (!batimentMaitre.estPlein()) {
            unite.retirerStockage(); // retire un minerai de l'unité
            batimentMaitre.ajouterMinerai(1); // ajoute un minerai au bâtiment maître
            progression = 0; // reset pour le prochain minerai
        }
        return false;
    }

    @Override
    public String getNom() {
        return "Deposit";
    }
}
