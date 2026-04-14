package controller;
 
import view.Camera;
import view.Affichage;
import view.AffichageTerrain;
import java.awt.event.*;
import java.awt.MouseInfo; 
import java.awt.Point;
/**
 * Déplacement de la caméra par glisser (bouton gauche) ou clavier.
 * Le glisser est distingué d'un clic simple via un seuil de pixels.
 */
public class CameraController implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
 
    private static final int PAN_KEY_SPEED  = 20;  // pixels par touche
    private static final int DRAG_THRESHOLD = 5;   // pixels avant d'activer le drag
 
    private final AffichageTerrain view;
 
    // État interne du glisser-déposer caméra.
    private boolean dragging   = false;
    private boolean dragIntent = false;
    private int dragStartX, dragStartY;
    private int camStartX,  camStartY;
 
    public CameraController(Affichage aff) {
        this.view   = aff.getAffichageTerrain();
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
        view.setFocusable(true);
        view.addMouseWheelListener(this);
        view.addKeyListener(this);
        view.requestFocusInWindow();
    }
 
    /** Utilisé par ReactionClic pour ignorer un clic après un glisser. */
    public boolean isDragging() { return dragging; }
 
    // ── Gestion du glisser souris ─────────────────────────────────────────
 
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            dragIntent = true;
            dragStartX = e.getX();
            dragStartY = e.getY();
            camStartX  = Camera.getInstance().getOffsetX();
            camStartY  = Camera.getInstance().getOffsetY();
        }
    }
 
    @Override
    public void mouseDragged(MouseEvent e) {
        if (!dragIntent) return;
        int dx = e.getX() - dragStartX;
        int dy = e.getY() - dragStartY;
        if (!dragging && dx*dx + dy*dy < DRAG_THRESHOLD * DRAG_THRESHOLD) return;
        dragging = true;
        Camera.getInstance().setOffset(camStartX - dx, camStartY - dy);
        view.repaint();
    }
 
    @Override
    public void mouseReleased(MouseEvent e) {
        dragIntent = false;
        dragging   = false;
    }
 
    // ── Gestion clavier ────────────────────────────────────────────────────
    @Override
    public void keyPressed(KeyEvent e) {
        int dx = 0, dy = 0;

        // 1. Get the mouse position relative to the view (Default to center if window isn't ready)
        int mouseX = view.getWidth() / 2;
        int mouseY = view.getHeight() / 2;
        
        if (view.isShowing()) {
            Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
            Point viewLoc = view.getLocationOnScreen();
            mouseX = mouseLoc.x - viewLoc.x;
            mouseY = mouseLoc.y - viewLoc.y;
        }

        // 2. Handle the keys
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> dx = -PAN_KEY_SPEED;
            case KeyEvent.VK_RIGHT -> dx =  PAN_KEY_SPEED;
            case KeyEvent.VK_UP -> dy = -PAN_KEY_SPEED;
            case KeyEvent.VK_DOWN -> dy =  PAN_KEY_SPEED;
            case KeyEvent.VK_EQUALS, KeyEvent.VK_PLUS -> {
                Camera.getInstance().zoomAt(mouseX, mouseY, Camera.ZOOM_STEP);
                view.repaint();
                return;
            }
            case KeyEvent.VK_MINUS ->  {
                Camera.getInstance().zoomAt(mouseX, mouseY, -Camera.ZOOM_STEP);
                view.repaint();
                return;
            }
            default -> { return; }
        }
        
        Camera.getInstance().move(dx, dy);
        view.repaint();
    }


    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        float delta = e.getWheelRotation() < 0 ? Camera.ZOOM_STEP : -Camera.ZOOM_STEP;
        Camera.getInstance().zoomAt(e.getX(), e.getY(), delta);
        view.repaint();
    }

    @Override public void mouseClicked (MouseEvent e) {}
    @Override public void mouseEntered (MouseEvent e) { view.requestFocusInWindow(); }
    @Override public void mouseExited  (MouseEvent e) {}
    @Override public void mouseMoved   (MouseEvent e) {}
    @Override public void keyReleased  (KeyEvent e)   {}
    @Override public void keyTyped     (KeyEvent e)   {}
}
