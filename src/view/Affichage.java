package view;
 
import model.Camera;
import model.Case;
import model.Terrain;
import javax.swing.*;
import java.awt.*;
 
public class Affichage extends JPanel {
 
    public static final int    MAX_VIEW_W         = 1300;
    public static final int    MAX_VIEW_H         = 700;
    public static final int    TAILLE_CASE        = 40;
    public static final double RATIO_LARGEUR_MENU = 0.30;
 
    public static int LARGEUR_GRILLE, HAUTEUR_GRILLE, LARGEUR, HAUTEUR;
 
    private final AffichageTerrain affichageTerrain;
    private final MenuPanel        menuPanel;
    private final int              menuW;
 
    private Camera camera = null;
 
    public Affichage(Terrain terrain) {
        super(null);
 
        LARGEUR_GRILLE = terrain.getTaille() * TAILLE_CASE;
        HAUTEUR_GRILLE = terrain.getTaille() * TAILLE_CASE;
        LARGEUR = Math.min(LARGEUR_GRILLE, MAX_VIEW_W);
        HAUTEUR = Math.min(HAUTEUR_GRILLE, MAX_VIEW_H);
        menuW   = (int)(LARGEUR * RATIO_LARGEUR_MENU);
 
        setPreferredSize(new Dimension(LARGEUR, HAUTEUR));
 
        affichageTerrain = new AffichageTerrain(terrain);
        affichageTerrain.setBounds(0, 0, LARGEUR, HAUTEUR);
        add(affichageTerrain);
 
        menuPanel = new MenuPanel(menuW, HAUTEUR, this);
        menuPanel.setBounds(LARGEUR - menuW, 0, menuW, HAUTEUR);
        menuPanel.setVisible(false);
        add(menuPanel);

        setComponentZOrder(menuPanel, 0);
        setComponentZOrder(affichageTerrain, 1);
    }
  
    public void setCamera(Camera c) { this.camera = c; affichageTerrain.setCamera(c); }
    public Camera           getCamera()           { return camera; }
    public AffichageTerrain getAffichageTerrain() { return affichageTerrain; }
    public MenuPanel        getMenuPanel()        { return menuPanel; }
 
    public void showMenu(Case c) {
        menuPanel.setSelectedCase(c);
        menuPanel.refresh();
        menuPanel.setVisible(true);
    }
 
    public void hideMenu() {
        menuPanel.setVisible(false);
    }
 
    public boolean isMenuVisible()      { return menuPanel.isVisible(); }
    public void refreshMenuIfSelected() { if (menuPanel.isVisible()) menuPanel.refresh(); }

    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false; // Tells Swing: "My children overlap, please calculate Z-order correctly!"
    }
}
