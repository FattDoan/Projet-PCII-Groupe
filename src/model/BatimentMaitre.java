package model;

/**
 * Représente le bâtiment maître (bâtiment principal du joueur).
 * 
 * Le bâtiment maître est le bâtiment de départ du joueur et sert de stockage principal.
 * C'est la destination finale des minerais acheminés par les routes.
 * Il possède une capacité de stockage limitée (100 minerais par défaut).
 * 
 * IMPORTANT : Si le bâtiment maître est détruit par les ennemis, le joueur perd la partie.
 * Les ennemis se déplacent en ligne droite vers le bâtiment maître depuis les bords de la carte.
 */
public class BatimentMaitre extends Batiment implements Runnable {
    /** Capacité de stockage du bâtiment maître */
    private static final int CAPACITE = 100;
    public static final int COUT_CONSTRUCTION = 50;
    private static final int HP_MAX = 100; // Points de vie maximum du bâtiment maître
    private volatile boolean running = true; // Indique si le thread doit continuer à fonctionner
    private static final int DELAI_ENVOI_MINERAI_MS = 1000; // Délai entre l'envoi de minerais vers le bâtiment maître (simule le temps de transport)
    /**
     * Crée un nouveau bâtiment maître avec la capacité par défaut de 100 minerais.
     * Ce constructeur est utilisé une seule fois lors de la génération initiale du terrain. 
     */
    protected BatimentMaitre(int x, int y, Terrain terrain) {
        // Le bâtiment maître est déjà construit au départ, donc on fixe fini=true et un coût de construction infini pour empêcher sa construction par le joueur.
        super(CAPACITE, x, y, terrain, HP_MAX, true); // Le terrain est fourni directement au constructeur.
    }

    @Override
    public TypeBatiment getType() {
        return TypeBatiment.BATIMENT_MAITRE;
    }

    @Override
    public void detruire() {
        // Le bâtiment maître peut être détruit par les ennemis
        // Cela déclenche la fin de la partie
        receiveDamage(getHP()); // Met les HP à 0
    }
    
    /**
     * Vérifie si le bâtiment maître est détruit (Game Over).
     * @return true si le bâtiment maître est détruit
     */
    public boolean estGameOver() {
        return estDetruit();
    }

    @Override
    public int getCost() {
        return COUT_CONSTRUCTION;
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // Simule le temps d'extraction du minerai
                Thread.sleep(DELAI_ENVOI_MINERAI_MS);

                // Extraction du minerai
                // Parce que on est pas foreuse, on suppose que le bâtiment maître reçoit les minerais acheminés par les routes
                // If theres exist an out bound route from this stockage, 
                // and if the stockage is not empty, 
                // and if the stockage is finished, then we can send a minerai to the building master
                if (!estVide() && this.estFini() && getTerrain().existeRouteSortante(getX(), getY())) {
                    this.retirerMinerai(1); // Retire un minerai du stockage (simule la réception d'un minerai acheminé par une route)
                    Minerai nouveauMinerai = new Minerai(getX(), getY(), getTerrain());

                    // Ajouter le minerai à la liste des minerais en transit pour affichage
                    // et démarrer son thread de transport
                    getTerrain().addMinerai(nouveauMinerai);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Méthode pour arrêter proprement le thread
     */
    public void arreter() {
        running = false;
    }
}
