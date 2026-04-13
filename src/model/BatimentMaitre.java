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
    private static final int COUT_CONSTRUCTION = Integer.MAX_VALUE; // Coût de construction infini pour le bâtiment maître, car il est déjà présent au début du jeu.
    private static final int HP_MAX = 100; // Points de vie maximum du bâtiment maître

    /**
     * Crée un nouveau bâtiment maître avec la capacité par défaut de 100 minerais.
     * Ce constructeur est utilisé une seule fois lors de la génération initiale du terrain. 
     */
    protected BatimentMaitre(int x, int y, Terrain terrain) {
        // Le bâtiment maître est déjà construit au départ, donc on fixe fini=true et un coût de construction infini pour empêcher sa construction par le joueur.
        super(CAPACITE, x, y, terrain, HP_MAX, true); // Le terrain est fourni directement au constructeur.
    }

    @Override
    public TypeBatiment type() {
        return TypeBatiment.BATIMENT_MAITRE;
    }

    @Override
    public void detruire() {
        // impossible de détruire le bâtiment maître
        // on déclenche une erreur 
        throw new UnsupportedOperationException("Le bâtiment maître ne peut pas être détruit. Perte de la partie.");
    }

    @Override
    public int getCost() {
        return COUT_CONSTRUCTION;
    }

}
