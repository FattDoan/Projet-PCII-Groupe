package controller;

import model.*;
import view.Affichage;

public class EventHandler {
    private Terrain terrain;
    private Affichage affichage;
    public EventHandler(Affichage affichage, Terrain terrain) {
        this.affichage = affichage;
        this.terrain = terrain;
    }
    
    // Méthode utilitaire pour convertir les énumérations en chaînes lisibles
    public static String toString(Enum<?> enumVal) {
        return String.valueOf(enumVal).replace("_", " ");
    }



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
