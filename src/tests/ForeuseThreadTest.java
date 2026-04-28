package tests;
import model.Foreuse;

public class ForeuseThreadTest {
    public static void main(String[] args) throws InterruptedException {
        // Test automatisable : vérifie qu'une foreuse extrait bien un minerai après un cycle.
        model.Terrain terrain = new model.Terrain(5);
        Foreuse foreuse = new Foreuse(0, 0, terrain);
        foreuse.terminerConstruction();
        Thread t = new Thread(foreuse);
        t.start();

        // La foreuse extrait toutes les DELAI_EXTRACTION_MS.
        Thread.sleep(Foreuse.DELAI_EXTRACTION_MS);

        int stockage = foreuse.getStockage();
        if (stockage != 1) {
            foreuse.arreter();
            t.interrupt();
            throw new AssertionError("Stockage attendu=1, obtenu=" + stockage);
        }

        foreuse.arreter();
        t.interrupt();
        t.join(2000);
        if (t.isAlive()) {
            throw new AssertionError("Le thread de la foreuse devrait etre arrete");
        }

        System.out.println("[OK] ForeuseThreadTest");
    }
}
