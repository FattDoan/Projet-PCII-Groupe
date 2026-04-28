package model.unite.commande;

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

                if (x < 0 || y < 0 || x >= terrain.getTaille() || y >= terrain.getTaille()) {
                    continue; // hors de la carte
                }
            //TODO
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

        if (dist <= step) {
            unite.avancer(dx, dy);   // ajuste exactement sur la destination
        }

        // Recherche la cible la plus proche (batiment ou unite) a attaquer



        unite.avancer((dx / dist) * step, (dy / dist) * step);
        return false;
    }

}
