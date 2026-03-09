package view;

import javax.swing.*;
import java.awt.*;

import model.Case;
import model.Terrain;

/** JPanel qui affiche le terrain (la grille) */
public class AffichageTerrain extends JPanel {
    private Terrain terrain;

    public AffichageTerrain(Terrain terrain) {
        super();
        this.terrain = terrain;

        // calcul des dimensions de la grille et de la fenêtre en fonction de la taille du terrain
    
        // la taille de la grille
        this.setPreferredSize(new java.awt.Dimension(Affichage.LARGEUR_GRILLE,
                                                     Affichage.HAUTEUR_GRILLE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // on affiche chaque case de la grille en fonction de son type (vide, minerai, bâtiment, etc.)
        for (int i = 0; i < terrain.getTaille(); ++i) {
            for (int j = 0; j < terrain.getTaille(); ++j) {
                Case c = terrain.getCase(i, j);
                AffichageCases.afficheCase(g, c);
            }
        }

    }

}
