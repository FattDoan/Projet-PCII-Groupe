package model;

/** Un bâtiment qui produit des unités à partir de minerai */
public class Usine extends Batiment {

    public static final int COUT_CONSTRUCTION = 200;

    /**
    * Crée une nouvelle usine avec la capacité par défaut.
     * 
     * @param x la coordonnée x du bâtiment sur la grille
     * @param y la coordonnée y du bâtiment sur la grille
     * @param terrain le terrain auquel appartient ce bâtiment
     */
    public Usine(int x, int y, Terrain terrain) {
        super(0, x, y, terrain, 0, false);
        // TODO Implémenter la classe Usine
        throw new UnsupportedOperationException("Classe Usine non implémentée");
    }

    @Override
    public TypeBatiment getType() {
        return TypeBatiment.USINE;
    }

    @Override
    public int getCost() {
        return COUT_CONSTRUCTION;
    }

    @Override
    public void detruire() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'detruire' for Usine");
    }

}
