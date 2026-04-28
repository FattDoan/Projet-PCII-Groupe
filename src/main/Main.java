package main;

import common.AsyncExecutor;
import controller.GameController;
import javax.swing.SwingUtilities;
import model.Terrain;
import view.Fenetre;
import view.TimerView;
import model.unite.*;

public class Main {
    // Taille utilisée quand aucun argument/propriété n'est fourni.
    private static final int DEFAULT_SIZE = 50;
    // Taille minimale pour garantir un terrain jouable.
    private static final int MIN_SIZE = 5;

    public static void main(String[] args) {
        // Filet de sécurité global: toute exception non capturée est loguée.
        // Cela évite de perdre une erreur silencieuse dans un thread secondaire.
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("[FATAL] Unhandled exception in thread " + thread.getName());
            throwable.printStackTrace(System.err);
        });

        // Résolution centralisée de la taille de terrain:
        // 1) argument CLI, 2) propriété JVM, 3) valeur par défaut.
        int terrainSize = resolveTerrainSize(args);
        Terrain terrain = new Terrain(terrainSize);
        // ajoute une unite en ce moment pour tester 
        terrain.addUnite(new Ouvrier(terrainSize/2 + 5, terrainSize/2 + 5, terrain));

        // ajoute un ennemi pour tester 
        // TODO: add automatic spawning of enemies in the future
        terrain.addUnite(new Ennemi(5, 5, terrain));
        

        // Le shutdown hook garantit l'arrêt des threads asynchrones même
        // si la fenêtre est fermée brutalement.
        installShutdownHook();

        // Construction de la carte de démo (foreuses + routes + stockages).
        DemoScenarioBuilder.apply(terrain);


        // Toute construction Swing doit se faire sur l'EDT pour éviter les
        // problèmes de concurrence UI.
        SwingUtilities.invokeLater(() -> {
            Fenetre fenetre = new Fenetre("Advanced Logistics Demo", terrain);
            new TimerView(fenetre);
            new GameController(terrain, fenetre.getAffichage());
        });
    }

    private static int resolveTerrainSize(String[] args) {
        // Priorité n°1: argument de ligne de commande.
        if (args != null && args.length > 0) {
            try {
                int argSize = Integer.parseInt(args[0]);
                if (argSize >= MIN_SIZE) {
                    return argSize;
                }
            } catch (NumberFormatException ignored) {
                // Repli vers la propriété JVM puis la valeur par défaut.
            }
        }

        // Priorité n°2: propriété JVM (ex: -Dpcii.size=80).
        String propertyValue = System.getProperty("pcii.size");
        if (propertyValue != null) {
            try {
                int propertySize = Integer.parseInt(propertyValue);
                if (propertySize >= MIN_SIZE) {
                    return propertySize;
                }
            } catch (NumberFormatException ignored) {
                // Repli vers la valeur par défaut.
            }
        }

        // Priorité n°3: valeur par défaut sûre.
        return DEFAULT_SIZE;
    }

    private static void installShutdownHook() {
        // Nomme explicitement le thread pour faciliter le diagnostic.
        Runtime.getRuntime().addShutdownHook(new Thread(AsyncExecutor::shutdown, "shutdown-async-executor"));
    }
}
