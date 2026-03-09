package tests;
import model.Foreuse;

public class ForeuseThreadTest {
    public static void main(String[] args) throws InterruptedException {
        // Création d'une foreuse et lancement dans un thread séparé pour simuler le fonctionnement asynchrone
        Foreuse foreuse = new Foreuse();
        Thread t = new Thread(foreuse);
        t.start();

        // Boucle principale : affichage du stockage et retrait de minerai toutes les 600 ms, 5 fois de suite
        for (int i = 0; i < 5; i++) {
            System.out.println("[TEMPS REEL] Stockage foreuse = " + getStockage(foreuse));
            Thread.sleep(600);
            // Si la foreuse contient des minerais, on retire un minerai et on affiche l'état avant/après
            if (!foreuse.estVide()) {
                System.out.println("[AVANT RETRAIT] Stockage foreuse = " + getStockage(foreuse));
                System.out.println("[ACTION] Retrait d'un minerai...");
                foreuse.retirerMinerai(1);
                System.out.println("[APRES RETRAIT] Stockage foreuse = " + getStockage(foreuse));
            } else {
                // Si la foreuse est vide, on affiche une information
                System.out.println("[INFO] Foreuse déjà vide, rien à retirer.");
            }
        }

        // Arrêt propre du thread de la foreuse à la fin du test
        foreuse.arreter();
        System.out.println("[FIN] Thread arrêté");
    }

    // Utilitaire de test : accès au champ privé 'stockage' de la foreuse via réflexion (non recommandé en production)
    private static int getStockage(Foreuse f) {
        try {
            java.lang.reflect.Field stockage = f.getClass().getSuperclass().getDeclaredField("stockage");
            stockage.setAccessible(true);
            return stockage.getInt(f);
        } catch (Exception e) {
            System.out.println("[ERREUR] Impossible de lire le stockage : " + e);
            return -1;
        }
    }
}
