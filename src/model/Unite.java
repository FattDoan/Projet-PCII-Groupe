package model;

import common.Validation;
/**
 * Classe abstraite représentant une unité (des travailleurs ou des ennemis) dans le jeu.
 * Les unités ont une position sur la grille, une santé (HP), et peuvent se déplacer
 * en leurs donnant les commandes.
 */
import java.util.ArrayDeque;

public abstract class Unite implements Runnable {
    private int x;
    private int y;
    private int hp;
    private TypeUnite typeUnite;
    private ArrayDeque<Tache> taches; // Liste des tâches assignées à l'unité
    private final Terrain terrain;

    private final int VITESSE_DEPLACEMENT = 1;  // 1 case par seconds 

    public Unite(int x, int y, int hp, TypeUnite typeUnite, Terrain terrain) {
        this.x = x;
        this.y = y;
        this.typeUnite = typeUnite;
        this.hp = hp;
        this.terrain = terrain;
        taches = new ArrayDeque<>();
    }
    
    public PositionGrille getPosition() {
        return new PositionGrille(x, y);
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    /** Renvoie le type de l'unité(TRAVAILLEUR ou ENNEMI) */
    public TypeUnite getTypeUnite() {
        return typeUnite;
    }

    // Todo: Si on clique sur le batiment, on fait rien
    public void seDeplacer(int newX, int newY) {
        Validation.requireArgument(newX >= 0 && newY >= 0
                                && newX < terrain.getTaille() && newY < terrain.getTaille(),
                                "Position de déplacement invalide: x=" + newX + ", y=" + newY);

        taches.add(new TacheDeplacement(newX, newY));
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!taches.isEmpty()) {
                    Tache tache = taches.getFirst();
                    if (tache instanceof TacheDeplacement) {
                        TacheDeplacement tacheDeplacement = (TacheDeplacement) tache;
                        int targetX = tacheDeplacement.getX();
                        int targetY = tacheDeplacement.getY();
                        if (targetX == x && targetY == y) {
                            taches.removeFirst(); // Tâche terminée
                            continue; // Passer à la prochaine tâche
                        }
                        // Simule le temps de déplacement
                        int newX = x + Integer.signum(targetX - x) * VITESSE_DEPLACEMENT;
                        int newY = y + Integer.signum(targetY - y) * VITESSE_DEPLACEMENT;
                        Thread.sleep(500); // TODO?

                        // Met à jour la position de l'unité
                        setPosition(newX, newY);
                    }
                    else if (tache instanceof TacheMiner) {
                        
                    }
                    else if (tache instanceof TacheDefendre) {

                    }
                    else if (tache instanceof TacheAttaquer) {

                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break; // Sortir de la boucle si le thread est interrompu
            }
        }
    }
}
