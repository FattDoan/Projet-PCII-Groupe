package controller;
 
import model.Camera;
import model.Case;
import model.Terrain;
import view.AffichageTerrain;
import java.awt.event.*;
 
public class ReactionHover implements MouseMotionListener {
 
    private final AffichageTerrain view;
    private final Terrain terrain;
    private final Camera camera;
    private Case lastHovered = null;
 
    public ReactionHover(AffichageTerrain view, Terrain terrain, Camera camera) {
        this.view = view; this.terrain = terrain; this.camera = camera;
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
            if (lastHovered!=null) { view.setHoveredCase(null); lastHovered=null; }
            return;
        }
        Case c = terrain.getCase(gx, gy);
        // Évite les repaint inutiles si la case n'a pas changé.
        if (c == lastHovered) return;
        lastHovered = c;
        // On ne met en surbrillance que les cases non vides.
        view.setHoveredCase(c.estVide() ? null : c);
    }
}
