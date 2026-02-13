package model;

/**
 * Thread responsable du transport automatique des minerais sur les routes.
 * Ce thread s'exécute en boucle et déplace les minerais à intervalles réguliers (toutes les secondes).
 * Le transport s'effectue de manière autonome en arrière-plan tant que le thread est actif.
 */
public class Transport extends Thread {
    /** Le système de transport gérant la logique métier */
    private final SystemeTransport systeme;
    
    /** Délai entre chaque déplacement de minerais (en millisecondes) */
    private static final int DELAI = 1000;
    
    /** Indique si le transport est actif. Volatile pour garantir la visibilité entre threads. */
    private volatile boolean actif;

    /**
     * Crée un nouveau thread de transport automatique.
     * Le thread doit être démarré avec start() pour commencer le transport.
     * Note : Le terrain ne peut pas être nul.
     * 
     * @param terrain le terrain sur lequel gérer les déplacements de minerais, ne doit pas être null
     * @throws AssertionError si terrain est null
     */
    public Transport(Terrain terrain) {
        super("Transport-Minerais");
        // Validation : le terrain ne peut pas être nul
        assert terrain != null : "terrain=null";
        
        this.systeme = new SystemeTransport(terrain);
        this.actif = true;
    }

    /**
     * Boucle principale du thread de transport.
     * Déplace automatiquement tous les minerais sur les routes à intervalles réguliers.
     * Continue jusqu'à ce que stopTransport() soit appelé ou que le thread soit interrompu.
     */
    @Override
    public void run() {
        while (actif) {
            // a faire systeme.deplacerMinerais();
            
            try {
                Thread.sleep(DELAI);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Arrête proprement le thread de transport.
     * Cette méthode interrompt le thread et empêche de nouveaux déplacements.
     */
    public void stopTransport() {
        actif = false;
        interrupt();
    }
}
