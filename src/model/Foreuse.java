package model;

/**
 * Représente une foreuse qui extrait automatiquement les minerais.
 * Une foreuse doit être placée sur une case contenant un filon de minerai.
 * Elle extrait le minerai et le stocke temporairement avant de l'envoyer vers une route ou un stockage.
 * La capacité de stockage temporaire est fixée à 1 minerai.
 */
public class Foreuse extends Batiment { 
    /**
     * Crée une nouvelle foreuse.
     * La capacité de stockage est fixée à 1 (un minerai extrait en attente).
     */
    public Foreuse() {
        super(1);
    }
}
