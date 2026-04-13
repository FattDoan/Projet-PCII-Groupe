package model.unite.commande;

import model.*;
import model.unite.Unite;

//TODO
public class CommandeMiner extends Commande {
    private final int tx, ty; //  coordonnées de grille du minerait
    private final int tMaitrex, tMaitreY; // coordonnées de grille du bâtiment maître

    public CommandeMiner(Case Minerai, Case tMaitre) {
        this.tx = Minerai.getX(); this.ty = Minerai.getY();
        this.tMaitrex = tMaitre.getX(); this.tMaitreY = tMaitre.getY();
    }

    @Override
    public boolean executer(Unite unite, double dt) {
        return true;
    }

}
