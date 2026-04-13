package model.unite.commande;

import model.unite.Unite;
import view.Affichage;
import model.*;

public class CommandeDeplacementEnnemi extends Commande {
    private Terrain terrain; // référence au terrain pour trouver les batiments ennemis
    
    private float batimentMaitrePX, batimentMaitrePY; // coordonnées du bâtiment maître ennemi
    private static final int SCAN_RADIUS = 1; // 2 cases de rayon de scan pour trouver des cibles ennemies

    public CommandeDeplacementEnnemi(Terrain terrain) {
        Batiment batimentMaitre = terrain.getBatimentMaitre();

        this.batimentMaitrePX = batimentMaitre.getX() * Affichage.TAILLE_CASE;
        this.batimentMaitrePY = batimentMaitre.getY() * Affichage.TAILLE_CASE;
    }


    //recherche autour de l'unité pour trouver la cible ennemie la plus proche (batiment ou unité)
    public Selectable scanNearby(Unite unite) {
        Selectable closestTarget = null;
        float closestDist = Float.MAX_VALUE;

        int centerX = (int) (unite.getPX() / Affichage.TAILLE_CASE);
        int centerY = (int) (unite.getPY() / Affichage.TAILLE_CASE);

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
            unite.avancer(dx, dy);   // snap exactly to destination
        }

        // Search around for the nearest building or unit to attack



        unite.avancer((dx / dist) * step, (dy / dist) * step);
        return false;
    }

}
