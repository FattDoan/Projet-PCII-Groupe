package view;

import java.awt.Graphics;
import model.unite.Unite;

/** Classe qui contient les méthodes d'affichage des unités */
public class AffichageUnites {
    public static final int TAILLE_UNITE = 20; // Taille d'affichage d'une unité en pixels, à ajuster selon vos besoins
    // Affichage d'une unite a la position (x, y) sur la fenetre
    // getGX(), getGY() : coordonnees terrain (int)
    // getPX(), getPY() : coordonnees pixels (float)
    public static void afficheUnite(Graphics g, Unite u) {
        switch (u.getType()) {
            case OUVRIER:
                afficheOuvrier(g, u);
                break;
            
            case ENNEMI:
                afficheEnnemi(g, u);
                break;

            default:
                // Si le type d'unité n'est pas reconnu, on peut choisir de ne rien afficher ou d'afficher une unité générique
                break;
        }
    }

    public static void afficheEnnemi(Graphics g, Unite u) {
        int x = (int)u.getPX(); // conversion des coordonnées de l'unité en pixels pour l'affichage
        int y = (int)u.getPY();

        // Choix visuel: red pour les ennemis, à ajuster selon les types d'unités que vous aurez
        g.setColor(java.awt.Color.RED); // Couleur de l'unité, ici rouge pour les ennemis

        // Draw a triangle centered on the unit's pixel coordinates with size equal to TAILLE_UNITE
        int halfSize = TAILLE_UNITE / 2;
        int[] xPoints = {x, x - halfSize, x + halfSize};
        int[] yPoints = {y - halfSize, y + halfSize, y + halfSize};
        g.fillPolygon(xPoints, yPoints, 3); // Dessine un
                                            // triangle centré sur les coordonnées de l'unité
                                            // le triangle pointe vers le haut, à ajuster selon vos préférences
    }

    public static void afficheOuvrier(Graphics g, Unite u) {
        int x = (int)u.getPX(); // conversion des coordonnées de l'unité en pixels pour l'affichage
        int y = (int)u.getPY();

        // Les ouvriers sont en magenta losqu'ils ont tous leurs points de vie, et deviennent plus bleus à mesure qu'ils perdent des PV, à ajuster selon le design
        int hp = u.getHP();
        int maxHp = u.getHPMax();
        int red = 100 + (int)(155 * ((double)hp / (double)maxHp)); // Plus les PV sont bas, plus le rouge diminue, rendant la couleur plus bleue
        int green = 0;
        int blue = 255;
        g.setColor(new java.awt.Color(red, green, blue)); // Couleur de l'unité, ajustée en fonction de ses PV

        // Dessine un cercle centre sur la position de l'unite
        g.fillOval(x - TAILLE_UNITE/2, y - TAILLE_UNITE/2, TAILLE_UNITE, TAILLE_UNITE);
   }


}
