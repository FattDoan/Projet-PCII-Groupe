package model.unite;

import model.*;

public class Ouvrier extends Unite {
    public static final int HP_INITIAL = 10; // Points de vie initiaux
    public static final int VITESSE = 1; // Vitesse de déplacement (pixels par seconde)
    public Ouvrier(int GX, int GY, Terrain terrain) {
        super(GX * Unite.CASE_SIZE, GY * Unite.CASE_SIZE, VITESSE, HP_INITIAL, TypeUnite.OUVRIER, terrain); 
    }
    
}


