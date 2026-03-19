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
        int gx = camera.screenToGridX(px), gy = camera.screenToGridY(py);
        if (gx < 0 || gx>=terrain.getTaille() ||
            gy < 0 || gy>=terrain.getTaille()) {
            if (lastHovered!=null) { view.setHoveredCase(null); lastHovered=null; }
            return;
        }
        Case c = terrain.getCase(gx, gy);
        if (c == lastHovered) return;
        lastHovered = c;
        view.setHoveredCase(c.estVide() ? null : c);
    }
}
