package view;

import javax.swing.JFrame;

import model.Terrain;

/** La fenêtre principale de l'application */
public class Fenetre extends JFrame {
    private Affichage affichage;
    /** Constructeur de la fenêtre principale. Génère un affichage de la grille et du menu à partir du terrain donné.
     * @param titre le titre de la fenêtre
     * @param terrain le terrain à afficher dans la fenêtre
     */
    public Fenetre(String titre, Terrain terrain) {
        super(titre);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        affichage = new Affichage(terrain);
        this.add(affichage);

        // TODO: ajouter ici les autres éléments relatifs à la fenêtre (menu, threads, MouseListener, etc.)

        this.pack();
        this.setVisible(true);
    }

    public Affichage getAffichage() {
        return affichage;
    }

}
