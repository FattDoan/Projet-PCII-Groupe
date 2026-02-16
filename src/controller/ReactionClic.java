package controller;

import java.awt.event.*;
import model.*;
import view.*;

public class ReactionClic implements MouseListener {
    private final Affichage affichage;
    private final EventHandler eventHandler;
    private final Terrain terrain;

    private final int terrainScreenSize;
    
    // menu à droite de la grille, pour les boutons d'action 
    // ou les infos sur les cases
    private final int menuScreenWidth; 
    // menuScreenHeight = getScreenHeight() = terrainScreenSize
    private final int menuScreenHeight;


    public ReactionClic(Affichage affichage, Terrain terrain, EventHandler eventHandler) {
        this.affichage = affichage;
        this.affichage.addMouseListener(this);
        this.eventHandler = eventHandler;
        this.terrain = terrain;
        this.terrainScreenSize = getCaseSize() * terrain.getTaille();
        this.menuScreenWidth = getScreenWidth() - terrainScreenSize;
        this.menuScreenHeight = getScreenHeight();
    }

    // pour l'instant, on suppose les tailles fixes
    // au lieu de appeler les fonctions de Affichage
    private int getScreenWidth() {
        return 1200;
    }
    private int getScreenHeight() {
        return 800;
    }
    private int getCaseSize() {
        return 10; 
    }
    
    private int getGridX(int screenX) {
        return screenX / getCaseSize();
    }
    private int getGridY(int screenY) {
        return screenY / getCaseSize();
    }

    @Override 
    public void mouseClicked(MouseEvent e) {
        if (e.getX() >= terrainScreenSize) {
            System.out.println("[Controller] Clic dans le menu: x=" + e.getX() + ", y=" + e.getY());
            eventHandler.handleClicDansMenu(e.getX() - terrainScreenSize, e.getY()); 
            return;
        }

        int gridX = getGridX(e.getX());
        int gridY = getGridY(e.getY());
        if (gridX >= 0 && gridX < terrain.getTaille() && gridY >= 0 && gridY < terrain.getTaille()) {
            eventHandler.handleClicSurCase(terrain.getCase(gridX, gridY)); 
        }
        else {
            System.out.println("[Controller] Clic hors de la grille: x=" + gridX + ", y=" + gridY);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
