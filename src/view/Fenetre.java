package view;

import javax.swing.JFrame;

import model.Terrain;
import java.awt.Dimension;

/** La fenêtre principale de l'application */
public class Fenetre extends JFrame {
    private final Affichage affichage;    // pour afficher la grille de jeu
    
    private final Terrain terrain;          // le terrain de jeu affiché dans la fenêtre
    /** Constructeur de la fenêtre principale. Génère un affichage de la grille et du menu à partir du terrain donné.
     * @param titre le titre de la fenêtre
     * @param terrain le terrain à afficher dans la fenêtre
     */
    public Fenetre(String titre, Terrain terrain) {
        super(titre);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.terrain = terrain;
        affichage = new Affichage(terrain);
        this.add(affichage);

        // TODO: ajouter ici les autres éléments de fenêtre (barre d'outils,
        // raccourcis clavier globaux, etc.) selon les besoins produit.

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        // if JFrame is resized, 
        // we update all the components of the window to 
        // fit the new size (especially MenuPanel)

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                affichage.revalidate();
                affichage.repaint();
            }
        });
    }



    public Affichage getAffichage() {
        return this.affichage;
    }

    public Terrain getTerrain() {
        return this.terrain;
    }
}
