package model;

/** Un bâtiment qui produit des unités à partir de minerai */
public class Usine extends Batiment implements Runnable {

    public static final int COUT_CONSTRUCTION = 20;
    public static final int HP_MAX = 25;
    public static final int CAPACITE = 20; // Stockage interne pour minerais

    public static final int COUT_PRODUCTION = 10; // Minerais requis par unité produite
    public static final int DELAI_PRODUCTION_MS = 3000; // Cadence de production

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
                getTerrain().addUnite(new model.unite.Ouvrier(spawn[0] + Case.TAILLE/2, spawn[1] + Case.TAILLE/2, getTerrain()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private int[] trouverCaseSpawn() {
        int x0 = getX();
        int y0 = getY();
        
        // Spawn spirally outwards from the center of the building
        // ideally not on top of the building itself, 
        // but also not on top of other units
        for (int r = 1; r <= 2; r++) { // Check a radius of 2 around the building
            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    int x = x0 + dx;
                    int y = y0 + dy;
                    if (getTerrain().isPositionValide(x, y) && 
                        getTerrain().getCase(x, y).getBatiment() == null && 
                        getTerrain().getUniteAt(x, y) == null) {
                        return new int[]{x, y};
                    }
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
        return running && !paused && estFini() && !Thread.currentThread().isInterrupted();
    }

    public boolean isPaused() {
        return paused;
    }

    public void togglePause() {
        paused = !paused;
    }
}
