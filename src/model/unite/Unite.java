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
    // Coordonnees continues (pixels)
    private float px, py;
    private float speed; // pixels par seconde
    private int hp;
    private final TypeUnite typeUnite;
    private final Terrain terrain;
    private int stockage = 0; // quantité de minerai actuellement stockée dans l'unité
    private static final int STOCKAGE_MAX = 5; // capacité maximale de stockage de l'unité
   
    // Drapeau d'arrêt coopératif du thread.
    private volatile boolean running = true;

    // Etat courant de deplacement
    private float destinationPX, destinationPY; // en pixels, pour éviter de recalculer à chaque tick

    // File de commandes pour l'unite.
    // Elles sont executees sequentiellement, une par tick, jusqu'a epuisement.
    private final Deque<Commande> commandQueue = new ArrayDeque<>();

    protected Unite(float px, float py, float speed, int hp, TypeUnite type, Terrain terrain) {
        this.px = px;
        this.py = py;
        this.speed = speed;
        this.hp = hp;
        this.typeUnite = type;
        this.terrain = terrain;
    }

    /** Appele a chaque tick par terrain.updateUnites(dt) sur l'EDT */
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
                // On passe un temps ecoule en millisecondes pour limiter les ecarts
                // si le thread est bloque ou retarde (ex : pause GC).
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

    // Only for CommandeDefendre
    public void addToFrontCommande(Commande c) {
        commandQueue.addFirst(c); 
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

    // Getters
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
    public int getHP() { return hp; }
    public int getHPMax() {
        switch (typeUnite) {
            case OUVRIER -> { return Ouvrier.HP_INITIAL; }
            case ENNEMI -> { return Ennemi.HP_INITIAL; }
            default -> { return 0; }
        }
    }
    public String getCurrentCommandName() {
        if (commandQueue.isEmpty()) return "Aucune";
        Commande c = commandQueue.peek();
        return c.getNom(); 
    }


    // Appellee par CommandeDeplacement pour avancer en pixels
    public void avancer(float dx, float dy) { 
        px += dx; 
        py += dy; 
    }

    public synchronized void receiveDamage(int degats) {
        hp -= degats;
        if (hp <= 0) {
            hp = 0; // Evite les valeurs negatives
            running = false; // Arrete le thread de l'unite
            terrain.removeUnite(this); // Retire l'unite du terrain
        }
    }

    public boolean isDestroyed() {
        return hp <= 0;
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
