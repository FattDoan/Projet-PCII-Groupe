package model.unite;

import view.Affichage;
import model.*;
import model.unite.commande.*;
import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;
import view.Camera;

public class Unite implements Selectable, Runnable {

    private static final int DELAI_THREAD_MS = 16;
    // Coordonees continues (pixels)
    private float px, py;
    private float speed; // pixel par second
    private int hp;
    private final TypeUnite typeUnite;
    private final Terrain terrain;
    private int stockage = 0; // quantité de minerai actuellement stockée dans l'unité
    private static final int STOCKAGE_MAX = 5; // capacité maximale de stockage de l'unité
   
    // Drapeau d'arrêt coopératif du thread.
    private volatile boolean running = true;

    // L'etat de pathfinding 
    private float destinationPX, destinationPY; // en pixels, pour éviter de recalculer à chaque tick

    // Command queue pour ce unite. 
    // Les commandes sont exécutées séquentiellement, une par tick, jusqu'à ce que la queue soit vide.
    private final Deque<Commande> commandQueue = new ArrayDeque<>();

    protected Unite(float px, float py, float speed, int hp, TypeUnite type, Terrain terrain) {
        this.px = px;
        this.py = py;
        this.speed = speed;
        this.hp = hp;
        this.typeUnite = type;
        this.terrain = terrain;
    }

    /** A appele tick par tick terrain.updateUnites(dt) sur l'EDT */
    public void update(double dt) {
        if (!commandQueue.isEmpty()) {
            boolean done = commandQueue.peek().executer(this, dt);
            if (done) commandQueue.poll();
        }
    }


    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // we need to pass to update acutal time elapsed in milliseconds, not a fixed value, 
                // to avoid issues if the thread is paused or delayed for some reason (e.g. GC pause)  
                Thread.sleep(DELAI_THREAD_MS);
                update(DELAI_THREAD_MS);
            } catch (InterruptedException e) {
                // Respect du contrat d'interruption: on remet le flag puis on sort.
                Thread.currentThread().interrupt();
                break;
            }
        } 
    }

    public void ajouterCommande(Commande c) {
        commandQueue.addLast(c); 
    }
    public void annulerCommandes() { 
        commandQueue.clear(); 
    }

    /** Vérifie si le stockage est plein */
    public boolean StockagePlein() {
        return stockage >= STOCKAGE_MAX;
    }
    public boolean StockageVide() {
        return stockage <= 0;
    }

    /** Ajoute du stockage */
    public void addStockage() {
        if (!StockagePlein()) {
            stockage++;
        }
    }
    public void retirerStockage() {
        if (stockage > 0) {
            stockage--;
        }
    }

    // Les getters
    // GX et GY sont les coordonnées de grille (case) calculées à partir des coordonnées pixels.
    public int getGX() { return (int)(px / Case.TAILLE); }
    public int getGY() { return (int)(py / Case.TAILLE); }
    public float getPX() { return px; }
    public float getPY() { return py; }
    public TypeUnite getType() { return typeUnite; }
    public Terrain getTerrain() { return terrain; }
    public float getSpeed() { return speed; }
    public float getDestinationPX() { return destinationPX; }
    public float getDestinationPY() { return destinationPY; }
    public int getStockage() { return stockage; }

    // Appellee par CommandeDeplacement pour avancer vers la prochaine position en pixel
    public void avancer(float dx, float dy) { 
        px += dx; 
        py += dy; 
    }

    public void recevoirDegats(int degats) {
        hp -= degats;
        //TODO quand on ajoute ennemi
        //if (hp <= 0) mourir();
    }

    @Override
    public String getDisplayName() {
        switch (typeUnite) {
            case OUVRIER -> { return "OUVRIER"; }
            case ENNEMI  -> { return "ENNEMI";  }
        }
        return "UNITE";
    }
    @Override
    public String getDescription() {
        switch (typeUnite) {
            case OUVRIER -> { 
                return "Unités de base, capables de construire des bâtiments et de récolter du minerai."; 
            }
            case ENNEMI -> { 
                return "Unités de combat, capables d'attaquer les bâtiments et les unités ennemies."; 
            }
            default -> {
                return "";
            }
        }
    }
}
