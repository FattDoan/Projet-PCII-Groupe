package model;

/** Un bâtiment de stockage de minerai */
public class Stockage extends Batiment implements Runnable {
    /** Capacité de stockage du bâtiment de stockage */
    private static final int CAPACITE = 20;
    public static final int COUT_CONSTRUCTION = 10;
    public static final int HP_MAX = 10; // Points de vie maximum du bâtiment de stockage

    private volatile boolean running = true; // Indique si le thread doit continuer à fonctionner
    private static final int DELAI_ENVOI_MINERAI_MS = 1000; // Délai entre l'envoi de minerais vers le bâtiment maître (simule le temps de transport)
    
    /**
    * Crée un nouveau bâtiment de stockage avec la capacité par défaut.
     * 
     * @param x la coordonnée x du bâtiment sur la grille
     * @param y la coordonnée y du bâtiment sur la grille
     * @param terrain le terrain auquel appartient ce bâtiment
     */
    public Stockage(int x, int y, Terrain terrain) {
        super(CAPACITE, x, y, terrain, HP_MAX, false); // La capacité est fixée à 10, le coût de construction est défini, et les HP max sont de 100.
    }

    @Override
    public TypeBatiment getType() {
        return TypeBatiment.STOCKAGE;
    }

    @Override
    public int getCost() {
        return COUT_CONSTRUCTION;
    }

    @Override
    public void detruire() {
        viderStockage(); // Vide le stockage avant de détruire le bâtiment (pour l'instant sert pas à grand chose mais peut éviter des bugs)
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
