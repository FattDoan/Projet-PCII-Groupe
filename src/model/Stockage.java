package model;

/** Un bâtiment de stockage de minerai */
public class Stockage extends Batiment {
    /** Capacité de stockage du bâtiment de stockage */
    // TODO: ajuster cette capacité selon l'équilibrage final du gameplay.
    private static final int CAPACITE = 10;

    /**
    * Crée un nouveau bâtiment de stockage avec la capacité par défaut.
     * 
     * @param x la coordonnée x du bâtiment sur la grille
     * @param y la coordonnée y du bâtiment sur la grille
     * @param terrain le terrain auquel appartient ce bâtiment
     */
    public Stockage(int x, int y, Terrain terrain) {
        super(CAPACITE, x, y, terrain);
    }

    @Override
    public TypeBatiment type() {
        return TypeBatiment.STOCKAGE;
    }
}
