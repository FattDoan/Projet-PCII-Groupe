package tests;

import common.AsyncExecutor;
import model.*;
import view.*;
import controller.*;

public class ReactionClicTest {

    public static void main(String[] args) {
        int size = 100;
        Terrain terrain = new Terrain(size);
        Fenetre fenetre = new Fenetre("Test Controller", terrain);
        new TimerView(fenetre);

        int mid = size / 2;

        setupForeuse(terrain, mid - 2, mid);

        terrain.getCase(mid - 1, mid)
               .setBatiment(new Route(Direction.EST, mid - 1, mid, terrain));
        new GameController(terrain, fenetre.getAffichage());
    }

    private static void setupForeuse(Terrain t, int x, int y) {
        t.definirTypeCase(x, y, TypeCase.MINERAI);
        Foreuse f = new Foreuse(x, y, t);
        t.getCase(x, y).setBatiment(f);
        AsyncExecutor.runLongLived(f);
    }
}
