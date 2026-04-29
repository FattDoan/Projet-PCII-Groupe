package model.unite;

import model.*;
import model.unite.commande.CommandeDeplacementEnnemi;

/**
 * Classe représentant les unités ennemies dans le jeu. 
 * Les ennemis se déplacent automatiquement vers le bâtiment maître ennemi et attaquent les unités ou bâtiments alliés à proximité.
 */
public class Ennemi extends Unite {
    public static final int HP_INITIAL = 8; // Points de vie initiaux
    public static final float VITESSE = 0.15f; // Vitesse de déplacement
    public static final int DEGATS = 2; // Dégâts infligés par l'ennemi
    public static final float ATTACK_RANGE = Case.TAILLE; // Portée d'attaque de l'ennemi 
    public Ennemi(int GX, int GY, Terrain terrain) {
        super(GX * Case.TAILLE, GY * Case.TAILLE, VITESSE, HP_INITIAL, TypeUnite.ENNEMI, terrain);
        this.ajouterCommande(new CommandeDeplacementEnnemi(terrain)); // L'ennemi commence par se déplacer vers le bâtiment maître ennemi
    }
    
}


