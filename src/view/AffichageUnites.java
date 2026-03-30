package view;

import java.awt.Graphics;
import model.unite.Unite;

/** Classe qui contient les méthodes d'affichage des unités */
public class AffichageUnites {
    // afichage d'une unité à la position (x, y) sur la fenêtre
    // une rectangle (de taille_case / 2 larguer, taille_case hauteur) de couleur purple pour les ouvriers
    // getGX(), getGY() pour récupérer les coordonnées de l'unité en terrain (int)
    // getPX(), getPY() pour récupérer les coordonnées de l'unité en pixels (float)
    public static void afficheUnite(Graphics g, Unite u) {
        switch (u.getType()) {
            case OUVRIER:
                afficheOuvrier(g, u);
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

    public static void afficheOuvrier(Graphics g, Unite u) {
        int x = (int)u.getPX(); // conversion des coordonnées de l'unité en pixels pour l'affichage
        int y = (int)u.getPY();

        // Choix visuel: purple pour les ouvriers, à ajuster selon les types d'unités que vous aurez
        g.setColor(java.awt.Color.MAGENTA); // Couleur de l'unité, ici magenta pour faire du purple

        // Dessine un rectangle représentant l'unité. La taille peut être ajustée selon vos préférences.
        int size = Affichage.TAILLE_CASE / 2; // Taille de l'unité, ici la moitié de la taille d'une case
        g.fillRect(x - size / 2, y - size / 2, size, size); // Dessine un carré centré sur les coordonnées de l'unité 
   }


}
