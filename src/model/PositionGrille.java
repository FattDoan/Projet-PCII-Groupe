package model;

/**
 * Représente une position (x, y) sur la grille de jeu.
 * Record immuable utilisé pour suivre les positions dans les collections.
 * 
 * @param x coordonnée horizontale (colonne)
 * @param y coordonnée verticale (ligne)
 */
public class PositionGrille {
    private final int x;
    private final int y;

    public PositionGrille(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
