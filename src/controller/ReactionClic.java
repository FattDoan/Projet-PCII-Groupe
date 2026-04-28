package controller;

import java.awt.event.*;
import model.*;
import view.*;
import model.unite.Unite;
import model.unite.commande.*;
/**
 * Gère les clics souris (bouton gauche, clic court) sur AffichageTerrain.
 * Utilise Camera.screenToGridX/Y pour tenir compte du pan ET du zoom.
 */
public class ReactionClic implements MouseListener, UnitActionCallback {
 
    private final Affichage        affichage;
    private final Terrain          terrain;
    private final CameraController cameraController; // pour lire isDragging()

    // Etat pour l'unite
    public enum Mode { 
        NORMAL, 
        AWAITING_DESTINATION,
        AWAITING_MINING_TARGET,
        AWAITING_BUILD_TARGET
    }
    private Mode mode = Mode.NORMAL;
    private Unite pendingUnite = null; // l'unité qui a demandé un ordre de déplacement 
                                       // et attend que le joueur clique sur la destination

    public ReactionClic(Affichage affichage, Terrain terrain,
                        CameraController cameraController) {
        this.affichage        = affichage;
        this.terrain          = terrain;
        this.cameraController = cameraController;
        affichage.getAffichageTerrain().addMouseListener(this);

        // ESC annule la selection de destination sans action.
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
 
        int gx = Camera.getInstance().screenToGridX(e.getX());
        int gy = Camera.getInstance().screenToGridY(e.getY());

        // C'est ce que comprend getUniteAtPixel().
        float worldPX = Camera.getInstance().screenToWorldX(e.getX());
        float worldPY = Camera.getInstance().screenToWorldY(e.getY());

        // ── Mode : AWAITING_DESTINATION ────────────────────────────────
        switch (mode) {
            case AWAITING_DESTINATION -> handleDestinationClick(gx, gy, worldPX, worldPY, e);
            case AWAITING_MINING_TARGET -> handleMiningTargetClick(gx, gy, worldPX, worldPY, e);
            case AWAITING_BUILD_TARGET -> handleBuildTargetClick(gx, gy, worldPX, worldPY, e);
            case NORMAL -> handleNormalClick(gx, gy, worldPX, worldPY, e);
        }
    }
    
    private void handleDestinationClick(int gx, int gy, float worldPX, float worldPY, MouseEvent e) {
        if (gx < 0 || gx >= terrain.getTaille() ||
            gy < 0 || gy >= terrain.getTaille()) {
            affichage.getAffichageTerrain().showWarning("Destination hors carte, choisissez une autre destination.", e.getX(), e.getY());
            return;
        }
        Selectable s = affichage.getElementAtPixel(worldPX, worldPY);
        if (s instanceof Case c &&
            !(c.estAccessible())) {   
            // Clic sur une case non vide: interdit
            affichage.getAffichageTerrain().showWarning("Case occupée, choisissez une autre destination.", e.getX(), e.getY());
            return;
        }

        pendingUnite.annulerCommandes();
        pendingUnite.ajouterCommande(new CommandeDeplacement(worldPX, worldPY));

        cancelMode();   // on sort toujours de ce mode apres un clic
    }

    private void handleNormalClick(int gx, int gy, float worldPX, float worldPY, MouseEvent e) {
        if (gx < 0 || gx >= terrain.getTaille() ||
            gy < 0 || gy >= terrain.getTaille()) {
            affichage.hideMenu(); 
            return;
        }

        Selectable s = affichage.getElementAtPixel(worldPX, worldPY);
        if (s != null) {
            affichage.getAffichageTerrain().setSelectedElement(s);
            if (s instanceof Unite) {
                affichage.setUnitCallback(this); // pour que les boutons du menu d'unite appellent onDeplacer/onMiner/etc.
            } 
            affichage.showMenu(s);
        }
    }

     private void handleMiningTargetClick(int gx, int gy, float worldPX, float worldPY, MouseEvent e) {
        if (gx < 0 || gx >= terrain.getTaille() ||
            gy < 0 || gy >= terrain.getTaille()) {
            affichage.getAffichageTerrain().showWarning("Cible hors carte, choisissez une autre cible.", e.getX(), e.getY());
            return;
        }
        Selectable s = affichage.getElementAtPixel(worldPX, worldPY);
        if (s instanceof Case c &&
            !(c.aMinerai())) {   
            // Clic sur une case sans minerai: interdit
            affichage.getAffichageTerrain().showWarning("Pas de minerai ici, choisissez une autre cible.", e.getX(), e.getY());
            return;
        }

        pendingUnite.annulerCommandes();
        pendingUnite.ajouterCommande(new CommandeDeplacement(worldPX, worldPY));
        pendingUnite.ajouterCommande(new CommandeMiner(terrain.getBatimentMaitre()));;

        cancelMode();   // on sort toujours de ce mode apres un clic
    }

    private void handleBuildTargetClick(int gx, int gy, float worldPX, float worldPY, MouseEvent e) {
        if (gx < 0 || gx >= terrain.getTaille() ||
            gy < 0 || gy >= terrain.getTaille()) {
            affichage.getAffichageTerrain().showWarning("Cible hors carte, choisissez une autre cible.", e.getX(), e.getY());
            return;
        }
        Selectable s = affichage.getElementAtPixel(worldPX, worldPY);
        // Clic sur une case qui nest pas un batiment ayant des PVs inférieurs à son max: interdit
        if (!(s instanceof Case c &&
            c.aBatiment() && c.getBatiment().getHP() < c.getBatiment().getHPMax())) { 
            affichage.getAffichageTerrain().showWarning("Cible invalide", e.getX(), e.getY());
            return;
        }
        Batiment target = ((Case)s).getBatiment();


        pendingUnite.annulerCommandes();
        pendingUnite.ajouterCommande(new CommandeDeplacement(worldPX, worldPY));
        pendingUnite.ajouterCommande(new CommandeConstruire(target));

        cancelMode();   // on sort toujours de ce mode apres un clic
    }


    // ── Appelé par le bouton "Deplacer" du menu d'unite
    public void enterDeplacementMode(Unite u) {
        mode         = Mode.AWAITING_DESTINATION;
        pendingUnite = u;
        affichage.getAffichageTerrain().setAwaitingDestination(true);
    }

    public void enterMiningTargetMode(Unite u) {
        mode = Mode.AWAITING_MINING_TARGET;
        pendingUnite = u;
        affichage.getAffichageTerrain().setAwaitingDestination(true);
    }

    public void enterBuildTargetMode(Unite u) {
        mode = Mode.AWAITING_BUILD_TARGET;
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
    public void onMiner(Unite u) {
        enterMiningTargetMode(u); 
    }

    @Override
    public void onAttaquer(Unite u) {
    }

    @Override
    public void onDefendre(Unite u) {}

    @Override 
    public void onConstruire(Unite u) {
        enterBuildTargetMode(u);
    }

    @Override public void mouseClicked (MouseEvent e) {}
    @Override public void mouseEntered (MouseEvent e) {}
    @Override public void mouseExited  (MouseEvent e) {}
}
 
