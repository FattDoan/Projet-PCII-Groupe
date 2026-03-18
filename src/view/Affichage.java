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
    public static final int TAILLE_CASE = 40;

    // les dimensions du menu latéral par rapport à la largeur de la grille
    public static final double RATIO_LARGEUR_MENU = 1.0 / 2.0; // le menu occupe 1/3 de la largeur totale de la fenêtre


    /** Variables relatives à la taille de la grille (inconnue avant exécution) */

    // les dimensions de la grille en pixels
    public static int LARGEUR_GRILLE;
    public static int HAUTEUR_GRILLE;

    // les dimensions de la fenêtre
    public static int LARGEUR;
    public static int HAUTEUR;


    /** VARIABLES DU JEU */

    private Terrain terrain;

    // les composants graphiques de la fenêtre
    private AffichageTerrain affichageTerrain;
    private MenuPanel menuPanel;

    public Affichage(Terrain terrain) {
        super();

        this.terrain = terrain;

        // calcul des dimensions de la grille et de la fenêtre en fonction de la taille du terrain
        LARGEUR_GRILLE = terrain.getTaille() * TAILLE_CASE;
        HAUTEUR_GRILLE = terrain.getTaille() * TAILLE_CASE;
        LARGEUR = (int) (LARGEUR_GRILLE * (1 + RATIO_LARGEUR_MENU)); // largeur totale = largeur de la grille + largeur du menu
        HAUTEUR = HAUTEUR_GRILLE; // la hauteur de la fenêtre est égale à la hauteur de la grille

        // initialisation de la fenêtre
        this.setPreferredSize(new java.awt.Dimension(LARGEUR, HAUTEUR));

        affichageTerrain = new AffichageTerrain(terrain);
        menuPanel = new MenuPanel();

        setLayout(new BorderLayout());
        this.add(affichageTerrain, BorderLayout.CENTER);
        this.add(menuPanel, BorderLayout.EAST);

    }

    public AffichageTerrain getAffichageTerrain() {
        return affichageTerrain;
    }

    public MenuPanel getMenuPanel() {
        return menuPanel;
    }
  
    private Case selectedCase = null;

    public void setSelectedCase(Case c) {
        this.selectedCase = c;
        refreshMenuIfSelected();  // Mise a jour immediate apres un clic.
    }

    public void refreshMenuIfSelected() {
        if (selectedCase != null)
            menuPanel.updateCase(selectedCase);  // Lit l'etat courant du modele.
    }
}
