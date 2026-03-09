package tests;

import model.BatimentMaitre;
import model.Case;
import model.Direction;
import model.Foreuse;
import model.Route;
import model.Terrain;
import model.TypeCase;
import view.Fenetre;
import view.TimerView;

/** Classe de tests pour l'affichage */
public class TestView {

    public static void main(String[] args) {
        // on créé un terrain de test 
        Terrain terrain = new Terrain(10); 

        //et une fenêtre pour l'afficher
        Fenetre fenetre = new Fenetre("Test d'affichage", terrain);

        // on démarre le timer d'affichage pour mettre à jour l'affichage régulièrement, 
        // même si pour l'instant ça ne fait rien car le terrain reste le même
        new TimerView(fenetre);

        // on ajoute des éléments de test sur le terrain pour vérifier leur affichage

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

        // les minerais sont déjà placés aléatoirement dans le constructeur du terrain, 
        // on n'a pas besoin d'en ajouter manuellement

        // TODO : ajouter les tests pour les autres types de bâtiments une fois qu'ils seront implémentés
    }

}
