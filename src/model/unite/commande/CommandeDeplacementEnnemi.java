package model.unite.commande;

import model.unite.Ennemi;
import model.unite.Unite;
import model.*;

/**
 * Commande de déplacement pour les unités ennemies. 
 * Les ennemis se déplacent automatiquement vers le bâtiment maître ennemi et attaquent les unités ou bâtiments alliés à proximité.
 */
public class CommandeDeplacementEnnemi extends Commande {
    private Terrain terrain; // référence au terrain pour trouver les batiments ennemis
    
    private float batimentMaitrePX, batimentMaitrePY; // coordonnées du bâtiment maître ennemi
    private static final int SCAN_RADIUS = 1; // 2 cases de rayon de scan pour trouver des cibles ennemies

    public CommandeDeplacementEnnemi(Terrain terrain) {
        this.terrain = terrain;
        Batiment batimentMaitre = terrain.getBatimentMaitre();

        this.batimentMaitrePX = batimentMaitre.getX() * Case.TAILLE + Case.TAILLE / 2;
        this.batimentMaitrePY = batimentMaitre.getY() * Case.TAILLE + Case.TAILLE / 2;
    }


    // Recherche autour de l'unite pour trouver la cible ennemie la plus proche (batiment ou unite)
    public Selectable scanNearby(Unite unite) {
        Selectable closestTarget = null;
        float closestDist = Float.MAX_VALUE;

        int centerX = (int) (unite.getPX() / Case.TAILLE);
        int centerY = (int) (unite.getPY() / Case.TAILLE);

        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
            for (int dy = -SCAN_RADIUS; dy <= SCAN_RADIUS; dy++) {
                int x = centerX + dx;
                int y = centerY + dy;

                Selectable target = terrain.getSelectableAt(x, y);
                if (target != null && !(target instanceof Ennemi)) { // s'assure que ce n'est pas l'unité elle-même
                    float targetPX = target.getPX();
                    float targetPY = target.getPY();
                    float dist = (float) Math.hypot(targetPX - unite.getPX(), targetPY - unite.getPY());
                    if (dist < closestDist) {
                        closestDist = dist;
                        closestTarget = target;
                    }
                }  
            }
        }
        return closestTarget;
    }

    @Override
    public boolean executer(Unite unite, double dt) {
        float dx = batimentMaitrePX - unite.getPX();
        float dy = batimentMaitrePY - unite.getPY();
        float dist = (float) Math.hypot(dx, dy);
        float step = unite.getSpeed() * (float) dt;

        // Search around for the nearest building or unit to attack
        Selectable target = scanNearby(unite);
        if (target != null) {
            // If found the target, move towards it and attack
            // we dont move directly to the target, 
            // we move towards it 
            // (in the direction vector from unite to target)
            // and stop at attack range (which is Case.TAILLE/2)
            float dirX = target.getPX() - unite.getPX();
            float dirY = target.getPY() - unite.getPY();
            float distToTarget = (float) Math.hypot(dirX, dirY);
            if (distToTarget > Ennemi.ATTACK_RANGE) {
                // Move towards the target
                // but the new position should be at the attack range (Case.TAILLE/2) from the target
                float newX = target.getPX() - (dirX / distToTarget) * Ennemi.ATTACK_RANGE;
                float newY = target.getPY() - (dirY / distToTarget) * Ennemi.ATTACK_RANGE;
                unite.ajouterCommande(new CommandeDeplacement(newX, newY)); // move towards the target but stop at attack range
            }
            
            unite.ajouterCommande(new CommandeAttaquer(target)); // attack the target 
            return true; // Commande terminée, on peut passer à la suivante (attaque)
        }

        unite.avancer((dx / dist) * step, (dy / dist) * step);
        return false;
    }

    @Override
    public String getNom() {
        return "Deplacement";
    }
}
