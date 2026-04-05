package controller;
 
import view.Camera;
import model.Terrain;
import model.Selectable;
import view.AffichageTerrain;
import view.Affichage;
import java.awt.event.*;
 
public class ReactionHover implements MouseMotionListener {
 
    private final Affichage affichage;
    private final AffichageTerrain view;
    private final Terrain terrain;
    private final Camera camera;
    private Selectable lastHovered = null;
 
    public ReactionHover(Affichage affichage, Terrain terrain, Camera camera) {
        this.affichage = affichage;
        this.view = affichage.getAffichageTerrain();
        this.terrain = terrain; this.camera = camera;
        view.addMouseMotionListener(this);
    }
 
    @Override public void mouseMoved   (MouseEvent e) { update(e.getX(), e.getY()); }
    @Override public void mouseDragged (MouseEvent e) { update(e.getX(), e.getY()); }
 
    private void update(int px, int py) {
        // Conversion pixel -> grille en tenant compte du décalage caméra.
        int gx = camera.screenToGridX(px), gy = camera.screenToGridY(py);
        if (gx < 0 || gx>=terrain.getTaille() ||
            gy < 0 || gy>=terrain.getTaille()) {
            // Hors carte: on retire l'état de survol précédent.
            if (lastHovered!=null) { view.setHoveredElement(null); lastHovered=null; }
            return;
        }
      
        float worldPX = px + camera.getOffsetX();
        float worldPY = py + camera.getOffsetY();
        if (view.isAwaitingDestination()) {
            view.setDestinationPreview(gx, gy, worldPX, worldPY);
        }
        else view.clearDestinationPreview();

        Selectable s = affichage.getElementAtPixel(px + camera.getOffsetX(), py + camera.getOffsetY());
        // Évite les repaint inutiles si la case n'a pas changé.
        if (s == lastHovered) return;
        lastHovered = s;
        // On ne met en surbrillance que les cases non vides.
        view.setHoveredElement(s);
    }

}
