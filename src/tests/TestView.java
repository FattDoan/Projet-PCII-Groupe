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

        Route r1 = new Route(Direction.EST, mid - 2, mid, terrain);
        r1.terminerConstruction();
        terrain.getCase(mid - 2, mid).setBatiment(r1);

        Route r2 = new Route(Direction.EST, mid - 1, mid, terrain);
        r2.terminerConstruction();
        terrain.getCase(mid - 1, mid).setBatiment(r2);

        // --- Test si les routes faisent des virages correctement ---
        Route r3 = new Route(Direction.NORD, mid, mid - 1, terrain);
        r3.terminerConstruction();
        terrain.getCase(mid, mid - 1).setBatiment(r3);
        Route r4 = new Route(Direction.SUD, mid, mid + 1, terrain);
        r4.terminerConstruction();
        terrain.getCase(mid, mid + 1).setBatiment(r4);
        Route r5 = new Route(Direction.OUEST, mid - 1, mid - 1, terrain);
        r5.terminerConstruction();
        terrain.getCase(mid - 1, mid - 1).setBatiment(r5);
        Route r6 = new Route(Direction.EST, mid + 1, mid - 1, terrain);
        r6.terminerConstruction();
        terrain.getCase(mid + 1, mid - 1).setBatiment(r6);
    
         new GameController(terrain, fenetre.getAffichage());
    }

    private static void setupForeuse(Terrain t, int x, int y) {
              t.definirTypeCase(x, y, TypeCase.MINERAI);
              Foreuse f = new Foreuse(x, y, t);
              f.terminerConstruction();
        t.getCase(x, y).setBatiment(f);
        AsyncExecutor.runAsync(f);
    }
}
