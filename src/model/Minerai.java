package model;

/***** A FAIRE *****/
/** * Représente un minerai extrait par une foreuse.
 * Un minerai est un objet simple qui peut être transporté par une unité de transport vers une route ou un stockage.
 * Il n'a pas de propriétés spécifiques pour l'instant, mais peut être étendu à l'avenir pour inclure des types de minerais, des valeurs, etc.
 */
public class Minerai extends Thread {
        // Référence vers le terrain pour accéder aux cases
        private Terrain terrain;
    // Coordonnées du minerai sur la grille
    private int x;
    private int y;

    // Indique si le thread doit continuer à fonctionner
    private boolean running = true;

    // Délai de transport du minerai en millisecondes (modifiable selon les besoins)
    private int DELAI_TRANSPORT = 1000;

        /**
         * Retourne la coordonnée X du minerai.
         */
        public int getX() {
            return x;
        }

        /**
         * Retourne la coordonnée Y du minerai.
         */
        public int getY() {
            return y;
        }

    public Minerai(int x, int y) {
        // Constructeur : initialise les coordonnées du minerai
        this.x = x;
        this.y = y;
        // terrain à fournir lors de la création du minerai
        // (à adapter selon votre architecture, ici on suppose un setter ou constructeur étendu)
    }

    public Minerai(int x, int y, Terrain terrain) {
        this.x = x;
        this.y = y;
        this.terrain = terrain;
    }

    @Override
    public void run() {
        // Méthode exécutée lors du démarrage du thread
        boolean surForeuse = true;
        while (running) {
            try {
                Thread.sleep(DELAI_TRANSPORT);
                Case currentCase = terrain.getCase(x, y);
                if (surForeuse && currentCase.aBatiment() && currentCase.getBatiment() instanceof Foreuse) {
                    Foreuse foreuse = (Foreuse) currentCase.getBatiment();
                    if (!foreuse.estVide()) {
                        foreuse.retirerMinerai(1);
                    }
                    surForeuse = false;
                }
                // Chercher la case suivante à l'est
                int nextX = x + 1;
                int nextY = y;
                if (nextX >= terrain.getTaille()) break;
                Case nextCase = terrain.getCase(nextX, nextY);
                if (nextCase.aBatiment()) {
                    if (nextCase.getBatiment() instanceof Route && !nextCase.getBatiment().estPlein()) {
                        Route route = (Route) nextCase.getBatiment();
                        route.ajouterMinerai(1);
                        // Retirer le minerai de la route précédente
                        if (currentCase.aBatiment() && currentCase.getBatiment() instanceof Route) {
                            Route routePrec = (Route) currentCase.getBatiment();
                            if (!routePrec.estVide()) routePrec.retirerMinerai(1);
                        }
                        this.x = nextX;
                        this.y = nextY;
                    } else if (nextCase.getBatiment() instanceof BatimentMaitre) {
                        BatimentMaitre maitre = (BatimentMaitre) nextCase.getBatiment();
                        maitre.ajouterMinerai(1);
                        // Retirer le minerai de la route précédente
                        if (currentCase.aBatiment() && currentCase.getBatiment() instanceof Route) {
                            Route routePrec = (Route) currentCase.getBatiment();
                            if (!routePrec.estVide()) routePrec.retirerMinerai(1);
                        }
                        this.x = nextX;
                        this.y = nextY;
                        // Le minerai disparaît (fin du thread)
                        Thread.currentThread().interrupt();
                    } else {
                        // Si ce n'est ni une route ni un bâtiment maître, on arrête
                        break;
                    }
                } else {
                    // Si la case suivante n'a pas de bâtiment, on arrête
                    break;
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
        // Permet d'arrêter proprement le thread
        running = false;
    }
}
