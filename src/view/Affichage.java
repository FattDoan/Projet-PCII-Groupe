package view;
 
import view.Camera;
import model.Case;
import model.Terrain;
import model.unite.Unite;
import model.Selectable;
import java.util.List;
import javax.swing.*;
import java.awt.*;
 
public class Affichage extends JPanel {
 
    public static final int    MAX_VIEW_W         = 1300;
    public static final int    MAX_VIEW_H         = 700;
    public static final int    TAILLE_CASE        = 40;
    public static final double RATIO_LARGEUR_MENU = 0.30;
 
    // Dimensions réelles du terrain en pixels (hors clipping fenêtre).
    private final int largeurGrille;
    private final int hauteurGrille;

    // Dimensions visibles effectives de la fenêtre de jeu.
    private final int largeurVue;
    private final int hauteurVue;
 
    private final AffichageTerrain affichageTerrain;
    private final MenuPanel        menuPanel;
    private final int              menuW;
 
    // Caméra injectée plus tard par le contrôleur de jeu.
    private Camera camera = null;
 
    public Affichage(Terrain terrain) {
        super(null);
 
        // Conversion cases -> pixels.
        largeurGrille = terrain.getTaille() * TAILLE_CASE;
        hauteurGrille = terrain.getTaille() * TAILLE_CASE;

        // On borne la taille de vue pour garder une UI lisible et fluide.
        largeurVue = Math.min(largeurGrille, MAX_VIEW_W);
        hauteurVue = Math.min(hauteurGrille, MAX_VIEW_H);

        // Le panneau menu occupe un ratio fixe de la largeur de vue.
        menuW   = (int)(largeurVue * RATIO_LARGEUR_MENU);
 
        setPreferredSize(new Dimension(largeurVue, hauteurVue));
 
        // Couche 1: terrain principal.
        affichageTerrain = new AffichageTerrain(terrain);
        affichageTerrain.setBounds(0, 0, largeurVue, hauteurVue);
        add(affichageTerrain);
 
        // Couche 2: menu contextuel superposé.
        menuPanel = new MenuPanel(menuW, hauteurVue, this, terrain);
        menuPanel.setBounds(largeurVue - menuW, 0, menuW, hauteurVue);
        menuPanel.setVisible(false);
        add(menuPanel);

        // Z-order explicite pour éviter les surprises de rendu.
        setComponentZOrder(menuPanel, 0);
        setComponentZOrder(affichageTerrain, 1);
    }
 
    // Add this method to Affichage.java
    @Override
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        affichageTerrain.setBounds(0, 0, w, h);

        int mw = (int)(w * RATIO_LARGEUR_MENU);
        menuPanel.setBounds(w - mw, 0, mw, h);

        if (camera != null) camera.updateViewSize(w, h);
    }

    public void setCamera(Camera c) { this.camera = c; affichageTerrain.setCamera(c); }
    public Camera           getCamera()           { return camera; }
    public AffichageTerrain getAffichageTerrain() { return affichageTerrain; }
    public MenuPanel        getMenuPanel()        { return menuPanel; }
    public int getLargeurVue() { return largeurVue; }
    public int getHauteurVue() { return hauteurVue; }
    public int getLargeurGrille() { return largeurGrille; }
    public int getHauteurGrille() { return hauteurGrille; }
 
    public void showMenu(Selectable s) {
        menuPanel.setSelectedElement(s);
        menuPanel.refresh();
        menuPanel.setVisible(true);
    }

    // Speical case of showMenu since Unite has a callback for its menu actions.
    public void setUnitCallback(UnitActionCallback cb) {
        menuPanel.setUnitCallback(cb);
    } 

    public void hideMenu() {
        menuPanel.setVisible(false);
    }
 
    public boolean isMenuVisible()      { return menuPanel.isVisible(); }
    public void refreshMenuIfSelected() { if (menuPanel.isVisible()) menuPanel.refresh(); }

    @Override
    public boolean isOptimizedDrawingEnabled() {
        // Les composants se chevauchent volontairement (terrain + menu overlay),
        // on désactive donc l'optimisation Swing pour respecter le Z-order.
        return false; // Indique à Swing de conserver le calcul de superposition des couches.
    }

    // Get unit at pixel coordinates (px, py). Returns null if no unit is present at these coordinates.
    public Unite getUniteAtPixel(float px, float py) {
        float unitBase = camera.effectiveCellSize() / 2;
        List<Unite> unites = affichageTerrain.getTerrain().getUnites();
        for (Unite u : unites) {
            if (px >= u.getPX() - unitBase && px <= u.getPX() + unitBase
                && py >= u.getPY() - unitBase / 2 && py <= u.getPY() + unitBase) {
                return u;
            }
        }
        return null;
    }

    public Selectable getElementAtPixel(float px, float py) {
        // On privilégie le clic sur une unité
        Unite u = getUniteAtPixel(px, py);
        if (u != null) return u;
        Terrain terrain = affichageTerrain.getTerrain();

        // Sinon on retourne la case correspondante
        int gx = (int)(px / camera.effectiveCellSize());
        int gy = (int)(py / camera.effectiveCellSize());
        if (gx < 0 || gx >= terrain.getTaille()  || gy < 0 || gy >= terrain.getTaille()) {
            return null; // Hors du terrain
        }
        return terrain.getCase(gx, gy);
    }



}
