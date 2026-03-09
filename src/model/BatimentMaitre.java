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
public class BatimentMaitre extends Batiment {
    /** Capacité de stockage du bâtiment maître */
    private static final int CAPACITE = 100;

    /**
     * Crée un nouveau bâtiment maître avec la capacité par défaut de 100 minerais.
     * Ce constructeur est utilisé une seule fois lors de la génération initiale du terrain. 
     */
    protected BatimentMaitre(int x, int y, Terrain terrain) {
        super(CAPACITE, x, y, terrain); // Le bâtiment maître est initialement positionné au centre du terrain, mais le terrain doit être assigné après la création du bâtiment
    }

    @Override
    public TypeBatiment type() {
        return TypeBatiment.BATIMENT_MAITRE;
    }
}
