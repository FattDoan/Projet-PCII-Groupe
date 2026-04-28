package model.unite.commande;

import model.unite.Ennemi;
import model.unite.Unite;
import model.*;

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
                /*
                System.out.println("Ennemi scanning nearby at case (" + x + ", " + y + ")");
                System.out.println("ENTER getTaille");
                int taille;
                synchronized (terrain) {
                    System.out.println("INSIDE terrain lock");
                    taille = terrain.getTaille();
                }
                System.out.println("EXIT terrain lock");
                if (x < 0 || y < 0 || x >= terrain.getTaille() || y >= terrain.getTaille()) {
                    System.out.println("Case (" + x + ", " + y + ") is out of bounds, skipping...");
                    continue; // hors de la carte
                }
                */

                System.out.println("Attempting to scan case (" + x + ", " + y + ") for targets...");
                // Vérifie si sur la case (x, y) il y a une unité ennemie ou un bâtiment ennemi
                Selectable target = terrain.getSelectableAt(x, y);
                System.out.println("Scanned case (" + x + ", " + y + ") and found: " + (target != null ? target.getClass().getSimpleName() : "none"));
                if (target != null && target != unite) { // s'assure que ce n'est pas l'unité elle-même
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
        System.out.println("Ennemi scanned nearby and found closest target: " + (closestTarget != null ? "FOUND " : "none"));
        return closestTarget;
    }

    @Override
    public boolean executer(Unite unite, double dt) {
        System.out.println("Ennemi executing CommandeDeplacementEnnemi towards (" + batimentMaitrePX + ", " + batimentMaitrePY + ")");
        float dx = batimentMaitrePX - unite.getPX();
        float dy = batimentMaitrePY - unite.getPY();
        float dist = (float) Math.hypot(dx, dy);
        float step = unite.getSpeed() * (float) dt;

        // Search around for the nearest building or unit to attack
        Selectable target = scanNearby(unite);
        System.out.println("Ennemi scanned for targets and found: " + (target != null ? target.getClass().getSimpleName() : "none"));
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
