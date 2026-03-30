package model.unite.commande;

import model.unite.Unite;

public abstract class Commande {
    // Appelée à chaque tick de jeu tant que la commande n'est pas terminée (retourne false).
    public abstract boolean executer(Unite unite, double deltaTime);
}
