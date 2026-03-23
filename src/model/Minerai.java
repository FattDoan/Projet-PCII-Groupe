package model;

import common.Validation;
import java.util.Objects;

/**
 * Représente un minerai mobile extrait par une foreuse.
 * Le minerai se déplace de bâtiment en bâtiment (routes puis stockage/HQ)
 * jusqu'à atteindre une destination finale ou un état invalide.
 */
public class Minerai implements Runnable {
    // Référence immutable vers le terrain pour lire l'état du monde.
    private final Terrain terrain;

    // Position courante du minerai dans la grille.
    private int x;
    private int y;

    // Drapeau d'arrêt coopératif du thread.
    private volatile boolean running = true;
    // Cadence de déplacement d'un minerai.
    public static final int DELAI_TRANSPORT_MS = 1000;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Minerai(int x, int y, Terrain terrain) {
        Validation.requireArgument(x >= 0 && y >= 0, "Position minerai invalide: x=" + x + ", y=" + y);
        this.x = x;
        this.y = y;
        this.terrain = Objects.requireNonNull(terrain, "terrain ne doit pas etre null");
    }

    @Override
    public void run() {
         // Boucle de vie du minerai: tant qu'il n'est pas arrivé à destination
        // (stockage/HQ) et tant que le thread n'est pas interrompu.
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(DELAI_TRANSPORT_MS);
                // Un seul pas logique à chaque tick.
                running = transporterUnPas();
            } catch (InterruptedException e) {
                // Respect du contrat d'interruption: on remet le flag puis on sort.
                Thread.currentThread().interrupt();
                break;
            }
        } 

/*         // Si le thread est déjà interrompu ou que le minerai est marqué pour arrêt, 
        // on ne fait rien
        if (!running || Thread.currentThread().isInterrupted()) {
            return;
        }

        // On fait un seul pas logique
        running = transporterUnPas();

        // Si le minerai n'est pas encore arrivé à destination finale
        // on se reprogramme pour continuer le transport au prochain tick.
        if (running) {
            common.AsyncExecutor.schedule(this, DELAI_TRANSPORT_MS);
        } */
    }

    private boolean transporterUnPas() {
        // 1) Lire la case courante et le bâtiment support.
        Case currentCase = terrain.getCase(x, y);
        Batiment currentBatiment = currentCase.getBatiment();
        if (currentBatiment == null) {
            // État invalide: un minerai ne doit pas exister hors bâtiment.
            return false;
        }

        // 2) Déterminer la direction:
        // - Sur route: direction imposée par la route.
        // - Sur foreuse: meilleure sortie disponible.
        Direction direction = (currentBatiment instanceof Route route)
            ? route.getDirection()
            : trouverMeilleurDirection();

        if (direction == null) {
            // Aucune direction praticable ce tick: on attend.
            return true;
        }

        // 3) Calcul de la coordonnée cible.
        int nextX = x;
        int nextY = y;
        switch (direction) {
            case NORD -> nextY--;
            case SUD -> nextY++;
            case EST -> nextX++;
            case OUEST -> nextX--;
        }

        // 4) Validation géométrique.
        if (!isInsideBounds(nextX, nextY)) {
            // Direction non praticable pour l'instant: on retentera au prochain tick.
            return true;
        }

        // 5) Validation métier: la case suivante doit contenir un bâtiment.
        Case nextCase = terrain.getCase(nextX, nextY);
        if (!nextCase.aBatiment()) {
            // Si la route n'est pas encore construite, on attend au lieu d'abandonner.
            return true;
        }

        // 6) Transaction logique en deux temps:
        // - réserver la place en cible,
        // - retirer de la source,
        // - rollback si la source ne peut plus retirer.
        Batiment cible = nextCase.getBatiment();
        if (!cible.essayerAjouterMinerai(1)) {
            // Cible pleine: on reste sur place.
            return true;
        }

        if (!currentBatiment.essayerRetirerMinerai(1)) {
            // Rollback pour conserver la cohérence des stockages.
            cible.essayerRetirerMinerai(1);
            return true;
        }

        // 7) Commit du déplacement.
        x = nextX;
        y = nextY;

        // 8) Fin de vie du minerai quand il atteint une destination finale.
        return !(cible instanceof Stockage || cible instanceof BatimentMaitre);
    }

    public void arreter() {
        // Arrêt explicite utilisable par un orchestrateur futur.
        running = false;
    }

    public boolean isInsideBounds(int x, int y) {
        return x >= 0 && x < terrain.getTaille() && y >= 0 && y < terrain.getTaille();
    }

    public Direction trouverMeilleurDirection() {
        // Direction de secours si on ne trouve pas d'alignement parfait.
        Direction dir = null;

        for (Direction d : Direction.values()) {
            int nx = this.x, ny = this.y;

            // Conversion direction -> case voisine.
            switch (d) {
                case NORD: ny--; break;
                case SUD:  ny++; break;
                case EST:  nx++; break;
                case OUEST: nx--; break;
            }

            // Ignore les voisins hors carte.
            if (!isInsideBounds(nx, ny)) {
                continue;
            }
            Case adj = terrain.getCase(nx, ny);
            if (adj.aBatiment() && adj.getBatiment() instanceof Route && !adj.getBatiment().estPlein()) {
                Route r = (Route) adj.getBatiment();
                
                // Priorité 1: route parfaitement alignée avec la direction de sortie.
                if (r.getDirection() == d) {
                    return d; 
                }
                // Priorité 2: route non alignée mais acceptable comme repli.
                dir = d;
            } else if (adj.aBatiment() && (adj.getBatiment() instanceof Stockage || adj.getBatiment() instanceof BatimentMaitre)
                        && !adj.getBatiment().estPlein()) {
                // Priorité maximale: destination finale atteignable immédiatement.
                return d;
            }
        }
        return dir;
    } 
}
