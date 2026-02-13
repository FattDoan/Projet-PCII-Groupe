package model;

/**
 * Représente une position (x, y) sur la grille de jeu.
 * Record immuable utilisé pour suivre les positions dans les collections.
 * 
 * @param x coordonnée horizontale (colonne)
 * @param y coordonnée verticale (ligne)
 */
public record PositionGrille(int x, int y) {}
