package model;

import model.unite.Ennemi;
import common.AsyncExecutor;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Gère les vagues d'ennemis qui attaquent le bâtiment maître.
 * Une vague est déclenchée toutes les 3 minutes, avec un nombre croissant d'ennemis.
 * 
 * - Vague 1 : 1 ennemi
 * - Vague 2 : 2 ennemis
 * - Vague 3 : 3 ennemis
 * - etc.
 */
public class GestionnaireVagues {
    
    /** Délai entre chaque vague (en millisecondes) */
    private static final long DELAI_ENTRE_VAGUES_MS = 3 * 60 * 1000; // 3 minutes
    
    private final Terrain terrain;
    private int numeroVague = 0;
    private Timer timer;
    
    /**
     * Crée un gestionnaire de vagues pour le terrain spécifié.
     * @param terrain le terrain sur lequel les vagues seront générées
     */
    public GestionnaireVagues(Terrain terrain) {
        this.terrain = terrain;
    }
    
    /**
     * Démarre le gestionnaire de vagues.
     * La première vague sera déclenchée après 3 minutes.
     */
    public void demarrer() {
        arreter(); // Arrêter un timer existant
        
        timer = new Timer("GestionnaireVagues", true);
        
        // Timer pour déclencher les vagues
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                declencherVague();
            }
        }, DELAI_ENTRE_VAGUES_MS, DELAI_ENTRE_VAGUES_MS);
        
        // Timer pour mettre à jour le temps restant (toutes les secondes)
        new Timer("GestionnaireVagues-TimerDisplay", true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (tempsRestantMs > 0) {
                    tempsRestantMs -= 1000;
                }
            }
        }, 1000, 1000);
    }
    
    /**
     * Arrête le gestionnaire de vagues.
     */
    public void arreter() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
    
    /** Temps restant avant la prochaine vague (en secondes) */
    private long tempsRestantMs = DELAI_ENTRE_VAGUES_MS;
    
    /**
     * Déclenche une nouvelle vague d'ennemis.
     * Le nombre d'ennemis est égal au numéro de la vague (vague 1 = 1 ennemi, vague 2 = 2 ennemis, etc.)
     */
    private void declencherVague() {
        numeroVague++;
        int nombreEnnemis = numeroVague;
        
        // Réinitialiser le temps restant
        tempsRestantMs = DELAI_ENTRE_VAGUES_MS;
        
        System.out.println("[VAUGE " + numeroVague + "] Déclenchement avec " + nombreEnnemis + " ennemi(s)");
        
        // Créer et positionner les ennemis sur les bords de la carte
        for (int i = 0; i < nombreEnnemis; i++) {
            PositionGrille position = genererPositionBord();
            Ennemi ennemi = new Ennemi(position.x(), position.y(), terrain);
            terrain.addUnite(ennemi);
            System.out.println("  - Ennemi créé en (" + position.x() + ", " + position.y() + ")");
        }
    }
    
    /**
     * Déclenche manuellement la prochaine vague d'ennemis.
     * Réinitialise le timer pour la prochaine vague.
     */
    public void declencherProchaineVague() {
        if (timer == null) return;
        
        // Annuler le timer actuel
        timer.cancel();
        timer.purge();
        
        // Déclencher une vague immédiatement
        declencherVague();
        
        // Relancer le timer
        demarrer();
    }
    
    /**
     * Obtient le temps restant avant la prochaine vague (en secondes).
     * @return le temps restant en secondes
     */
    public int getTempsRestantSecondes() {
        return (int) (tempsRestantMs / 1000);
    }
    
    /**
     * Obtient le numéro de la prochaine vague.
     * @return le numéro de la prochaine vague
     */
    public int getProchaineVague() {
        return numeroVague + 1;
    }
    
    /**
     * Met à jour le temps restant avant la prochaine vague.
     * Appelé par le timer à chaque tick.
     */
    public void majTempsRestant() {
        // Mettre à jour le temps restant (approximation)
        // Cette méthode pourrait être appelée plus fréquemment pour une meilleure précision
    }
    
    /**
     * Génère une position aléatoire sur les bords de la carte.
     * Les ennemis apparaissent en bordure et se déplacent vers le bâtiment maître.
     * @return une position sur le bord de la carte
     */
    private PositionGrille genererPositionBord() {
        int taille = terrain.getTaille();
        int x, y;
        
        // Choisir un bord aléatoirement (0 = haut, 1 = droite, 2 = bas, 3 = gauche)
        int bord = (int) (Math.random() * 4);
        
        switch (bord) {
            case 0: // Bord supérieur (y = 0)
                x = (int) (Math.random() * taille);
                y = 0;
                break;
            case 1: // Bord droit (x = taille - 1)
                x = taille - 1;
                y = (int) (Math.random() * taille);
                break;
            case 2: // Bord inférieur (y = taille - 1)
                x = (int) (Math.random() * taille);
                y = taille - 1;
                break;
            case 3: // Bord gauche (x = 0)
                x = 0;
                y = (int) (Math.random() * taille);
                break;
            default:
                x = 0;
                y = 0;
        }
        
        return new PositionGrille(x, y);
    }
    
    /**
     * @return le numéro de la vague actuelle
     */
    public int getNumeroVague() {
        return numeroVague;
    }
    
    /**
     * Réinitialise le gestionnaire de vagues (utile pour une nouvelle partie).
     */
    public void reinitialiser() {
        arreter();
        numeroVague = 0;
    }
}
