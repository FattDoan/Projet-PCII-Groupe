package tests;

import common.AsyncExecutor;
import model.*;
import view.*;
import controller.*;

public class TestView {

    public static void main(String[] args) {
        int size = 20;
        Terrain terrain = new Terrain(size);
        Fenetre fenetre = new Fenetre("Test View", terrain);
        new TimerView(fenetre);

        int mid = size / 2;

       // --- Chaîne simple foreuse -> route -> stockage ---
        setupForeuse(terrain, mid - 3, mid);

        terrain.getCase(mid - 2, mid)
               .setBatiment(new Route(Direction.EST, mid - 2, mid, terrain));

        terrain.getCase(mid - 1, mid)
               .setBatiment(new Route(Direction.EST, mid - 1, mid, terrain));

        // --- Test si les routes faisent des virages correctement ---
        terrain.getCase(mid, mid - 1)
               .setBatiment(new Route(Direction.NORD, mid, mid - 1, terrain));
        terrain.getCase(mid, mid + 1)
               .setBatiment(new Route(Direction.SUD, mid, mid + 1, terrain));
        terrain.getCase(mid - 1, mid - 1)
               .setBatiment(new Route(Direction.OUEST, mid - 1, mid - 1, terrain));
        terrain.getCase(mid + 1, mid - 1)
               .setBatiment(new Route(Direction.EST, mid + 1, mid - 1, terrain));
    
         new GameController(terrain, fenetre.getAffichage());
    }

    private static void setupForeuse(Terrain t, int x, int y) {
              t.definirTypeCase(x, y, TypeCase.MINERAI);
        Foreuse f = new Foreuse(x, y, t);
        t.getCase(x, y).setBatiment(f);
              AsyncExecutor.runLongLived(f);
    }
}
