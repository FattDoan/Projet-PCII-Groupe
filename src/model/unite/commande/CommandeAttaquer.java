package model.unite.commande;

import model.*;
import model.unite.Ennemi;
import model.unite.Unite;

// Attack either a unit or a building (Selectable given)
public class CommandeAttaquer extends Commande {
    private static final int TEMPS = 1000; // temps nécessaire pour infliger des dégâts significatifs
                                           // (x point de dégâts par seconde, 
                                           // ou x est la puissance d'attaque de l'unité)
    private int progression = 0; // nombre de ticks déjà passés à attaquer la cible actuelle

    private final Selectable cible; // la cible à attaquer (peut être une unité ou un bâtiment)

    public CommandeAttaquer(Selectable cible) {
        this.cible = cible;
    }

    @Override
    public boolean executer(Unite unite, double dt) {
        if (cible.isDestroyed()) {
            // après la destruction, recommence à chercher une cible
            unite.ajouterCommande(new CommandeDeplacementEnnemi(unite.getTerrain())); 
            return true; // cible déjà détruite, on considère la commande terminée
        }
        if (progression < TEMPS) {
            progression += dt;
            return false; // pas encore terminé
        }
        cible.receiveDamage(Ennemi.DEGATS); // inflige les dégâts de l'unité à la cible
        progression = 0; // reset pour le prochain tick d'attaque
        return false; // pas encore terminé, continue à attaquer jusqu'à ce que la cible soit détruite 
    }

    @Override
    public String getNom() {
        return "Attaquer";
    }
}
