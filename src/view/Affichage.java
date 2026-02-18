package view;

import javax.swing.*;
import java.awt.*;

import model.Case;
import model.Terrain;

/** JPanel qui affiche le jeu (la grille et le menu) */
public class Affichage extends JPanel {

    /** CONSTANTES */

    // les dimensions relatives à la grille de jeu
    // taille d'une case en pixels, définit la taille de la fenêtre
    public static final int TAILLE_CASE = 30;

    // les dimensions du menu latéral par rapport à la largeur de la grille
    public static final double LARGEUR_MENU = 1.0 / 3.0; // le menu occupe 1/3 de la largeur totale de la fenêtre


    /** Variables relatives à la taille de la grille (inconnue avant exécution) */

    // les dimensions de la grille en pixels
    public static int LARGEUR_GRILLE;
    public static int HAUTEUR_GRILLE;

    // les dimensions de la fenêtre
    public static int LARGEUR;
    public static int HAUTEUR;


    /** VARIABLES DU JEU */

    private Terrain terrain;

    public Affichage(Terrain terrain) {
        super();

        this.terrain = terrain;

        // calcul des dimensions de la grille et de la fenêtre en fonction de la taille du terrain
        LARGEUR_GRILLE = terrain.getTaille() * TAILLE_CASE;
        HAUTEUR_GRILLE = terrain.getTaille() * TAILLE_CASE;
        LARGEUR = (int) (LARGEUR_GRILLE * (1 + LARGEUR_MENU)); // largeur totale = largeur de la grille + largeur du menu
        HAUTEUR = HAUTEUR_GRILLE; // la hauteur de la fenêtre est égale à la hauteur de la grille

        // initialisation de la fenêtre
        this.setPreferredSize(new java.awt.Dimension(LARGEUR, HAUTEUR));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // on affiche chaque case de la grille en fonction de son type (vide, minerai, bâtiment, etc.)
        for (int i = 0; i < terrain.getTaille(); ++i) {
            for (int j = 0; j < terrain.getTaille(); ++j) {
                Case c = terrain.getCase(i, j);
                AffichageCases.afficheCase(g, c);
            }
        }

        // TODO : afficher le menu latéral à droite de la grille, avec les informations sur le jeu (nombre de minerais extraits, etc.)
    }

}
