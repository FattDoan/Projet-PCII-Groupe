package controller;

import java.awt.event.*;
import model.*;
import view.*;

/**
 * Gère les clics souris (bouton gauche, clic court) sur AffichageTerrain.
 * Utilise Camera.screenToGridX/Y pour tenir compte du pan ET du zoom.
 */
public class ReactionClic implements MouseListener {
 
    private final Affichage        affichage;
    private final Terrain          terrain;
    private final CameraController cameraController; // pour lire isDragging()
 
    public ReactionClic(Affichage affichage, Terrain terrain,
                        CameraController cameraController) {
        this.affichage        = affichage;
        this.terrain          = terrain;
        this.cameraController = cameraController;
        affichage.getAffichageTerrain().addMouseListener(this);
    }
 
    @Override public void mousePressed(MouseEvent e) {}
 
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) return;
        // Ignore l'événement s'il s'agissait d'un glisser et non d'un clic.
        if (cameraController.isDragging()) return;
 
        Camera cam = affichage.getCamera();
        if (cam == null) return;
 
        int gx = cam.screenToGridX(e.getX());
        int gy = cam.screenToGridY(e.getY());
 
        if (gx < 0 || gx >= terrain.getTaille() ||
            gy < 0 || gy >= terrain.getTaille()) {
            affichage.hideMenu(); return;
        }
 
        Case c = terrain.getCase(gx, gy);

        affichage.getAffichageTerrain().setSelectedCase(c);
        affichage.showMenu(c);
    }
 
    @Override public void mouseClicked (MouseEvent e) {}
    @Override public void mouseEntered (MouseEvent e) {}
    @Override public void mouseExited  (MouseEvent e) {}
}
 
