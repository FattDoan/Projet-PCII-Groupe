package view;

import model.Terrain;
import javax.swing.SwingUtilities;

/** Une TimerTask qui redessine la fenêtre principale */
public class TaskRedessine extends java.util.TimerTask {
    public static final int DELAI = 16; // délai en millisecondes entre chaque redessin
    
    private Affichage aff;
    private Terrain terrain;
    public TaskRedessine(Fenetre fenetre) {
        this.aff = fenetre.getAffichage();
        this.terrain = fenetre.getTerrain();
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> {
            terrain.updateUnites(DELAI);
            aff.repaint();
            aff.refreshMenuIfSelected();
        });
    }

}
