package model.unite.commande;

import model.*;
import model.unite.Ennemi;
import model.unite.Ouvrier;
import model.unite.Unite;

// Attack either a unit or a building (Selectable given)
public class CommandeDefendre extends Commande {
    private static final int TEMPS = 1000; // temps nécessaire pour infliger des dégâts significatifs
                                           // (x point de dégâts par seconde, 
                                           // ou x est la puissance d'attaque de l'unité)

    private int progression = 0; // nombre de ticks déjà passés à attaquer la cible actuelle
   
    private final float defensePX; // position à défendre
    private final float defensePY;
    private Unite cibleEnnemi; // la cible ennemie à attaquer (si trouvée)
                                
    public CommandeDefendre(float defensePX, float defensePY) {
        this.defensePX = defensePX;
        this.defensePY = defensePY;
    }

    private double distance(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public Unite scanForEnemies(Unite unite) {
        // scan les unités ennemies à proximité (dans un certain rayon, ici 2 cases)
        for (Unite u : unite.getTerrain().getUnites()) {
            if (u instanceof Ennemi && distance(unite.getPX(), unite.getPY(), u.getPX(), u.getPY()) < Case.TAILLE * 2) {
                return u; // retourne la première unité ennemie trouvée dans le rayon
            }
        }
        return null; // aucune unité ennemie trouvée
    }

    // Currently simple:
    // once at the position,
    // if theres enemy units in range, attack them,
    // once they destroyed, go back to the position and wait for new enemies to come 
    // (unless new command is given)
    @Override
    public boolean executer(Unite unite, double dt) {
        if (cibleEnnemi == null || cibleEnnemi.isDestroyed()) {
            // si pas de cible ou cible détruite, cherche une nouvelle cible ennemie à proximité
            cibleEnnemi = scanForEnemies(unite);
        }
        if (cibleEnnemi != null) {
            // code just like Ennemi, go to attack the enemy
            // at the attack range, attack, otherwise move towards the enemy
            float dx = cibleEnnemi.getPX() - unite.getPX();
            float dy = cibleEnnemi.getPY() - unite.getPY();
            float dist = (float) Math.hypot(dx, dy);
            if (dist <= Ouvrier.DEFENSE_RANGE) {
                if (cibleEnnemi.isDestroyed()) {
                    cibleEnnemi = null; // cible détruite, reset pour chercher une nouvelle cible au prochain tick
                    progression = 0; // reset de la progression d'attaque
                    return false; // pas encore terminé, continue à chercher des ennemis à attaquer
                }

                // at attack range, attack
                if (progression < TEMPS) {
                    progression += dt;
                    return false; // pas encore terminé
                }
                cibleEnnemi.receiveDamage(Ouvrier.DEGATS); // inflige les dégâts de l'unité à la cible
                progression = 0; // reset pour le prochain tick d'attaque
                return false; // pas encore terminé, continue à attaquer jusqu'à ce que la cible soit détruite
            } else {
                // Move towards the target
                // but the new position should be at the attack range from the target
                float newX = cibleEnnemi.getPX() - (dx / dist) * Ouvrier.DEFENSE_RANGE;
                float newY = cibleEnnemi.getPY() - (dy / dist) * Ouvrier.DEFENSE_RANGE;
                unite.addToFrontCommande(new CommandeDeplacement(newX, newY)); // move towards the target but stop at attack range 
            }
        }   
        
        // if no enemy, move towards the defense position
        // but only if we are not already there (to avoid jittering)
        float dx = defensePX - unite.getPX();
        float dy = defensePY - unite.getPY();
        float dist = (float) Math.hypot(dx, dy);
        if (dist == 0) {
            return false; // already at the position, just wait for enemies to come
        }
        unite.addToFrontCommande(new CommandeDeplacement(defensePX, defensePY)); // move towards the defense position
        return false; // pas encore terminé, continue à se déplacer vers la position de défense           
    }

    @Override
    public String getNom() {
        return "Defendre";
    }
}
