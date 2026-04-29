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

    // Position courante du minerai dans la grille (case actuelle).
    private int x;
    private int y;
    
    // Position en pixels pour un affichage fluide.
    // Quand le minerai se déplace d'une case à une autre, ces coordonnées
    // passent progressivement d'une case à l'autre.
    private float px;
    private float py;
    
    // Position de départ et d'arrivée pour l animation de déplacement
    private float startPX, startPY;
    private float targetPX, targetPY;
    
    // Indicateur de progression du déplacement (0.0 = début, 1.0 = fin)
    private float progression = 0.0f;
    
    // Drapeau d'arrêt coopératif du thread.
    private volatile boolean running = true;
    
    // Temps total pour le déplacement d'une case à l'autre (en ms)
    public static final int DELAI_TRANSPORT_MS = 1000;
    
    // Étape de progression par tick (basé sur DELAI_TRANSPORT_MS)
    private static final float STEP_PROGRESSION = 1.0f / 10; // 10 étapes pour un déplacement complet

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    /**
     * Obtient la position X en pixels pour l'affichage.
     */
    public float getPX() {
        return px;
    }
    
    /**
     * Obtient la position Y en pixels pour l'affichage.
     */
    public float getPY() {
        return py;
    }
    
    /**
     * Obtient la progression du déplacement actuel (0.0 à 1.0).
     */
    public float getProgression() {
        return progression;
    }

    /** Constructeur d'un minerai avec une position et un terrain donnés. */
    public Minerai(int x, int y, Terrain terrain) {
        Validation.requireArgument(x >= 0 && y >= 0, "Position minerai invalide: x=" + x + ", y=" + y);
        this.x = x;
        this.y = y;
        this.terrain = Objects.requireNonNull(terrain, "terrain ne doit pas etre null");
        
        // Initialiser les coordonnées pixels (centre de la case)
        this.px = x * Case.TAILLE + Case.TAILLE / 2.0f;
        this.py = y * Case.TAILLE + Case.TAILLE / 2.0f;
        
        // Initialiser les positions de départ et cible
        this.startPX = px;
        this.startPY = py;
        this.targetPX = px;
        this.targetPY = py;
    }

    @Override
    public void run() {
        // Boucle de vie du minerai: tant qu'il n'est pas arrivé à destination
        // (stockage/HQ) et tant que le thread n'est pas interrompu.
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(DELAI_TRANSPORT_MS / 10); // Plus fréquent pour une animation fluide
                
                // Si on est en train d'animer un déplacement, continuer l'animation
                if (progression > 0.0f && progression < 1.0f) {
                    avancerAnimation();
                } else {
                    // Sinon, essayer de faire un pas logique
                    running = transporterUnPas();
                    
                    // Si le minerai ne peut plus continuer et doit être détruit,
                    // le retirer de la liste des minerais en transit
                    if (!running) {
                        terrain.removeMinerai(this);
                        break;
                    }
                }
            } catch (InterruptedException e) {
                // Respect du contrat d'interruption: on remet le flag puis on sort.
                Thread.currentThread().interrupt();
                terrain.removeMinerai(this);
                break;
            }
        } 
        
        // Nettoyage final
        terrain.removeMinerai(this);
    }

    /** Transporte le minerai d'une case à la suivante. Renvoie true si le minerai peut continuer à exister, false si on doit le détruire. */
    private boolean transporterUnPas() {
        // 1) Lire la case courante et le bâtiment support.
        Case currentCase = terrain.getCase(x, y);
        Batiment currentBatiment = currentCase.getBatiment();
        if (currentBatiment == null) {
            // État invalide: un minerai ne doit pas exister hors bâtiment.
            // Ou on a supprimé le bâtiment sous un minerai: dans les deux cas, on considère que le minerai disparaît.
            return false;
        }
        if (!currentBatiment.estFini()) {
            // On attend la fin de construction du bâtiment courant avant tout transport.
            return true;
        }

        // 2) Déterminer la direction:
        // - Sur route: direction imposée par la route.
        // - Sur foreuse/batiment de stockage: meilleure sortie disponible.
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
        if (cible == null || !cible.estFini()) {
            // La cible doit exister et être terminée pour accepter un minerai.
            return true;
        }
        if (!cible.essayerAjouterMinerai(1)) {
            // Cible pleine: on reste sur place.
            return true;
        }

        if (!currentBatiment.essayerRetirerMinerai(1)) {
            // Rollback pour conserver la cohérence des stockages.
            cible.essayerRetirerMinerai(1);
            return true;
        }

        // 7) Mettre à jour les coordonnées de grille IMMEDIATEMENT
        // pour que la logique du prochain pas soit correcte
        x = nextX;
        y = nextY;
        
        // 8) Démarrer l'animation de déplacement visuelle
        // Sauvegarder la position en pixels ACTUELLE comme point de départ
        startPX = px;
        startPY = py;
        
        // Calculer la position cible (centre de la case actuelle = destination)
        targetPX = x * Case.TAILLE + Case.TAILLE / 2.0f;
        targetPY = y * Case.TAILLE + Case.TAILLE / 2.0f;
        
        // Réinitialiser la progression
        progression = 0.0f;
        
        // Avancer dans l'animation pour démarrer le mouvement
        avancerAnimation();
        
        // 9) Fin de vie du minerai quand il atteint une destination finale (stockage ou bâtiment maître).
        return !(cible instanceof BatimentMaitre || cible instanceof Stockage || (cible instanceof Usine));
    }
    
    /**
     * Avance l'animation de déplacement d'une étape.
     * Met à jour px et py par interpolation entre start et target.
     */
    private void avancerAnimation() {
        if (progression >= 1.0f) {
            // Animation terminée: px/py sont déjà à la position cible
            px = targetPX;
            py = targetPY;
            startPX = px;
            startPY = py;
            targetPX = px;
            targetPY = py;
            progression = 0.0f;
            return;
        }
        
        progression += STEP_PROGRESSION;
        if (progression > 1.0f) {
            progression = 1.0f;
        }
        
        // Interpolation linéaire entre start et target
        px = startPX + (targetPX - startPX) * progression;
        py = startPY + (targetPY - startPY) * progression;
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
            if (!adj.aBatiment() || !adj.getBatiment().estFini()) {
                continue;
            }
            if (adj.aBatiment() && adj.getBatiment() instanceof Route && !adj.getBatiment().estPlein()) {
                Route r = (Route) adj.getBatiment();
                
                // Priorité 1: route parfaitement alignée avec la direction de sortie.
                if (r.getDirection() == d) {
                    return d; 
                }
                // Priorité 2: route non alignée mais acceptable comme repli.
                //dir = d;
            } else if (adj.aBatiment() && (adj.getBatiment() instanceof Stockage || adj.getBatiment() instanceof BatimentMaitre)
                        && !adj.getBatiment().estPlein()) {
                // Priorité maximale: destination finale atteignable immédiatement.
                return d;
            }
        }
        return dir;
    } 
}
