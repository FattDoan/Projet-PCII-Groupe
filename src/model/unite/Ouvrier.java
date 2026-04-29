package model.unite;

import model.*;

/**
 * Classe représentant un ouvrier dans le jeu.
 * Les ouvriers sont des unités de base qui peuvent construire, miner et défendre.
 */
public class Ouvrier extends Unite {
    public static final int HP_INITIAL = 10; // Points de vie initiaux
    public static final float VITESSE = 0.2f; // Vitesse de déplacement
    public static final float DEFENSE_RANGE = Case.TAILLE; // Portée de défense contre les ennemis
    public static final int DEGATS = 2; // Dégâts infligés par l'ouvrier en mode défense
                                        
    public Ouvrier(int GX, int GY, Terrain terrain) {
        super(GX * Case.TAILLE, GY * Case.TAILLE, VITESSE, HP_INITIAL, TypeUnite.OUVRIER, terrain); 
    }    
}


