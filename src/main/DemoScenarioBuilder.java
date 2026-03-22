package main;

import common.AsyncExecutor;
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
        terrain.getCase(mid - 4, mid - 5).setBatiment(new Route(Direction.EST, mid - 4, mid - 5, terrain));
        terrain.getCase(mid - 3, mid - 5).setBatiment(new Route(Direction.EST, mid - 3, mid - 5, terrain));
        terrain.getCase(mid - 2, mid - 5).setBatiment(new Route(Direction.SUD, mid - 2, mid - 5, terrain));
        terrain.getCase(mid - 2, mid - 4).setBatiment(new Route(Direction.SUD, mid - 2, mid - 4, terrain));
        terrain.getCase(mid - 2, mid - 3).setBatiment(new Stockage(mid - 2, mid - 3, terrain));

        for (int y = mid - 4; y < mid; y++) {
            terrain.getCase(mid - 5, y).setBatiment(new Route(Direction.SUD, mid - 5, y, terrain));
        }
        for (int x = mid - 5; x < mid; x++) {
            terrain.getCase(x, mid).setBatiment(new Route(Direction.EST, x, mid, terrain));
        }

        // --- Courbe en S (vers stockage n°2) ---
        setupForeuse(terrain, mid + 5, mid - 5);
        terrain.getCase(mid + 4, mid - 5).setBatiment(new Route(Direction.OUEST, mid + 4, mid - 5, terrain));
        terrain.getCase(mid + 3, mid - 5).setBatiment(new Route(Direction.SUD, mid + 3, mid - 5, terrain));
        terrain.getCase(mid + 3, mid - 4).setBatiment(new Route(Direction.SUD, mid + 3, mid - 4, terrain));
        terrain.getCase(mid + 3, mid - 3).setBatiment(new Route(Direction.EST, mid + 3, mid - 3, terrain));
        terrain.getCase(mid + 4, mid - 3).setBatiment(new Stockage(mid + 4, mid - 3, terrain));

        // --- Forme en U vers le QG ---
        setupForeuse(terrain, mid + 1, mid - 2);
        for (int y = mid - 3; y >= mid - 4; y--) {
            terrain.getCase(mid + 1, y).setBatiment(new Route(Direction.NORD, mid + 1, y, terrain));
        }
        terrain.getCase(mid + 1, mid - 5).setBatiment(new Route(Direction.OUEST, mid + 1, mid - 5, terrain));
        for (int y = mid - 5; y < mid; y++) {
            terrain.getCase(mid, y).setBatiment(new Route(Direction.SUD, mid, y, terrain));
        }

        setupForeuse(terrain, mid + 5, mid);
        for (int x = mid + 1; x < mid + 5; x++) {
            terrain.getCase(x, mid).setBatiment(new Route(Direction.OUEST, x, mid, terrain));
        }
    }

    private static void setupForeuse(Terrain terrain, int x, int y) {
        terrain.definirTypeCase(x, y, TypeCase.MINERAI);
        Foreuse foreuse = new Foreuse(x, y, terrain);
        terrain.getCase(x, y).setBatiment(foreuse);
        AsyncExecutor.runLongLived(foreuse);
    }
}
