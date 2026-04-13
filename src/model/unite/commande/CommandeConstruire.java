package model.unite.commande;

import model.*;
import model.unite.Unite;

public class CommandeConstruire extends Commande {
    private static final int TEMPS = 1000;
    private int progression = 0; // nombre de ticks déjà passés à miner le minerai actuel
    private final Batiment batiment; // le bâtiment à construire

    public CommandeConstruire(Batiment batiment) {
        this.batiment = batiment;
    }

    @Override
    public boolean executer(Unite unite, double dt) {
        if (progression < TEMPS) {
            progression += dt;
            return false; // pas encore terminé
        }
        if (batiment.getHP() >= batiment.getHPMax()) {
            return true; // déjà plein, on considère la commande terminée
        }
        batiment.ajouterHP();
        progression = 0; // reset pour le prochain tick de construction
        return false; // terminé
    }
}
