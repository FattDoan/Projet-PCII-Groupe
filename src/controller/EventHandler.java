package controller;

import model.*;
import view.Affichage;

/**
 * Cette classe gère les événements de clics sur la grille et dans le menu.
 * Elle doit être appelée par la classe ReactionClic pour traiter les clics de souris.
 */
public class EventHandler {
    private Affichage affichage;
    public EventHandler(Affichage affichage) {
        this.affichage = affichage;
    }
    
    // Méthode utilitaire pour convertir les énumérations en chaînes lisibles
    public static String toString(Enum<?> enumVal) {
        return String.valueOf(enumVal).replace("_", " ");
    }


    // Méthode pour gérer les clics sur la grille de jeu
    public void handleClicSurCase(Case c) { 
        // TODO: A implementer les comportements spécifiques selon le type de la case
        // par ex: affiche le menu a droite avec les options disponibles pour cette case, 
        // ou les informations sur le contenu de la case
        if (c.aBatiment()) {
            System.out.println("[EventHandler] La case contient un bâtiment: " + toString(c.getBatiment().type()));         
        }
        else if (c.aMinerai()) {
            System.out.println("[EventHandler] La case contient du minerai");
        }
        else if (c.estVide()) {
            System.out.println("[EventHandler] La case est vide");
        }

        // En ce moment, on se contente d'afficher les informations sur la case 
        // dans le menu à droite, 
        // mais on pourrait aussi ajouter des options d'interaction 
        // (par ex: construire une route, extraire du minerai, etc.)
        affichage.setSelectedCase(c);
    }

    /**
     * x et y sont les coordonnées du clic relatif à la fenêtre de menu
     * et non pas à la grille ou à l'écran global.
     *
     */
    public void handleClicDansMenu(int menuX, int menuY) {
        System.out.println("[EventHandler] Clic dans le menu: menuX=" + menuX + ", menuY=" + menuY);
    }
}
