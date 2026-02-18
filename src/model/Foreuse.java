package model;

/**
 * Représente une foreuse qui extrait automatiquement les minerais.
 * Une foreuse doit être placée sur une case contenant un filon de minerai.
 * Elle extrait le minerai et le stocke temporairement avant de l'envoyer vers une route ou un stockage.
 * La capacité de stockage temporaire est fixée à 1 minerai.
 */
public class Foreuse extends Batiment implements Runnable {
    private int DELAI_EXTRACTION = 1000; // Délai d'extraction en millisecondes (1 seconde)
    private volatile boolean running = true; // Indique si le thread doit continuer à fonctionner


    /**
     * Crée une nouvelle foreuse.
     * La capacité de stockage est fixée à 1 (un minerai extrait en attente).
     */
    public Foreuse() {
        super(1);
    }

    @Override
    public TypeBatiment type() {
        return TypeBatiment.FOREUSE;
    }

    
    @Override
    public void run() {
        while (running) {
            try {
                // Simule le temps d'extraction du minerai
                Thread.sleep(DELAI_EXTRACTION);
                
                // Extraction du minerai
                if (!estPlein()) {
                    this.ajouterMinerai();
                } else {
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
