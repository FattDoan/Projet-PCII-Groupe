package view;

import java.awt.Graphics;
import model.unite.Unite;

/** Classe qui contient les méthodes d'affichage des unités */
public class AffichageUnites {
    // afichage d'une unité à la position (x, y) sur la fenêtre
    // une rectangle (de taille_case / 2 larguer, taille_case hauteur) de couleur purple pour les ouvriers
    // getGX(), getGY() pour récupérer les coordonnées de l'unité en terrain (int)
    // getPX(), getPY() pour récupérer les coordonnées de l'unité en pixels (float)
    public static void afficheUnite(Graphics g, Unite u, int base) {
        switch (u.getType()) {
            case OUVRIER:
                afficheOuvrier(g, u, base);
                break;
            // case SOLDAT:
            //     afficheSoldat(g, u);
            //     break;
            // case VEHICULE:
            //     afficheVehicule(g, u);
            //     break;
            default:
                // Si le type d'unité n'est pas reconnu, on peut choisir de ne rien afficher ou d'afficher une unité générique
                break;
        }
    }

    public static void afficheOuvrier(Graphics g, Unite u, int base) {
        int x = (int)u.getPX(); // conversion des coordonnées de l'unité en pixels pour l'affichage
        int y = (int)u.getPY();

        // Choix visuel: purple pour les ouvriers, à ajuster selon les types d'unités que vous aurez
        g.setColor(java.awt.Color.MAGENTA); // Couleur de l'unité, ici magenta pour faire du purple

        // Draw a circle centered on the unit's pixel coordinates with diameter equal to TAILLE_UNITE
        g.fillOval(x - base/2, y - base/2, base, base); // Dessine un cercle centré sur les coordonnées de l'unité
   }


}
