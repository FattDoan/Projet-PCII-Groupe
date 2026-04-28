package tests;
import model.*;

/**
 * Test simple pour le déplacement d'un minerai sur une route.
 */
public class MineraiTest {
    public static void main(String[] args) throws InterruptedException {
        // Création d'un terrain 7x7
        Terrain terrain = new Terrain(7);
        // Ajout d'une foreuse en (2,2)
        Foreuse foreuse = new Foreuse(2, 2, terrain);
        foreuse.terminerConstruction();
        terrain.getCase(2, 2).setBatiment(foreuse);
        // Ajout d'une seule route vers le sud pour atteindre (3,3)
        Route route1 = new Route(Direction.SUD, 3, 2, terrain);
        route1.terminerConstruction();
        terrain.getCase(3, 2).setBatiment(route1);
        // Récupération du bâtiment maître déjà placé sur le terrain (3, 3)
        BatimentMaitre batimentMaitre = (BatimentMaitre) terrain.getCase(3, 3).getBatiment();
        // Création d'un minerai sur la foreuse
        Minerai minerai = new Minerai(2, 2, terrain);
        // Ajout du minerai à la foreuse
        foreuse.ajouterMinerai(1);
        // Démarrage du thread de transport
        Thread mineraiThread = new Thread(minerai);
        mineraiThread.start();

        mineraiThread.join(5000);
        if (mineraiThread.isAlive()) {
            throw new AssertionError("Le thread minerai devrait etre termine");
        }

        if (foreuse.getStockage() != 0) {
            throw new AssertionError("Stockage foreuse attendu=0, obtenu=" + foreuse.getStockage());
        }

        if (route1.getStockage() != 0) {
            throw new AssertionError("Stockage route attendu=0, obtenu=" + route1.getStockage());
        }

        if (batimentMaitre.getStockage() != 1) {
            throw new AssertionError("Stockage batiment maitre attendu=1, obtenu=" + batimentMaitre.getStockage());
        }

        System.out.println("[OK] MineraiTest");
    }
}
