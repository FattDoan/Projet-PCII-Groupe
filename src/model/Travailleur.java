package model;

public class Travailleur extends Unite {
    private static final int HP_INITIAL = 10; // Points de vie initiaux du travailleur

    public Travailleur(int x, int y, Terrain terrain) {
        super(x, y, HP_INITIAL, TypeUnite.TRAVAILLEUR, terrain);
    }

    
}


