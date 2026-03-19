package view;

import model.Camera;
import model.Case;
import model.Terrain;
import model.Minerai;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AffichageTerrain extends JPanel {

    private final Terrain terrain;
    private Camera camera = null;   // null jusqu'à l'injection par GameController

    private Case hoveredCase = null;
    private Case selectedCase = null;

    private final List<Minerai> minerais = new CopyOnWriteArrayList<>();
 
    private static final Color C_AMBER = new Color(255, 185, 30, 80);

    public AffichageTerrain(Terrain terrain) {
        super();
        this.terrain = terrain;
        setFocusable(true);
    }

    // ── Injection caméra (post-construction) ─────────────────────────────

    /** Appelé par Affichage.setCamera(), lui-même appelé par GameController. */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera() { return camera; }

    // ── Hover ─────────────────────────────────────────────────────────────

    public void setHoveredCase(Case c) {
        if (c == hoveredCase) return;
        hoveredCase = c;
        repaint();
    }

    public void setSelectedCase(Case c) {
        if (c == selectedCase) return;
        selectedCase = c;
        repaint();
    }

    // ── Rendu ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Guard : camera pas encore injectée au premier repaint spurieux
        if (camera == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,     RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Sauvegarde pour restauration après — protège les overlays Swing (MenuPanel)
        AffineTransform originalTransform = g2.getTransform();

        int   offsetX  = camera.getOffsetX();
        int   offsetY  = camera.getOffsetY();
        float cellSize = camera.effectiveCellSize();
        int   base     = camera.getBaseCellSize();

        // Applique translation
        g2.translate(-offsetX, -offsetY);

        // Culling — ne dessine que les cases visibles, cases partielles incluses
        int firstCol = Math.max(0, (int)(offsetX / cellSize));
        int firstRow = Math.max(0, (int)(offsetY / cellSize));
        int lastCol  = Math.min(terrain.getTaille() - 1, (int)((offsetX + getWidth())  / cellSize));
        int lastRow  = Math.min(terrain.getTaille() - 1, (int)((offsetY + getHeight()) / cellSize));

        for (int i = firstCol; i <= lastCol; i++) {
            for (int j = firstRow; j <= lastRow; j++) {
                AffichageCases.afficheCase(g2, terrain.getCase(i, j));    
            }
        }

        // Make hovered case whiten up
        if (hoveredCase != null && hoveredCase != selectedCase) {
            int hx = hoveredCase.getX() * base;
            int hy = hoveredCase.getY() * base;

            g2.setColor(new Color(255, 255, 255, 45)); 
            g2.fillRect(hx, hy, base, base);

            g2.setColor(new Color(255, 255, 255, 80));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRect(hx, hy, base - 1, base - 1);
        }

        // Draw selected case border
        if (selectedCase != null) {
            int sx = selectedCase.getX() * base;
            int sy = selectedCase.getY() * base;

            g2.setColor(new Color(C_AMBER.getRed(), C_AMBER.getGreen(), C_AMBER.getBlue(), 60));
            g2.setStroke(new BasicStroke(4.0f)); 
            g2.drawRect(sx, sy, base, base);

            g2.setColor(C_AMBER);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRect(sx + 1, sy + 1, base - 2, base - 2);
            
        }
        // Restaure la transformation
        g2.setTransform(originalTransform);
    }
}
