package controller;

import java.awt.event.*;
import model.*;
import view.*;
import view.Camera;
import model.unite.Unite;
import model.unite.commande.*;
import view.UnitActionCallback;
/**
 * Gère les clics souris (bouton gauche, clic court) sur AffichageTerrain.
 * Utilise Camera.screenToGridX/Y pour tenir compte du pan ET du zoom.
 */
public class ReactionClic implements MouseListener, UnitActionCallback {
 
    private final Affichage        affichage;
    private final Terrain          terrain;
    private final CameraController cameraController; // pour lire isDragging()

    // Etat pour l'unite
    public enum Mode { NORMAL, AWAITING_DESTINATION }
    private Mode mode = Mode.NORMAL;
    private Unite pendingUnite = null; // l'unité qui a demandé un ordre de déplacement 
                                       // et attend que le joueur clique sur la destination

    public ReactionClic(Affichage affichage, Terrain terrain,
                        CameraController cameraController) {
        this.affichage        = affichage;
        this.terrain          = terrain;
        this.cameraController = cameraController;
        affichage.getAffichageTerrain().addMouseListener(this);

        // ESC cancels destination-picking without doing anything
        affichage.getAffichageTerrain().addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) cancelMode();
            }
        });
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

        // C'est ce que comprend getUniteAtPixel().
        float worldPX = e.getX() + cam.getOffsetX();
        float worldPY = e.getY() + cam.getOffsetY();

        // ── Mode: AWAITING_DESTINATION ────────────────────────────────
        if (mode == Mode.AWAITING_DESTINATION) {
            if (gx >= 0 && gx < terrain.getTaille() &&
                gy >= 0 && gy < terrain.getTaille()) {
                Selectable s = affichage.getElementAtPixel(worldPX, worldPY);
                if (s instanceof Case c &&
                    !(c.estVide() || (c.aBatiment() && c.getBatiment() instanceof Route))) {   
                    // Clic sur une case non vide: interdit
                    affichage.getAffichageTerrain().showWarning("Case occupée, choisissez une autre destination.", e.getX(), e.getY());
                    return;
                }

                pendingUnite.annulerCommandes();
                pendingUnite.ajouterCommande(new CommandeDeplacement(gx, gy, worldPX, worldPY));
            }
            cancelMode();   // always exit this mode after a click
            return;
        }


        // ── Mode: NORMAL ──────────────────────────────────────────────-
 
        if (gx < 0 || gx >= terrain.getTaille() ||
            gy < 0 || gy >= terrain.getTaille()) {
            affichage.hideMenu(); 
            return;
        }

        Selectable s = affichage.getElementAtPixel(worldPX, worldPY);
        if (s != null) {
            affichage.getAffichageTerrain().setSelectedElement(s);
            if (s instanceof Unite) {
                affichage.setUnitCallback(this); // pour que les boutons du menu d'unité appellent nos méthodes onDeplacer, onMiner, etc.
            } 
            affichage.showMenu(s);
        }
    }

    // ── Called by unit menu "Deplacer" button 
    public void enterDeplacementMode(Unite u) {
        mode         = Mode.AWAITING_DESTINATION;
        pendingUnite = u;
        affichage.getAffichageTerrain().setAwaitingDestination(true);
    }

    public void cancelMode() {
        mode         = Mode.NORMAL;
        pendingUnite = null;
        affichage.getAffichageTerrain().setAwaitingDestination(false);
    }


    @Override 
    public void onDeplacer(Unite u) {
        enterDeplacementMode(u);
    }

    @Override
    public void onMiner(Unite u) {}

    @Override
    public void onAttaquer(Unite u) {}

    @Override
    public void onDefendre(Unite u) {}

    @Override public void mouseClicked (MouseEvent e) {}
    @Override public void mouseEntered (MouseEvent e) {}
    @Override public void mouseExited  (MouseEvent e) {}
}
 
