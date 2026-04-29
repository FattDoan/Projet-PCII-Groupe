package model;

/**
 * Représente une foreuse qui extrait automatiquement les minerais.
 * Une foreuse doit être placée sur une case contenant un filon de minerai.
 * Elle extrait le minerai et le stocke temporairement avant de l'envoyer vers une route ou un stockage.
 * La capacité de stockage temporaire est fixée à 1 minerai.
 */
public class Foreuse extends Batiment implements Runnable {
    public static final int DELAI_EXTRACTION_MS = 2000; // 2 seconde
    public static final int COUT_CONSTRUCTION = 10;
    public static final int HP_MAX = 10; // Points de vie maximum de la foreuse
    private volatile boolean running = true; // Indique si le thread doit continuer à fonctionner


    /**
     * Crée une nouvelle foreuse.
     * La capacité de stockage est fixée à 1 (un minerai extrait en attente).
     */
    public Foreuse(int x, int y, Terrain terrain) {
        super(1, x, y, terrain, HP_MAX, false); // La foreuse a une capacité de 1, un coût de construction et des HPmax définis.
    }

    @Override
    public TypeBatiment getType() {
        return TypeBatiment.FOREUSE;
    }


    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // Simule le temps d'extraction du minerai
                Thread.sleep(DELAI_EXTRACTION_MS);

                // Extraction du minerai
                if (!estPlein() && this.estFini()) {
                    this.ajouterMinerai(1);
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

    @Override
    public void detruire() {
        this.viderStockage(); // Vide le stockage avant de détruire la foreuse (pour l'instant sert pas à grand chose mais peut éviter des bugs)
        arreter(); // Arrête le thread de la foreuse
    }

    @Override
    public int getCost() {
        return COUT_CONSTRUCTION;
    }

    /** Renvoie vrai si la foreuse est en train d'extraire du minerai */
    public boolean isRunning() {
        return running && estFini() && !Thread.currentThread().isInterrupted() && !estPlein();
    }

}
