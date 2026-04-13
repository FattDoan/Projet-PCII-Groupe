package model;

/** Un bâtiment de stockage de minerai */
public class Stockage extends Batiment {
    /** Capacité de stockage du bâtiment de stockage */
    private static final int CAPACITE = 10;
    public static final int COUT_CONSTRUCTION = 10;
    public static final int HP_MAX = 10; // Points de vie maximum du bâtiment de stockage

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
    public TypeBatiment type() {
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
}
