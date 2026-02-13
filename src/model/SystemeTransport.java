package model;

import java.util.HashSet;
import java.util.Set;

/**
 * Système de gestion du transport automatique des minerais.
 * Gère la logique de déplacement des minerais sur les routes en suivant leurs directions.
 * Optimise les performances en ne traitant que les routes actives (contenant des minerais).
 */
public class SystemeTransport {
    /** Le terrain sur lequel s'effectue le transport */
    private final Terrain terrain;
    
    /** Ensemble des coordonnées des routes contenant au moins un minerai (optimisation) */
    private final Set<PositionGrille> routesAvecMinerais;
    
    /**
     * Crée un nouveau système de transport pour le terrain spécifié.
     * Initialise l'ensemble des routes actives (vide au départ).
     * Note : Le terrain ne peut pas être nul.
     * 
     * @param terrain le terrain à gérer, ne doit pas être null
     * @throws AssertionError si terrain est null
     */
    public SystemeTransport(Terrain terrain) {
        // Validation : le terrain ne peut pas être nul
        assert terrain != null : "terrain=null";
        
        this.terrain = terrain;
        this.routesAvecMinerais = new HashSet<>();
    }
    
    /**
     * Marque une route comme active (contenant des minerais à transporter).
     * Note : Les coordonnées doivent être valides et dans les limites de la grille.
     * 
     * @param x coordonnée horizontale de la route, doit être >= 0 et < taille de la grille
     * @param y coordonnée verticale de la route, doit être >= 0 et < taille de la grille
     * @throws AssertionError si x ou y sont invalides ou hors limites
     */
    public void ajouterRouteActive(int x, int y) {
        // Validation : les coordonnées doivent être dans les limites de la grille
        assert x >= 0 && y >= 0 : "Coordonnées négatives: x=" + x + ", y=" + y;
        assert x < terrain.getTaille() && y < terrain.getTaille() 
            : "Coordonnées hors limites: x=" + x + ", y=" + y + ", taille=" + terrain.getTaille();
        routesAvecMinerais.add(new PositionGrille(x, y));
    }
    
    /**
     * Retire une route de la liste active (plus de minerais à transporter).
     * Note : Les coordonnées doivent être valides et dans les limites de la grille.
     * 
     * @param x coordonnée horizontale de la route, doit être >= 0 et < taille de la grille
     * @param y coordonnée verticale de la route, doit être >= 0 et < taille de la grille
     * @throws AssertionError si x ou y sont invalides ou hors limites
     */
    public void retirerRouteActive(int x, int y) {
        // Validation : les coordonnées doivent être dans les limites de la grille
        assert x >= 0 && y >= 0 : "Coordonnées négatives: x=" + x + ", y=" + y;
        assert x < terrain.getTaille() && y < terrain.getTaille() 
            : "Coordonnées hors limites: x=" + x + ", y=" + y + ", taille=" + terrain.getTaille();
        routesAvecMinerais.remove(new PositionGrille(x, y));
    }
}
