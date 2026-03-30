package model.unite;

import model.unite.TypeUnite;
import model.*;

public class Ouvrier extends Unite {
    public static final int HP_INITIAL = 10; // Points de vie initiaux du travailleur
    public static final int VITESSE = 1; // Vitesse de déplacement du travailleur
    public Ouvrier(float x, float y, Terrain terrain) {
        super(x, y, VITESSE, HP_INITIAL, TypeUnite.OUVRIER, terrain); 
    }
    
}


