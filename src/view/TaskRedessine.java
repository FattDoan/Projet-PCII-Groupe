package view;

import model.Terrain;
import javax.swing.SwingUtilities;

/** Une TimerTask qui redessine la fenêtre principale */
public class TaskRedessine extends java.util.TimerTask {
    public static final int DELAI = 16; // délai en millisecondes entre chaque redessin
    
    private Affichage aff;
    public TaskRedessine(Fenetre fenetre) {
        this.aff = fenetre.getAffichage();
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> {
            aff.repaint();
            aff.refreshMenuIfSelected();
        });
    }

}
