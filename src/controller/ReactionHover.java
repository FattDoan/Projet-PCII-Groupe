package controller;
 
import view.Camera;
import model.Terrain;
import model.Selectable;
import view.AffichageTerrain;
import view.Affichage;
import java.awt.event.*;

/**
 * Gère le survol de la souris pour mettre en surbrillance les éléments du terrain.
 * Utilise Camera.screenToGridX/Y pour tenir compte du pan ET du zoom.
 */
public class ReactionHover implements MouseMotionListener {
 
    private final Affichage affichage;
    private final AffichageTerrain view;
    private final Terrain terrain;
    private Selectable lastHovered = null;
 
    public ReactionHover(Affichage affichage, Terrain terrain) {
        this.affichage = affichage;
        this.view = affichage.getAffichageTerrain();
        this.terrain = terrain; 
        view.addMouseMotionListener(this);
    }
 
    @Override public void mouseMoved   (MouseEvent e) { update(e.getX(), e.getY()); }
    @Override public void mouseDragged (MouseEvent e) { update(e.getX(), e.getY()); }
 
    private void update(int px, int py) {
        // Conversion pixel -> grille en tenant compte du décalage caméra.
        int gx = Camera.getInstance().screenToGridX(px), gy = Camera.getInstance().screenToGridY(py);
        if (gx < 0 || gx>=terrain.getTaille() ||
            gy < 0 || gy>=terrain.getTaille()) {
            // Hors carte: on retire l'état de survol précédent.
            if (lastHovered!=null) { view.setHoveredElement(null); lastHovered=null; }
            return;
        }
      
        float worldPX = Camera.getInstance().screenToWorldX(px);
        float worldPY = Camera.getInstance().screenToWorldY(py);

        if (view.isAwaitingDestination()) {
            view.setDestinationPreview(gx, gy, worldPX, worldPY);
        }
        else view.clearDestinationPreview();

        Selectable s = affichage.getElementAtPixel(worldPX, worldPY);
        // Évite les repaint inutiles si la case n'a pas changé.
        if (s == lastHovered) return;
        lastHovered = s;
        // On ne met en surbrillance que les cases non vides.
        view.setHoveredElement(s);
    }

}
