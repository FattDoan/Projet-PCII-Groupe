package view;

import java.awt.Graphics;
import model.unite.Unite;

/** Classe qui contient les méthodes d'affichage des unités */
public class AffichageUnites {
    public static final int TAILLE_UNITE = 20; // Taille d'affichage d'une unité en pixels, à ajuster selon vos besoins
    // afichage d'une unité à la position (x, y) sur la fenêtre
    // getGX(), getGY() pour récupérer les coordonnées de l'unité en terrain (int)
    // getPX(), getPY() pour récupérer les coordonnées de l'unité en pixels (float)
    public static void afficheUnite(Graphics g, Unite u) {
        switch (u.getType()) {
            case OUVRIER:
                afficheOuvrier(g, u);
                break;

            default:
                // Si le type d'unité n'est pas reconnu, on peut choisir de ne rien afficher ou d'afficher une unité générique
                break;
        }
    }

    public static void afficheOuvrier(Graphics g, Unite u) {
        int x = (int)u.getPX(); // conversion des coordonnées de l'unité en pixels pour l'affichage
        int y = (int)u.getPY();

        // Choix visuel: purple pour les ouvriers, à ajuster selon les types d'unités que vous aurez
        g.setColor(java.awt.Color.MAGENTA); // Couleur de l'unité, ici magenta pour faire du purple

        // Draw a circle centered on the unit's pixel coordinates with diameter equal to TAILLE_UNITE
        g.fillOval(x - TAILLE_UNITE/2, y - TAILLE_UNITE/2, TAILLE_UNITE, TAILLE_UNITE); // Dessine un cercle centré sur les coordonnées de l'unité
   }


}
