package view;

import java.awt.Graphics;
import model.Case;

/** Classe qui contient les méthodes d'affichage des cases du terrain */
public class AffichageCases {

    /** Fonction de base à partir de laquelle on appelle toutes les autres fonctions d'affichage */
    public static void afficheCase(Graphics g, Case c) {
        if (c.estVide()) {
            afficheCaseVide(g, c);
        }
        else if (c.getType() == model.TypeCase.MINERAI) {
            afficheMinerai(g, c);
        }
        else {
            AffichageBatiments.afficheBatiment(g, c);
        }
    }

    /** Affiche une case vide à la position (x, y) sur la fenêtre */
    private static void afficheCaseVide(Graphics g, Case c) {
        int x = c.getX() * Affichage.TAILLE_CASE; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * Affichage.TAILLE_CASE;

        g.setColor(java.awt.Color.GREEN); // Couleur d'une case vide, ici vert pour faire de l'herbe
        g.fillRect(x, y, Affichage.TAILLE_CASE, Affichage.TAILLE_CASE); // Dessine un carré dans la case correspondante
    }

    /** Affiche un minerai à la position (x, y) sur la fenêtre */
    private static void afficheMinerai(Graphics g, Case c) {
        int x = c.getX() * Affichage.TAILLE_CASE; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * Affichage.TAILLE_CASE;

        g.setColor(java.awt.Color.BLUE); // Couleur du minerai
        g.fillRect(x, y, Affichage.TAILLE_CASE, Affichage.TAILLE_CASE); // Dessine un carré dans la case correspondante
    }

}
