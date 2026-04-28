package model.unite.commande;

import model.unite.Unite;

public abstract class Commande {
    // Appelee a chaque tick de jeu tant que la commande n'est pas terminee (retourne false).
    public abstract boolean executer(Unite unite, double deltaTime);

    public abstract String getNom();
}
