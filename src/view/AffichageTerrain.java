package view;

import view.Camera;
import model.Case;
import model.Terrain;
import model.Selectable;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import model.unite.Unite;

public class AffichageTerrain extends JPanel {

    private final Terrain terrain;

    private Selectable selectedElement = null; // peut être une Case ou une Unite
    private Selectable hoveredElement = null;  // peut être une Case ou une Unite

    // Destination-picking mode (after "Deplacer" is clicked in unit menu)
    private boolean awaitingDestination = false;
    private int[] destinationPreview = null; // grid coords [gx, gy] of hovered cell while picking
    private float previewWorldPX, previewWorldPY; // world coords of mouse cursor during destination picking (for warning bubble positioning)

    // Avertissement affiché temporairement en cas d'action interdite (ex: déplacement hors portée).
    private final WarningBubble warning = new WarningBubble();

    private static final Color C_AMBER = new Color(255, 185, 30, 80);
    private static final Color C_DEST_TINT    = new Color(100, 180, 255,  40);
    private static final Color C_DEST_BORDER  = new Color(100, 180, 255, 190);

    public AffichageTerrain(Terrain terrain) {
        super();
        this.terrain = terrain;
        setFocusable(true);

        // For warning bubble: if its out of bounds, reposition it
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override public void mouseMoved(java.awt.event.MouseEvent e) {
                boolean wasHov = warning.isVisible() && warning.getCloseRect().contains(e.getPoint());
                warning.setXHovered(wasHov);
                if (warning.isVisible()) repaint();
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseReleased(java.awt.event.MouseEvent e) {
                if (warning.isVisible() && warning.getCloseRect().contains(e.getPoint())) {
                    warning.dismiss();
                    repaint();
                }
            }
        });
    }
    public Terrain getTerrain() { return terrain; }
    // ── Gestion du survol ─────────────────────────────────────────────────

    public void setHoveredElement(Selectable s) {
        if (s == hoveredElement) return;
        hoveredElement = s;
        repaint();
    }

    public void setSelectedElement(Selectable s) {
        if (s == selectedElement) return;
        selectedElement = s;
        repaint();
    }

    // ── Destination mode ─────────────────────────────────────────────────
    public void setAwaitingDestination(boolean b) {
        awaitingDestination = b;
        setCursor(b ? Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
                    : Cursor.getDefaultCursor());
        clearDestinationPreview();
        repaint();
    }

    public void showWarning(String msg, int cursorX, int cursorY) {
        warning.show(msg, cursorX, cursorY, getWidth(), getHeight());
        repaint();
    }

    // ── Rendu ──────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,     RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int cellSize = Case.TAILLE;
        int unitSize = AffichageUnites.TAILLE_UNITE;
        
        // Sauvegarde de la transformation pour restaurer correctement les overlays.
        AffineTransform originalTransform = g2.getTransform();

        int offsetX  = Camera.getInstance().getOffsetX();
        int offsetY  = Camera.getInstance().getOffsetY();
        float zoom = Camera.getInstance().getZoom(); 

        // Translation globale selon l'offset caméra.
        g2.translate(-offsetX, -offsetY);
        g2.scale(zoom, zoom);
    
        // Élagage du rendu: ne dessine que les cases visibles (partielles incluses).
        int firstCol = Math.max(0, (int)(Camera.getInstance().screenToWorldX(0) / cellSize));
        int firstRow = Math.max(0, (int)(Camera.getInstance().screenToWorldY(0) / cellSize));
        int lastCol  = Math.min(terrain.getTaille() - 1, 
                                (int)((Camera.getInstance().screenToWorldX(getWidth())  / cellSize)));
        int lastRow  = Math.min(terrain.getTaille() - 1, 
                                (int)((Camera.getInstance().screenToWorldY(getHeight()) / cellSize)));

        // Affichage des cases
        for (int i = firstCol; i <= lastCol; i++) {
            for (int j = firstRow; j <= lastRow; j++) {
                AffichageCases.afficheCase(g2, terrain.getCase(i, j));
            }
        }

        //Affichage des unites
        List <Unite> unites = terrain.getUnites();
        for (Unite u : unites) {
            AffichageUnites.afficheUnite(g2, u);
        }

        drawIndicatorForUnitPath(g2, cellSize);

        // Surbrillance légère de la case survolée.
        drawHoverIndicator(g2, cellSize, unitSize);

        // Encadrement renforcé de la case sélectionnée.
        drawSelectionIndicator(g2, cellSize, unitSize);

        // Restauration pour ne pas impacter les autres couches Swing.
        g2.setTransform(originalTransform);
    
        // Warning bubble must be displayed in screen space, not world(model) space
        warning.paint(g2);
    }

    // TODO: Unit hovered should be circle, not square like Case
    private void drawHoverIndicator(Graphics2D g2, int cellSize, int unitSize) {
        if (hoveredElement != null && hoveredElement != selectedElement) {
            int hx = (int)hoveredElement.getPX();
            int hy = (int)hoveredElement.getPY(); 

            int size = cellSize;
            if (hoveredElement instanceof Unite) {
                size = unitSize;
                hx -= unitSize/2;
                hy -= unitSize/2;
            }

            g2.setColor(new Color(255, 255, 255, 45)); 
            g2.fillRect(hx, hy, size, size);

            g2.setColor(new Color(255, 255, 255, 80));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRect(hx, hy, size - 1, size - 1);
        }
    }

    private void drawSelectionIndicator(Graphics2D g2, int cellSize, int unitSize) {
        if (selectedElement != null) {
            int sx = (int)selectedElement.getPX();
            int sy = (int)selectedElement.getPY();
 
            int size = cellSize;
            if (selectedElement instanceof Unite) {
                size = size/2; 
                sx -= size/2;
                sy -= size/2;
            }

            g2.setColor(new Color(C_AMBER.getRed(), C_AMBER.getGreen(), C_AMBER.getBlue(), 60));
            g2.setStroke(new BasicStroke(4.0f)); 
            g2.drawRect(sx, sy, size, size);

            g2.setColor(C_AMBER);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRect(sx, sy, size - 1, size - 1);    
        }
    }

    public boolean isAwaitingDestination() { return awaitingDestination; }

    public void setDestinationPreview(int gx, int gy, float worldPX, float worldPY) {
        destinationPreview = new int[]{gx, gy};
        previewWorldPX = worldPX;
        previewWorldPY = worldPY;
        repaint();
    }
    public void clearDestinationPreview() { destinationPreview = null; repaint(); }

    private void drawIndicatorForUnitPath(Graphics2D g2, int cellSize) {
        if (awaitingDestination && destinationPreview != null) {
            int dpx = destinationPreview[0] * cellSize;
            int dpy = destinationPreview[1] * cellSize;
            g2.setColor(C_DEST_TINT);
            g2.fillRect(dpx, dpy, cellSize, cellSize);
            float[] dash = {4f, 3f};
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, dash, 0));
            g2.setColor(C_DEST_BORDER);
            g2.drawRect(dpx + 1, dpy + 1, cellSize - 2, cellSize - 2);
        }
    }

}
