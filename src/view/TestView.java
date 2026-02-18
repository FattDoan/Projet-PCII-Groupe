package view;

import model.BatimentMaitre;
import model.Case;
import model.Direction;
import model.Foreuse;
import model.Route;
import model.Terrain;
import model.TypeCase;

/** Classe de tests pour l'affichage */
public class TestView {

    public static void main(String[] args) {
        // on créé un terrain de test 
        Terrain terrain = new Terrain(10); // TODO : remplacer les dimensions par celles souhaitées pour le test

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
        for (int i=0; i<10; ++i) {
            batimentMaitre2.ajouterMinerai();
        }
        for (int i=0; i<100; ++i) {
            batimentMaitre3.ajouterMinerai();
        }
        terrain.getCase(1, 1).setBatiment(batimentMaitre1);
        terrain.getCase(2, 1).setBatiment(batimentMaitre2);
        terrain.getCase(3, 1).setBatiment(batimentMaitre3);

        // une foreuse avec 0 et 1 minerai
        Foreuse foreuse1 = new Foreuse();
        Foreuse foreuse2 = new Foreuse();
        foreuse2.ajouterMinerai();
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
        terrain.setCase(5, 1, caseMinerai1); // on utilise le setter pour remplacer la case vide par une case contenant un minerai car on ne peut pas modifier le type d'une case déjà créée (le type est final), on doit donc créer une nouvelle case et la placer à la place de l'ancienne
        // c'est une solution temporaire pour les tests, en attendant que les minerais soient générés aléatoirement lors de la création du terrain

        // TODO : ajouter les tests pour les autres types de bâtiments une fois qu'ils seront implémentés
    }

}
