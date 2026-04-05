package model.unite.commande;

import model.*;
import common.AsyncExecutor;
import model.unite.Unite;
import javax.swing.SwingUtilities;
import java.util.*;
import model.unite.pathfinding.*;
import model.unite.Unite;

public class CommandeDeplacement extends Commande {
    private final int tx, ty; // destination en coordonnées de grille
    private final float finalPx, finalPy; // destination en coordonnées pixels

    public CommandeDeplacement(int tx, int ty, float finalPx, float finalPy) {
        this.tx = tx; this.ty = ty;
        this.finalPx = finalPx; this.finalPy = finalPy;
    }

    @Override
    public boolean executer(Unite unite, double dt) {
        // Si le chemin n'est pas encore calculé et qu'on n'est pas déjà en train de le calculer, 
        // on lance une tâche asynchrone pour le trouver.
        if (unite.getChemin() == null && !unite.isCheminEnAttente()) {
            unite.setCheminEnAttente(true);
            AsyncExecutor.runAsync(() -> {
                List<int[]> result = BFS.trouver(unite.getTerrain(),
                        unite.getGX(), unite.getGY(), tx, ty);
                SwingUtilities.invokeLater(() -> {
                    unite.setChemin(result.isEmpty() ? null : result);
                    unite.setCheminEnAttente(false);
                });
            });
            return false; // waiting
        }
        if (unite.isCheminEnAttente()) return false; // still computing
        if (unite.cheminTermine()) {
            unite.setChemin(null); // clear path for next command
            return true;       // arrived
        }

        // 2. Check if next waypoint is still passable (dynamic obstacle)
        int[] wp = unite.getChemin().get(unite.getProchainWP());
        Case nextCase = unite.getTerrain().getCase(wp[0], wp[1]);
        if (nextCase.aBatiment() && nextCase.getBatiment().type() != TypeBatiment.ROUTE) {
            // Obstacle appeared — re-path
            unite.setChemin(null);
            return false;
        }

        // 3. Move toward next waypoint in pixel space
        // if it's the last waypoint, target the exact final pixel coordinates instead of the center of the case
        boolean isLastWaypoint = (unite.getProchainWP() == unite.getChemin().size() - 1);
        float targetPX = isLastWaypoint ? finalPx : wp[0] * Unite.CASE_SIZE + Unite.CASE_SIZE / 2f;
        float targetPY = isLastWaypoint ? finalPy : wp[1] * Unite.CASE_SIZE + Unite.CASE_SIZE / 2f;
        float dx = targetPX - unite.getPX();
        float dy = targetPY - unite.getPY();
        float dist = (float) Math.hypot(dx, dy);
        float step = unite.getSpeed() * (float) dt;

        if (dist <= step) {
            unite.avancer(dx, dy); // snap to center
            unite.avancerWaypoint();
        } else {
            unite.avancer((dx / dist) * step, (dy / dist) * step);
        }
        return false;
    }
}
