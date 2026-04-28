package model.unite.commande;

import model.*;
import model.unite.Unite;
import java.util.Set;

public class CommandeConstruire extends Commande {
    private static final int TEMPS = 1000;
    private int progression = 0; // nombre de ticks déjà passés à miner le minerai actuel
    private final Batiment batiment; // le bâtiment à construire
    private final Terrain terrain;

    public CommandeConstruire(Batiment batiment, Terrain terrain) {
        this.batiment = batiment;
        this.terrain = terrain;
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float)Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private Batiment scanForDamagedBuilding(Unite unite) {
        // get the closest damaged building in the terrain
        Batiment closest = null;
        float closestDist = Float.MAX_VALUE;
        Set<Batiment> batiments = terrain.getBatimentsEndommages();
        for (Batiment b : batiments) {
            float dist = distance(unite.getPX(), unite.getPY(), 
                                  b.getX() * Case.TAILLE + Case.TAILLE/2, 
                                  b.getY() * Case.TAILLE + Case.TAILLE/2);
            if (closest == null || dist < closestDist) {
                closest = b;
                closestDist = dist;
            }
        }
        return closest;
    }

    @Override
    public boolean executer(Unite unite, double dt) {
        if (progression < TEMPS) {
            progression += dt;
            return false; // pas encore terminé
        }
        if (batiment.getHP() >= batiment.getHPMax()) {
            Batiment nextTarget = scanForDamagedBuilding(unite); 
            if (nextTarget != null) {
                // si il y a un autre bâtiment endommagé, change la cible de construction pour celui-ci
                float targetPX = nextTarget.getX() * Case.TAILLE + Case.TAILLE/2;
                float targetPY = nextTarget.getY() * Case.TAILLE + Case.TAILLE/2;
                unite.ajouterCommande(new CommandeDeplacement(targetPX, targetPY));
                unite.ajouterCommande(new CommandeConstruire(nextTarget, terrain));
            }
            return true; // déjà plein, on considère la commande terminée
        }
        batiment.ajouterHP();
        progression = 0; // reset pour le prochain tick de construction
        return false; // terminé
    }

    @Override
    public String getNom() {
        return "Construire";
    }
}
