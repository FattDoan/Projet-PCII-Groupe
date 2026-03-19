package controller;
 
import model.Camera;
import view.Affichage;
import view.AffichageTerrain;
import java.awt.event.*;
 
/**
 * Pan via drag (BUTTON1) ou touches fléchées / WASD.
 * Drag distingué du clic via DRAG_THRESHOLD pixels.
 */
public class CameraController implements MouseListener, MouseMotionListener, KeyListener {
 
    private static final int PAN_KEY_SPEED  = 20;  // pixels par touche
    private static final int DRAG_THRESHOLD = 5;   // pixels avant d'activer le drag
 
    private final Camera camera;
    private final AffichageTerrain view;
 
    // Drag state
    private boolean dragging   = false;
    private boolean dragIntent = false;
    private int dragStartX, dragStartY;
    private int camStartX,  camStartY;
 
    public CameraController(Camera camera, Affichage aff) {
        this.camera = camera;
        this.view   = aff.getAffichageTerrain();
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
        view.setFocusable(true);
        view.addKeyListener(this);
        view.requestFocusInWindow();
    }
 
    /** Read by ReactionClic to skip click handling after a drag. */
    public boolean isDragging() { return dragging; }
 
    // ── Mouse drag ────────────────────────────────────────────────────────
 
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            dragIntent = true;
            dragStartX = e.getX();
            dragStartY = e.getY();
            camStartX  = camera.getOffsetX();
            camStartY  = camera.getOffsetY();
        }
    }
 
    @Override
    public void mouseDragged(MouseEvent e) {
        if (!dragIntent) return;
        int dx = e.getX() - dragStartX;
        int dy = e.getY() - dragStartY;
        if (!dragging && dx*dx + dy*dy < DRAG_THRESHOLD * DRAG_THRESHOLD) return;
        dragging = true;
        camera.setOffset(camStartX - dx, camStartY - dy);
        view.repaint();
    }
 
    @Override
    public void mouseReleased(MouseEvent e) {
        dragIntent = false;
        dragging   = false;
    }
 
    // ── Keyboard pan ──────────────────────────────────────────────────────
 
    @Override
    public void keyPressed(KeyEvent e) {
        int dx = 0, dy = 0;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> dx = -PAN_KEY_SPEED;
            case KeyEvent.VK_RIGHT -> dx =  PAN_KEY_SPEED;
            case KeyEvent.VK_UP -> dy = -PAN_KEY_SPEED;
            case KeyEvent.VK_DOWN -> dy =  PAN_KEY_SPEED;
            default -> { return; }
        }
        camera.move(dx, dy);
        view.repaint();
    }
 
    @Override public void mouseClicked (MouseEvent e) {}
    @Override public void mouseEntered (MouseEvent e) { view.requestFocusInWindow(); }
    @Override public void mouseExited  (MouseEvent e) {}
    @Override public void mouseMoved   (MouseEvent e) {}
    @Override public void keyReleased  (KeyEvent e)   {}
    @Override public void keyTyped     (KeyEvent e)   {}
}
