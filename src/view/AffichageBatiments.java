package view;

import java.awt.Graphics;
import model.Case;
import model.Route;

/** Classe qui contient les méthodes d'affichage des bâtiments */
public class AffichageBatiments {

    /** Fonction d'affichage général pour une case contenant un bâtiment */
    public static void afficheBatiment(Graphics g, Case c) {
        switch (c.getBatiment().getType()) {
            case USINE:
                afficheUsine(g, c);
                break;
            case FOREUSE:
                afficheForeuse(g, c);
                break;
            case STOCKAGE:
                afficheStockage(g, c);
                break;
            case BATIMENT_MAITRE:
                afficheBatimentMaitre(g, c);
                break;
            case ROUTE:
                afficheRoute(g, c);
                break;
        }
        // Si le bâtiment a prit des dégâts, on affiche une indication visuelle
        if (!c.getBatiment().atFullHP()) {
            afficheDegats(g, c);
        }
        // Si le bâtiment est en construction, on affiche une indication visuelle
        if (!c.getBatiment().estFini()) {
            afficheEnTravaux(g, c);
        }
    }



    /****** MODIFICATEURS ******/


    /** Affichage par dessus un bâtiment pour indiquer qu'il n'est pas encore fini */
    private static void afficheEnTravaux(Graphics g, Case c) {
        // On affiches des lignes parallèles en diagonales pour indiquer que le bâtiment n'est pas encore fini
        int x = c.getX() * Affichage.TAILLE_CASE; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * Affichage.TAILLE_CASE;
        g.setColor(java.awt.Color.BLACK); // Couleur des lignes (exemple : noir)
        for (int i = 0; i < Affichage.TAILLE_CASE; i += 10) {
            g.drawLine(x + i, y, x, y + i); // Ligne diagonale de haut en bas
            g.drawLine(x + Affichage.TAILLE_CASE, y + i, x + i, y + Affichage.TAILLE_CASE); // Ligne diagonale de bas en haut
        }
    }

    /** Affichage par dessus un bâtiment pour indiquer qu'il a prit des dégâts */
    private static void afficheDegats(Graphics g, Case c) {
        // On affiche un carré rouge semi-transparent par dessus le bâtiment pour indiquer qu'il a prit des dégâts
        int x = c.getX() * Affichage.TAILLE_CASE; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * Affichage.TAILLE_CASE;
        double hpRatio = ((double) c.getBatiment().getHP()) / ((double) c.getBatiment().getHPMax()); // Calcul du ratio de points de vie restants
        int alpha = (int) (30 + (1 - hpRatio) * 200); // Plus le bâtiment a de dégâts, plus le carré rouge est opaque
        g.setColor(new java.awt.Color(255, 0, 0, alpha)); // Couleur rouge semi-transparente
        g.fillRect(x, y, Affichage.TAILLE_CASE, Affichage.TAILLE_CASE); // Dessine un carré par dessus le bâtiment
    }



    /****** BASE BATIMENTS ******/


    /** Beaucoup de code est copié-collé, mais c'est un affichage temporaire et ça permet de modifier le visuel de chaque batiment individuellement, donc on se permet ici de faire comme ça */

    /** Affichage d'un bâtiment de type "usine" dans la case (x, y) sur la fenêtre */
    private static void afficheUsine(Graphics g, Case c) {
        int x = c.getX() * Affichage.TAILLE_CASE; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * Affichage.TAILLE_CASE;

        g.setColor(java.awt.Color.GRAY); // Couleur de l'usine
        g.fillRect(x, y, Affichage.TAILLE_CASE, Affichage.TAILLE_CASE); // Dessine un carré dans la case correspondante

        // affiche le nombre de minerais contenus dans le bâtiment maître
        g.setColor(java.awt.Color.BLACK); // Couleur du texte
        g.drawString(Integer.toString(c.getBatiment().getStockage()), x + Affichage.TAILLE_CASE / 4, y + Affichage.TAILLE_CASE / 2); // Dessine le nombre de minéraux extraits au centre de la case
    }

    /** Affichage d'un bâtiment de type "mine" dans la case (x, y) sur la fenêtre */
    private static void afficheForeuse(Graphics g, Case c) {
        int x = c.getX() * Affichage.TAILLE_CASE; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * Affichage.TAILLE_CASE;

        g.setColor(java.awt.Color.YELLOW); // Couleur de la foreuse
        g.fillRect(x, y, Affichage.TAILLE_CASE, Affichage.TAILLE_CASE); // Dessine un carré dans la case correspondante

        // affiche le nombre de minerais contenus dans le bâtiment maître
        g.setColor(java.awt.Color.BLACK); // Couleur du texte (ici noir pour que ce soit lisible sur le jaune)
        g.drawString(Integer.toString(c.getBatiment().getStockage()), x + Affichage.TAILLE_CASE / 4, y + Affichage.TAILLE_CASE / 2); // Dessine le nombre de minéraux extraits au centre de la case
    }

