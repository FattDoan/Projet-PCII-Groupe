package controller;

import java.awt.event.*;
import model.*;
import view.*;

public class ReactionClic implements MouseListener {
    private final Affichage affichage;
    private final EventHandler eventHandler;
    private final Terrain terrain;

    public ReactionClic(Affichage affichage, Terrain terrain, EventHandler eventHandler) {
        this.affichage = affichage;
        this.eventHandler = eventHandler;
        this.terrain = terrain;
        this.affichage.addMouseListener(this);
    }


    private int getCaseSize() {
        return Affichage.TAILLE_CASE; 
    }

    private int getTerrainWidth() {
        return terrain.getTaille() * getCaseSize();
    }

    private int getTerrainHeight() {
        return terrain.getTaille() * getCaseSize();
    }

    private int getMenuWidth() {
        return affichage.getWidth() - getTerrainWidth();
    }

    // Conversion des coordonnées de la souris en coordonnées de la grille
    private int getGridX(int pixelX) {
        return pixelX / getCaseSize();
    }

    private int getGridY(int pixelY) {
        return pixelY / getCaseSize();
    }

    private enum ClickContext {
        GRID,
        MENU
    }


    private ClickContext getClickContext(int x, int y) {
        assert x >= 0 && y >= 0 : "Coordonnées de clic négatives: x=" + x + ", y=" + y; 
        // Clic dans le menu à droite de la grille
        if (x > getTerrainWidth()) {
            return ClickContext.MENU;
        }
        // Clic dans la grille
        else {
            return ClickContext.GRID;
        }
    }



    @Override 
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        ClickContext ctx = getClickContext(x, y);
        switch(ctx) {
            case GRID : 
                int gridX = getGridX(x);
                int gridY = getGridY(y);
                assert gridX < terrain.getTaille() && gridY < terrain.getTaille() : "Clic hors de la grille: gridX=" + gridX + ", gridY=" + gridY;
                System.out.println("[ReactionClic] Clic dans la grille: x=" + x + ", y=" + y + " => gridX=" + gridX + ", gridY=" + gridY);
                eventHandler.handleClicSurCase(terrain.getCase(gridX, gridY));
                break;

            case MENU :
                System.out.println("[ReactionClic] Clic dans le menu: x=" + x + ", y=" + y);
                eventHandler.handleClicDansMenu(x - getTerrainWidth(), y);
                break;

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
