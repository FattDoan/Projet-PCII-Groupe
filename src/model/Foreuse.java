package model;

import common.AsyncExecutor;

/**
 * Représente une foreuse qui extrait automatiquement les minerais.
 * Une foreuse doit être placée sur une case contenant un filon de minerai.
 * Elle extrait le minerai et le stocke temporairement avant de l'envoyer vers une route ou un stockage.
 * La capacité de stockage temporaire est fixée à 1 minerai.
 */
public class Foreuse extends Batiment implements Runnable {
    public static final int DELAI_EXTRACTION_MS = 2000; // 2 seconde
    private volatile boolean running = true; // Indique si le thread doit continuer à fonctionner


    /**
     * Crée une nouvelle foreuse.
     * La capacité de stockage est fixée à 1 (un minerai extrait en attente).
     */
    public Foreuse(int x, int y, Terrain terrain) {
        super(1, x, y, terrain);
    }

    @Override
    public TypeBatiment type() {
        return TypeBatiment.FOREUSE;
    }

    
    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // Simule le temps d'extraction du minerai
                Thread.sleep(DELAI_EXTRACTION_MS);
                
                // Extraction du minerai
                if (!estPlein()) {
                    this.ajouterMinerai(1);
                    Minerai nouveauMinerai = new Minerai(getX(), getY(), getTerrain());
    
                    // Soumet le minerai extrait pour transport vers la route ou le stockage
                    // immédiatement
                    common.AsyncExecutor.schedule(nouveauMinerai, 0);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

    }

    /**
     * Méthode pour arrêter proprement le thread
     */
    public void arreter() {
        running = false;
    }
}
