package main;

import controller.*;
import model.*;
import view.Fenetre;
import view.TimerView;

/** Classe principale pour lancer le projet */
public class Main {
    public static void main(String[] args) {
        // on créé un terrain de test 
        Terrain terrain = new Terrain(10); 

        //et une fenêtre pour l'afficher
        Fenetre fenetre = new Fenetre("Test d'affichage", terrain);

        // on démarre le timer d'affichage pour mettre à jour l'affichage régulièrement, 
        // même si pour l'instant ça ne fait rien car le terrain reste le même
        new TimerView(fenetre);

        // On créé une foreuse sur un minerai, puis une route vers le bâtiment maître pour tester les affichages de ces éléments sur la grille

        int x = 1, y = 2; // coordonnées de la foreuse à placer, à ajuster selon les besoins du test
        if (!terrain.getCase(x, y).aMinerai()) {
            // on place un minerai à la position (x, y) si ce n'est pas déjà le cas, pour pouvoir y placer la foreuse
            terrain.setCase(x, y, new Case(x, y, TypeCase.MINERAI));
        }
        Foreuse foreuse = new Foreuse();
        terrain.getCase(x, y).setBatiment(foreuse);

        // route vers le bâtiment maître
        int positionMaitre = terrain.getTaille() / 2; // position du bâtiment maître au centre de la grille
        for (int i = x+1; i < positionMaitre; i++) {
            Route route = new Route(Direction.EST);
            terrain.getCase(i, y).setBatiment(route);
        }
        for (int j = y; j < positionMaitre; j++) {
            Route route = new Route(Direction.SUD);
            terrain.getCase(positionMaitre, j).setBatiment(route);
        }

        // on ajoute des éléments de test sur le terrain pour vérifier leur affichage

        // batiment maître avec 0, 10 et 100 minerais
        // TODO : à remplacer par des bâtiments basiques de stockage, le bâtiment maître ne doit pas être créé manuellement
        // BatimentMaitre batimentMaitre1 = new BatimentMaitre();
        // BatimentMaitre batimentMaitre2 = new BatimentMaitre();
        // BatimentMaitre batimentMaitre3 = new BatimentMaitre();
        // batimentMaitre2.ajouterMinerai(10);
        // batimentMaitre3.ajouterMinerai(100);
        // terrain.getCase(1, 1).setBatiment(batimentMaitre1);
        // terrain.getCase(2, 1).setBatiment(batimentMaitre2);
        // terrain.getCase(3, 1).setBatiment(batimentMaitre3);

        initController(fenetre, terrain);
    }

    // TODO: maybe a wrapper class for controllers class?
    private static ReactionClic initController(Fenetre fenetre, Terrain terrain) {
        ReactionClic reactionClic = new ReactionClic(fenetre.getAffichage(), 
                                                     terrain, 
                                                     new EventHandler(fenetre.getAffichage(), terrain));

        return reactionClic;
    }
}
