package main;

import common.AsyncExecutor;
import controller.*;
import model.*;
import view.*;

public class Main {
    public static void main(String[] args) {
        int size = 50;
        Terrain terrain = new Terrain(size);
        Fenetre fenetre = new Fenetre("Advanced Logistics Demo", terrain);
        new TimerView(fenetre);
        
        int mid = size / 2;

        // --- "L-CURVE" (To Stockage #1) ---
        // Starts at (mid-10, mid-10), goes EST, then turns SUD to a Stockage
        setupForeuse(terrain, mid - 5, mid - 5);
        terrain.getCase(mid - 4, mid - 5).setBatiment(new Route(Direction.EST, mid - 4, mid - 5, terrain));
        terrain.getCase(mid - 3, mid - 5).setBatiment(new Route(Direction.EST, mid - 3, mid - 5, terrain));
        terrain.getCase(mid - 2, mid - 5).setBatiment(new Route(Direction.SUD, mid - 2, mid - 5, terrain)); // THE TURN
        terrain.getCase(mid - 2, mid - 4).setBatiment(new Route(Direction.SUD, mid - 2, mid - 4, terrain));
        terrain.getCase(mid - 2, mid - 3).setBatiment(new Stockage(mid - 2, mid - 3, terrain));

        for (int y = mid - 4; y < mid ; y++) {
            terrain.getCase(mid - 5, y).setBatiment(new Route(Direction.SUD, mid - 5, y, terrain));
        }
        for (int x = mid - 5; x < mid; x++) {
            terrain.getCase(x, mid).setBatiment(new Route(Direction.EST, x, mid, terrain));
        }

        // --- "S-CURVE" (To Stockage #2) ---
        // Starts North, moves OUEST, then turns SUD
        setupForeuse(terrain, mid + 5, mid - 5);
        terrain.getCase(mid + 4, mid - 5).setBatiment(new Route(Direction.OUEST, mid + 4, mid - 5, terrain));
        terrain.getCase(mid + 3, mid - 5).setBatiment(new Route(Direction.SUD, mid + 3, mid - 5, terrain)); // TURN 1
        terrain.getCase(mid + 3, mid - 4).setBatiment(new Route(Direction.SUD, mid + 3, mid - 4, terrain));
        terrain.getCase(mid + 3, mid - 3).setBatiment(new Route(Direction.EST, mid + 3, mid - 3, terrain));  // TURN 2
        terrain.getCase(mid + 4, mid - 3).setBatiment(new Stockage(mid + 4, mid - 3, terrain));

        // --- THE UPWARD U SHAPE TO THE HQ (in the middle of the map) ---
        setupForeuse(terrain, mid+1, mid-2);
        for (int y = mid-3; y >= mid-4; y--) {
            terrain.getCase(mid+1, y).setBatiment(new Route(Direction.NORD, mid+1, y, terrain));
        }
        terrain.getCase(mid+1, mid-5).setBatiment(new Route(Direction.OUEST, mid+1, mid-5, terrain)); // TURN 1
        for (int y = mid-5; y < mid; y++) {
            terrain.getCase(mid, y).setBatiment(new Route(Direction.SUD, mid, y, terrain));
        }

        setupForeuse(terrain, mid + 5, mid);
        for (int x = mid+1; x < mid+5; x++) {
            terrain.getCase(x, mid).setBatiment(new Route(Direction.OUEST, x, x, terrain));
        }

        new GameController(terrain, fenetre.getAffichage());
    }

    private static void setupForeuse(Terrain t, int x, int y) {
        t.setCase(x, y, new Case(x, y, TypeCase.MINERAI));
        Foreuse f = new Foreuse(x, y, t);
        t.getCase(x, y).setBatiment(f);
        new Thread(f).start();
    }
}
