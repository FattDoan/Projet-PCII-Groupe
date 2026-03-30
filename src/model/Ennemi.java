package model;

public class Ennemi extends Unite {
    private static final int HP_INITIAL = 6; // Points de vie initiaux de l'ennemi

    public Ennemi(int x, int y, Terrain terrain) {
        super(x, y, HP_INITIAL, TypeUnite.ENNEMI, terrain);
    }


}
