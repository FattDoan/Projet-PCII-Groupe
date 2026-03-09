package main;

import model.*;
import view.*;
import controller.*;

public class Main {

    private static Terrain initModel() {
        Terrain terrain = new Terrain(10); 

        // TODO: replace these manual placements with automatic generation of terrain

        // batiment maître avec 0, 10 et 100 minerais
        BatimentMaitre batimentMaitre1 = new BatimentMaitre();
        BatimentMaitre batimentMaitre2 = new BatimentMaitre();
        BatimentMaitre batimentMaitre3 = new BatimentMaitre();
        batimentMaitre2.ajouterMinerai(10);
        batimentMaitre3.ajouterMinerai(100);
        terrain.getCase(1, 1).setBatiment(batimentMaitre1);
        terrain.getCase(2, 1).setBatiment(batimentMaitre2);
        terrain.getCase(3, 1).setBatiment(batimentMaitre3);

        // une foreuse avec 0 et 1 minerai
        Foreuse foreuse1 = new Foreuse();
        Foreuse foreuse2 = new Foreuse();
        foreuse2.ajouterMinerai(1);
        terrain.getCase(1, 3).setBatiment(foreuse1);
        terrain.getCase(2, 3).setBatiment(foreuse2);

        // routes qui vont dans 4 directions différentes
        Route routeHaut = new Route(Direction.NORD);
        Route routeBas = new Route(Direction.SUD);
        Route routeGauche = new Route(Direction.OUEST);
        Route routeDroite = new Route(Direction.EST);
        terrain.getCase(5, 4).setBatiment(routeHaut);
        terrain.getCase(5, 6).setBatiment(routeBas);
        terrain.getCase(4, 5).setBatiment(routeGauche);
        terrain.getCase(6, 5).setBatiment(routeDroite);

        // minerais
        Case caseMinerai1 = new Case(5, 1, TypeCase.MINERAI);
        terrain.setCase(5, 1, caseMinerai1); 

        return terrain;
    }
    
    private static Fenetre initView(Terrain terrain) {
        Fenetre fenetre = new Fenetre("Game", terrain);

        // on démarre le timer d'affichage pour mettre à jour l'affichage régulièrement, 
        // même si pour l'instant ça ne fait rien car le terrain reste le même
        new TimerView(fenetre);
  
        /* on assemble la fenêtre et on l'affiche */
        fenetre.pack();
        fenetre.setVisible(true);

        /* on s'assure que le JPanel de dessin peut recevoir le focus pour les événements clavier 
         * TODO: controller peut eventuellement avoir besoin des evements clavier 
         * */
        fenetre.getAffichage().setFocusable(true);
        fenetre.getAffichage().requestFocusInWindow();

        return fenetre;
    }

    // TODO: maybe a wrapper class for controllers class?
    private static ReactionClic initController(Fenetre fenetre, Terrain terrain) {
        ReactionClic reactionClic = new ReactionClic(fenetre.getAffichage(), 
                                                     terrain, 
                                                     new EventHandler(fenetre.getAffichage(), terrain));

        return reactionClic;
    }

    public static void main(String[] args) {
        Terrain terrain = initModel();
        Fenetre fenetre = initView(terrain);
        ReactionClic reactionClic = initController(fenetre, terrain);
    }
}
