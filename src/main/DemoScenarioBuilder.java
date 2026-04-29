package main;

import common.AsyncExecutor;
import model.Batiment;
import model.Direction;
import model.Foreuse;
import model.Route;
import model.Stockage;
import model.Terrain;
import model.TypeCase;

/**
 * Construit un scénario de démonstration sur un terrain donné.
 */
public final class DemoScenarioBuilder {
    private DemoScenarioBuilder() {
    }

    public static void apply(Terrain terrain) {
        int mid = terrain.getTaille() / 2;

        // --- Courbe en L (vers stockage n°1) ---
        setupForeuse(terrain, mid - 5, mid - 5);
        placeBatimentTermine(terrain, new Route(Direction.EST, mid - 4, mid - 5, terrain));
        placeBatimentTermine(terrain, new Route(Direction.EST, mid - 3, mid - 5, terrain));
        placeBatimentTermine(terrain, new Route(Direction.SUD, mid - 2, mid - 5, terrain));
        placeBatimentTermine(terrain, new Route(Direction.SUD, mid - 2, mid - 4, terrain));

        Stockage stockage1 = new Stockage(mid - 2, mid - 3, terrain);
        placeBatimentTermine(terrain, stockage1);
        AsyncExecutor.runAsync(stockage1);

        for (int y = mid - 4; y < mid; y++) {
            placeBatimentTermine(terrain, new Route(Direction.SUD, mid - 5, y, terrain));
        }
        for (int x = mid - 5; x < mid; x++) {
            placeBatimentTermine(terrain, new Route(Direction.EST, x, mid, terrain));
        }

        // --- Courbe en S (vers stockage n°2) ---
        setupForeuse(terrain, mid + 5, mid - 5);
        placeBatimentTermine(terrain, new Route(Direction.OUEST, mid + 4, mid - 5, terrain));
        placeBatimentTermine(terrain, new Route(Direction.SUD, mid + 3, mid - 5, terrain));
        placeBatimentTermine(terrain, new Route(Direction.SUD, mid + 3, mid - 4, terrain));
        placeBatimentTermine(terrain, new Route(Direction.EST, mid + 3, mid - 3, terrain));
       
        Stockage stockage2 = new Stockage(mid + 4, mid - 3, terrain);
        placeBatimentTermine(terrain, stockage2);
        AsyncExecutor.runAsync(stockage2);

        // --- Forme en U vers le QG ---
        setupForeuse(terrain, mid + 1, mid - 2);
        for (int y = mid - 3; y >= mid - 4; y--) {
            placeBatimentTermine(terrain, new Route(Direction.NORD, mid + 1, y, terrain));
        }
        placeBatimentTermine(terrain, new Route(Direction.OUEST, mid + 1, mid - 5, terrain));
        for (int y = mid - 5; y < mid; y++) {
            placeBatimentTermine(terrain, new Route(Direction.SUD, mid, y, terrain));
        }

        setupForeuse(terrain, mid + 5, mid);
        for (int x = mid + 1; x < mid + 5; x++) {
            placeBatimentTermine(terrain, new Route(Direction.OUEST, x, mid, terrain));
        }
    }

    private static void placeBatimentTermine(Terrain terrain, Batiment batiment) {
        batiment.terminerConstruction();
        terrain.getCase(batiment.getX(), batiment.getY()).setBatiment(batiment);
    }

    private static void setupForeuse(Terrain terrain, int x, int y) {
        terrain.definirTypeCase(x, y, TypeCase.MINERAI);
        Foreuse foreuse = new Foreuse(x, y, terrain);
        placeBatimentTermine(terrain, foreuse);
        //AsyncExecutor.runLongLived(foreuse);
        AsyncExecutor.runAsync(foreuse);
    }
}
