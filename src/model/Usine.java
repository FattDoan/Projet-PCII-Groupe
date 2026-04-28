package model;

/** Un bâtiment qui produit des unités à partir de minerai */
public class Usine extends Batiment implements Runnable {

    public static final int COUT_CONSTRUCTION = 200;
    public static final int HP_MAX = 30;
    public static final int CAPACITE = 20; // Stockage interne pour minerais

    public static final int COUT_PRODUCTION = 5; // Minerais requis par unité produite
    public static final int DELAI_PRODUCTION_MS = 2500; // Cadence de production

    private volatile boolean running = true;
    private volatile boolean paused = false;

    /**
     * Crée une nouvelle usine avec la capacité par défaut.
     * 
     * @param x la coordonnée x du bâtiment sur la grille
     * @param y la coordonnée y du bâtiment sur la grille
     * @param terrain le terrain auquel appartient ce bâtiment
     */
    public Usine(int x, int y, Terrain terrain) {
        super(CAPACITE, x, y, terrain, HP_MAX, false);
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(DELAI_PRODUCTION_MS);

                if (paused || !estFini() || estDetruit()) {
                    continue;
                }

                int[] spawn = trouverCaseSpawn();
                if (spawn == null) {
                    continue; // Pas d'espace libre pour sortir l'unite
                }

                // Consomme les minerais puis cree l'unite.
                if (!essayerRetirerMinerai(COUT_PRODUCTION)) {
                    continue; // Pas assez de minerais au moment de produire
                }
                getTerrain().addUnite(new model.unite.Ouvrier(spawn[0], spawn[1], getTerrain()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private int[] trouverCaseSpawn() {
        int x0 = getX();
        int y0 = getY();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int x = x0 + dx;
                int y = y0 + dy;
                if (x < 0 || y < 0 || x >= getTerrain().getTaille() || y >= getTerrain().getTaille()) {
                    continue;
                }
                Case c = getTerrain().getCase(x, y);
                if (c != null && c.estAccessible()) {
                    return new int[]{x, y};
                }
            }
        }
        return null;
    }

    @Override
    public TypeBatiment getType() {
        return TypeBatiment.USINE;
    }

    @Override
    public int getCost() {
        return COUT_CONSTRUCTION;
    }

    @Override
    public void detruire() {
        viderStockage();
        running = false;
    }

    public boolean isRunning() {
        return running && estFini() && !Thread.currentThread().isInterrupted();
    }

    public boolean isPaused() {
        return paused;
    }

    public void togglePause() {
        paused = !paused;
    }
}