    /** Affichage d'un bâtiment de type "stockage" dans la case (x, y) sur la fenêtre */
    private static void afficheStockage(Graphics g, Case c) {
        int x = c.getX() * Affichage.TAILLE_CASE; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * Affichage.TAILLE_CASE;

        g.setColor(java.awt.Color.ORANGE); // Couleur du stockage
        g.fillRect(x, y, Affichage.TAILLE_CASE, Affichage.TAILLE_CASE); // Dessine un carré dans la case correspondante

        // affiche le nombre de minerais contenus dans le bâtiment maître
        g.setColor(java.awt.Color.WHITE); // Couleur du texte (exemple : blanc)
        g.drawString(Integer.toString(c.getBatiment().getStockage()), x + Affichage.TAILLE_CASE / 4, y + Affichage.TAILLE_CASE / 2); // Dessine le nombre de minéraux extraits au centre de la case
    }

    /** Affichage d'un bâtiment de type "batiment_maitre" dans la case (x, y) sur la fenêtre */
    private static void afficheBatimentMaitre(Graphics g, Case c) {
        int x = c.getX() * Affichage.TAILLE_CASE; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * Affichage.TAILLE_CASE;
        g.setColor(java.awt.Color.RED); // Couleur du batiment_maitre
        g.fillRect(x, y, Affichage.TAILLE_CASE, Affichage.TAILLE_CASE); // Dessine un carré dans la case correspondante

        // affiche le nombre de minerais contenus dans le bâtiment maître
        g.setColor(java.awt.Color.WHITE); // Couleur du texte (exemple : blanc)
        g.drawString(Integer.toString(c.getBatiment().getStockage()), x + Affichage.TAILLE_CASE / 4, y + Affichage.TAILLE_CASE / 2); // Dessine le nombre de minéraux extraits au centre de la case
    }

    /** Affichage d'une route dans la case (x, y) sur la fenêtre */
    private static void afficheRoute(Graphics g, Case c) {
        int x = c.getX() * Affichage.TAILLE_CASE; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * Affichage.TAILLE_CASE;
        g.setColor(java.awt.Color.LIGHT_GRAY); // Couleur de la route (exemple : gris clair)
        g.fillRect(x, y, Affichage.TAILLE_CASE, Affichage.TAILLE_CASE); // Dessine un carré dans la case correspondante

        // affiche une flèche pour indiquer la direction de la route (exemple : une flèche vers le bas)
        g.setColor(java.awt.Color.BLACK); // Couleur de la flèche (exemple : noir)

        if (!(c.getBatiment() instanceof Route)) { // en théorie impossible mais on vérifie quand même
            throw new IllegalArgumentException("La case ne contient pas une route, impossible de l'afficher comme une route");
        }
        Route route = (Route) c.getBatiment(); // On suppose que la case contient une route, il faudrait vérifier avant de caster pour éviter les erreurs

        // on détermine la direction de la case pour afficher la flèche dans la bonne direction
        int[] xPoints;
        int[] yPoints;
        switch (route.getDirection()) {
            case NORD:
                xPoints = new int[]{x + Affichage.TAILLE_CASE / 2, x + Affichage.TAILLE_CASE / 4, x + 3 * Affichage.TAILLE_CASE / 4};
                yPoints = new int[]{y + Affichage.TAILLE_CASE / 4, y + 3 * Affichage.TAILLE_CASE / 4, y + 3 * Affichage.TAILLE_CASE / 4};
                break;
            case SUD:
                xPoints = new int[]{x + Affichage.TAILLE_CASE / 2, x + Affichage.TAILLE_CASE / 4, x + 3 * Affichage.TAILLE_CASE / 4};
                yPoints = new int[]{y + 3 * Affichage.TAILLE_CASE / 4, y + Affichage.TAILLE_CASE / 4, y + Affichage.TAILLE_CASE / 4};
                break;
            case EST:
                xPoints = new int[]{x + 3 * Affichage.TAILLE_CASE / 4, x + Affichage.TAILLE_CASE / 4, x + Affichage.TAILLE_CASE / 4};
                yPoints = new int[]{y + Affichage.TAILLE_CASE / 2, y + Affichage.TAILLE_CASE / 4, y + 3 * Affichage.TAILLE_CASE / 4};
                break;
            case OUEST:
                xPoints = new int[]{x + Affichage.TAILLE_CASE / 4, x + 3 * Affichage.TAILLE_CASE / 4, x + 3 * Affichage.TAILLE_CASE / 4};
                yPoints = new int[]{y + Affichage.TAILLE_CASE / 2, y + Affichage.TAILLE_CASE / 4, y + 3 * Affichage.TAILLE_CASE / 4};
                break;
            default:
                throw new IllegalArgumentException("Direction de route inconnue : " + route.getDirection());
        }
        g.fillPolygon(xPoints, yPoints, 3); // Dessine une flèche dans la case correspondante

        // Si un minerai est en transit sur la route, on l'affiche (exemple : un carré bleu pour représenter le minerai)
        if (c.getBatiment().getType() == model.TypeBatiment.ROUTE && c.getBatiment().getStockage() > 0) { 
            AffichageCases.afficheMineralIngot(g, c);
        }
    }

    /** Affiche un minerai qui se déplace sur la case c */
    public static void afficheMinerai(Graphics g, Case c) {
        int N = Affichage.TAILLE_CASE / 2;
        int x = c.getX() * Affichage.TAILLE_CASE; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * Affichage.TAILLE_CASE;

        g.setColor(java.awt.Color.BLUE); // Couleur du minerai
        g.fillRect(x + N, y + N, N, N); // Dessine un carré dans la case correspondante
    }

}
