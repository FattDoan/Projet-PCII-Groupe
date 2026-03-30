package model.unite.commande;

import model.*;
import model.unite.Unite;

//TODO
public class CommandeMiner extends Commande {
    private final int tx, ty; // destination en coordonnées de grille

    public CommandeMiner(int tx, int ty) {
        this.tx = tx; this.ty = ty;
    }

    @Override
    public boolean executer(Unite unite, double dt) {
        return true;
    }

}
