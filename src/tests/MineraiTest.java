package tests;
import model.*;

/**
 * Test simple pour le déplacement d'un minerai sur une route.
 */
public class MineraiTest {
    public static void main(String[] args) {
        // Création d'un terrain 7x7
        Terrain terrain = new Terrain(7);
        // Ajout d'une foreuse en (2,2)
        Foreuse foreuse = new Foreuse(2, 2, terrain);
        terrain.getCase(2, 2).setBatiment(foreuse);
        // Ajout d'une seule route vers le sud pour atteindre (3,3)
        Route route1 = new Route(Direction.SUD, 3, 2, terrain);
        terrain.getCase(3, 2).setBatiment(route1);
        // Récupération du bâtiment maître déjà placé sur le terrain (3, 3)
        BatimentMaitre batimentMaitre = (BatimentMaitre) terrain.getCase(3, 3).getBatiment();
        // Création d'un minerai sur la foreuse
        Minerai minerai = new Minerai(2, 2, terrain);
        // Ajout du minerai à la foreuse
        foreuse.ajouterMinerai(1);
        // Démarrage du thread
        minerai.start();
        // Attente pour observer le déplacement
        try {
            Thread.sleep(500); // Attendre 5 secondes pour laisser le temps au minerai de se déplacer
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Vérification
        System.out.println("Stockage foreuse (2,2): " + foreuse.getStockage());
        System.out.println("Stockage route1 (3,2): " + route1.getStockage());
        System.out.println("Stockage batiment maitre (3,3): " + batimentMaitre.getStockage());
        if (batimentMaitre.getStockage() == 0) {
            System.out.println("Position minerai: x=" + minerai.getX() + ", y=" + minerai.getY());
        } else {
            System.out.println("Le minerai a été récupéré par le bâtiment maître et a disparu.");
        }
        try {
            Thread.sleep(500); // Attendre 5 secondes pour laisser le temps au minerai de se déplacer
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Vérification
        System.out.println("Stockage foreuse (2,2): " + foreuse.getStockage());
        System.out.println("Stockage route1 (3,2): " + route1.getStockage());
        System.out.println("Stockage batiment maitre (3,3): " + batimentMaitre.getStockage());
        System.out.println("Position minerai: x=" + minerai.getX() + ", y=" + minerai.getY());
        try {
            Thread.sleep(500); // Attendre 5 secondes pour laisser le temps au minerai de se déplacer
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Vérification
        System.out.println("Stockage foreuse (2,2): " + foreuse.getStockage());
        System.out.println("Stockage route1 (3,2): " + route1.getStockage());
        System.out.println("Stockage batiment maitre (3,3): " + batimentMaitre.getStockage());
        System.out.println("Position minerai: x=" + minerai.getX() + ", y=" + minerai.getY());
        try {
            Thread.sleep(500); // Attendre 5 secondes pour laisser le temps au minerai de se déplacer
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Vérification
        System.out.println("Stockage foreuse (2,2): " + foreuse.getStockage());
        System.out.println("Stockage route1 (3,2): " + route1.getStockage());
        System.out.println("Stockage batiment maitre (3,3): " + batimentMaitre.getStockage());
        System.out.println("Position minerai: x=" + minerai.getX() + ", y=" + minerai.getY());
        try {
            Thread.sleep(500); // Attendre 5 secondes pour laisser le temps au minerai de se déplacer
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Vérification
        System.out.println("Stockage foreuse (2,2): " + foreuse.getStockage());
        System.out.println("Stockage route1 (3,2): " + route1.getStockage());
        System.out.println("Stockage batiment maitre (3,3): " + batimentMaitre.getStockage());
        System.out.println("Position minerai: x=" + minerai.getX() + ", y=" + minerai.getY());
        try {
            Thread.sleep(500); // Attendre 5 secondes pour laisser le temps au minerai de se déplacer
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Vérification
        System.out.println("Stockage foreuse (2,2): " + foreuse.getStockage());
        System.out.println("Stockage route1 (3,2): " + route1.getStockage());
        System.out.println("Stockage batiment maitre (3,3): " + batimentMaitre.getStockage());
        System.out.println("Position minerai: x=" + minerai.getX() + ", y=" + minerai.getY());
        try {
            Thread.sleep(500); // Attendre 5 secondes pour laisser le temps au minerai de se déplacer
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
