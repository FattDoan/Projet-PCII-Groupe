package model;

import java.util.Objects;

/**
 * Représente un minerai extrait par une foreuse.
/***** A FAIRE *****/
/** * Représente un minerai extrait par une foreuse.
 * Un minerai est un objet simple qui peut être transporté par une unité de transport vers une route ou un stockage.
 * Il n'a pas de propriétés spécifiques pour l'instant, mais peut être étendu à l'avenir pour inclure des types de minerais, des valeurs, etc.
 */
public class Minerai implements Runnable {
    // Référence vers le terrain pour accéder aux cases
    private final Terrain terrain;
    // Coordonnées du minerai sur la grille
    private int x;
    private int y;

    // Indique si le thread doit continuer à fonctionner
    private volatile boolean running = true;

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

    public Minerai(int x, int y, Terrain terrain) {
        this.x = x;
        this.y = y;
        this.terrain = Objects.requireNonNull(terrain, "terrain ne doit pas etre null");
    }

    @Override
    public void run() {
        // Méthode exécutée lors du démarrage du thread
        boolean surForeuse = true;
        while (running) {
            try {
                Thread.sleep(DELAI_TRANSPORT);
                Case currentCase = terrain.getCase(x, y);
                // Déplacement selon la direction de la route
                int nextX = x;
                int nextY = y;
                if (currentCase.aBatiment() && currentCase.getBatiment() instanceof Route) {
                    Route route = (Route) currentCase.getBatiment();
                    switch (route.getDirection()) {
                        case NORD:
                            nextY = y - 1;
                            break;
                        case SUD:
                            nextY = y + 1;
                            break;
                        case EST:
                            nextX = x + 1;
                            break;
                        case OUEST:
                            nextX = x - 1;
                            break;
                    }
                } else {    // minerai dans la foreuse
                    // Priorite en fonction de l'ordre de Direction.java
                    Direction dir = trouverMeilleurDirection();
                    if (dir == null) {
                        // Aucune direction valide trouvée (pour l'instant), 
                        // on attends
                        continue;
                    }
                    // On DOIT verifier si il exist au moins une routes adjacente qui n'est pas pleine et valide
                    // avant de retirer le minerai de la foreuse
                    if (surForeuse && currentCase.aBatiment() && currentCase.getBatiment() instanceof Foreuse) {
                        Foreuse foreuse = (Foreuse) currentCase.getBatiment();
                        foreuse.retirerMinerai(1);
                        surForeuse = false;
                    }
                    switch (dir) {
                        case NORD:
                            nextY = y - 1;
                            break;
                        case SUD:
                            nextY = y + 1;
                            break;
                        case EST:
                            nextX = x + 1;
                            break;
                        case OUEST:
                            nextX = x - 1;
                            break;
                    }
                }
                // Vérification des limites
                if (nextX < 0 || nextX >= terrain.getTaille() || nextY < 0 || nextY >= terrain.getTaille()) {
                    // Le minerai sort de la carte : le transport s'arrete.
                    break;
                }
                Case nextCase = terrain.getCase(nextX, nextY);
                if (nextCase.aBatiment()) {
                    if (nextCase.getBatiment() instanceof Route && !nextCase.getBatiment().estPlein()) {
                        Route route = (Route) nextCase.getBatiment();
                        route.ajouterMinerai(1);
                        // Retirer le minerai de la route précédente
                        if (currentCase.aBatiment() && currentCase.getBatiment() instanceof Route) {
                            Route routePrec = (Route) currentCase.getBatiment();
                            routePrec.retirerMinerai(1);
                        }
                        this.x = nextX;
                        this.y = nextY;
                    } else if (nextCase.getBatiment() instanceof BatimentMaitre ||
                                nextCase.getBatiment() instanceof Stockage) {
                        Batiment bat = nextCase.getBatiment();
                        if (!bat.estPlein()) {
                            bat.ajouterMinerai(1);
                            // Retirer le minerai de la route précédente
                            if (currentCase.aBatiment() && currentCase.getBatiment() instanceof Route) {
                                Route routePrec = (Route) currentCase.getBatiment();
                                routePrec.retirerMinerai(1);
                            }
                            this.x = nextX;
                            this.y = nextY;
                            // Le minerai disparaît (fin du thread)
                            Thread.currentThread().interrupt();
                        }
                        else {
                            // Si le bâtiment de destination est plein, on attends
                        }
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


    public Direction trouverMeilleurDirection() {
        Direction dir = null;

        for (Direction d : Direction.values()) {
            int nx = this.x, ny = this.y;
        
            // Calculate neighbor coordinates
            switch (d) {
                case NORD: ny--; break;
                case SUD:  ny++; break;
                case EST:  nx++; break;
                case OUEST: nx--; break;
            }

            // Boundary Check
            if (nx < 0 || nx >= terrain.getTaille() || ny < 0 || ny >= terrain.getTaille()) continue;

            Case adj = terrain.getCase(nx, ny);
            if (adj.aBatiment() && adj.getBatiment() instanceof Route && !adj.getBatiment().estPlein()) {
                Route r = (Route) adj.getBatiment();
                
                // PRIORITE 1: C'est une route parfaitement alignée, on y va directement
                if (r.getDirection() == d) {
                    return d; 
                }
                // PRIORITE 2: C'est une route mais pas alignée, on la garde en option au cas où on ne trouve pas mieux
                dir = d;
            } else if (adj.aBatiment() && (adj.getBatiment() instanceof Stockage || adj.getBatiment() instanceof BatimentMaitre)) {
                // PRIORITE PLUS HAUTE: C'est un bâtiment de stockage ou maître, on veut y aller directement
                return d;
            }
        }
        return dir;
    } 
}
