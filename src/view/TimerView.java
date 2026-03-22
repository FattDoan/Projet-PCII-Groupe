package view;
import java.util.Objects;
import java.util.Timer;

/** Un Timer qui s'occupe de tous les threads relatifs à l'affichage */
public class TimerView extends Timer {

    private final Fenetre fenetre;

    /** Démarre automatiquement le timer d'affichage dès sa création, aucune étape supplémentaire n'est nécessaire */
    public TimerView(Fenetre f) {
        super("timer-view", true);

        // on initialise les champs de la classe
        this.fenetre = Objects.requireNonNull(f, "fenetre=null");

        // Démarrage du timer pour les threads d'affichage
        this.scheduleAtFixedRate(new TaskRedessine(fenetre), 0, TaskRedessine.DELAI);
    }
}
