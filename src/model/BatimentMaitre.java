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
     * Ce constructeur est utilisé une seule lors de la génération initiale du terrain. 
     * TODO : ajouter ça dans Terrain puis rendre le constructeur package-private
     */
    public BatimentMaitre() {
        super(CAPACITE);
    }

    @Override
    public TypeBatiment type() {
        return TypeBatiment.BATIMENT_MAITRE;
    }
}
