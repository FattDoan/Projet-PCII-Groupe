package model.unite;

import model.*;

public class Ouvrier extends Unite {
    public static final int HP_INITIAL = 10; // Points de vie initiaux
    public static final float VITESSE = 0.5f; // Vitesse de déplacement
    public Ouvrier(int GX, int GY, Terrain terrain) {
        super(GX * Unite.CASE_SIZE, GY * Unite.CASE_SIZE, VITESSE, HP_INITIAL, TypeUnite.OUVRIER, terrain); 
    }
    
}


