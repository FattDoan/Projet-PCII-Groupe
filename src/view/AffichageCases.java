package view;

import java.awt.Graphics;
import java.awt.Color;
import model.Case;

/** Classe qui contient les méthodes d'affichage des cases du terrain */
public class AffichageCases {

    /****** CONSTANTES *******/

    // Couleur d'une case vide, définie une fois pour toutes pour éviter de recréer un objet Color à chaque affichage
    private static final Color DARK_GREEN = new Color(34, 139, 34); // Couleur d'une case vide


    /****** FONCTIONS D'AFFICHAGE *******/

    /** Fonction de base à partir de laquelle on appelle toutes les autres fonctions d'affichage */
    public static void afficheCase(Graphics g, Case c) {
        // si une case contient un bâtiment et un minerai, on affiche le bâtiment (le minerai est caché dessous)
        if (c.aBatiment()) {
            AffichageBatiments.afficheBatiment(g, c);
        }
        else if (c.aMinerai()) {
            afficheMinerai(g, c);
        }
        else {
            afficheCaseVide(g, c);
        }
    }

    /** Affiche une case vide à la position (x, y) sur la fenêtre */
    private static void afficheCaseVide(Graphics g, Case c) {
        int x = c.getX() * Affichage.TAILLE_CASE; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * Affichage.TAILLE_CASE;

        // Choix visuel: vert foncé pour évoquer l'herbe.
        g.setColor(DARK_GREEN); // Couleur d'une case vide, ici vert pour faire de l'herbe
        g.fillRect(x, y, Affichage.TAILLE_CASE, Affichage.TAILLE_CASE); // Dessine un carré dans la case correspondante
    }

    /** Affiche un gisement de minerai (/!\ PAS UN MINERAI QUI SE DEPLACE SUR UNE ROUTE/!\) à la position (x, y) sur la fenêtre */
    private static void afficheMinerai(Graphics g, Case c) {
        int x = c.getX() * Affichage.TAILLE_CASE; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * Affichage.TAILLE_CASE;

        g.setColor(java.awt.Color.BLUE); // Couleur du minerai
        g.fillRect(x, y, Affichage.TAILLE_CASE, Affichage.TAILLE_CASE); // Dessine un carré dans la case correspondante
    }

}
