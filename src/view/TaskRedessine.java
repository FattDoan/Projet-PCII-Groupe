package view;

import model.Terrain;
import model.BatimentMaitre;
import javax.swing.SwingUtilities;

/** Une TimerTask qui redessine la fenêtre principale */
public class TaskRedessine extends java.util.TimerTask {
    public static final int DELAI = 16; // délai en millisecondes entre chaque redessin
    
    private Affichage aff;
    private Fenetre fenetre;
    private boolean gameOverDisplayed = false;
    
    public TaskRedessine(Fenetre fenetre) {
        this.aff = fenetre.getAffichage();
        this.fenetre = fenetre;
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> {
            // Vérifier si le jeu est terminé (bâtiment maître détruit)
            Terrain terrain = fenetre.getTerrain();
            BatimentMaitre batimentMaitre = terrain.getBatimentMaitre();
            
            if (batimentMaitre != null && batimentMaitre.isDestroyed() && !gameOverDisplayed) {
                gameOverDisplayed = true;
                afficherGameOver();
            }
            
            // Toujours rafraîchir l'affichage, même après Game Over
            // pour que l'écran Game Over soit visible
            aff.repaint();
            aff.refreshMenuIfSelected();
            
            // Si Game Over, on rafraîchit aussi la fenêtre principale
            if (fenetre.isGameOver()) {
                fenetre.repaint();
            }
        });
    }
    
    /**
     * Affiche un écran de Game Over.
     */
    private void afficherGameOver() {
        SwingUtilities.invokeLater(() -> {
            fenetre.afficherEcranGameOver();
        });
    }

}
