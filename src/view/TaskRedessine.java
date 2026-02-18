package view;

import javax.swing.SwingUtilities;

/** Une TimerTask qui redessine la fenêtre principale */
public class TaskRedessine extends java.util.TimerTask {

    public static final int DELAI = 50; // délai en millisecondes entre chaque redessin
    
    private Fenetre fenetre;

    public TaskRedessine(Fenetre fenetre) {
        this.fenetre = fenetre;
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> {
            this.fenetre.repaint();
        });
    }

}