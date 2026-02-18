package tests;

import model.*;
import view.*;
import controller.*;

/**
 * Classe de tests pour ReactionClic et EventHandler.
 * 
 * Pour tester le contrôleur, nous réutilisons la fenêtre de test de la classe TestView
 * fournie par notre camarade (Emilie) en charge de l'affichage. 
 * 
 * Pour l'instant, les tests actuels se limitent à vérifier que les clics sont bien détectés :
 *  - Clics sur la grille : affiche les coordonnées de la case et le type de bâtiment présent.
 *  - Clics sur le menu latéral (à droite) : affiche les coordonnées du clic dans le menu.
 * 
 */
public class ReactionClicTest {

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

        // minerais
        Case caseMinerai1 = new Case(5, 1, TypeCase.MINERAI);
        terrain.setCase(5, 1, caseMinerai1); 

       
        //---------------------------------------------------------------------------------------------//
        // Ajout du contrôleur de clic pour tester les interactions avec les bâtiments
        // pour l'instant, il n'y a pas d'interactions définies, juste les affichages du console
        ReactionClic reactionClic = new ReactionClic(fenetre.getAffichage(), 
                                                     terrain, 
                                                     new EventHandler(fenetre.getAffichage(), terrain));
         //---------------------------------------------------------------------------------------------// 
    }
}
