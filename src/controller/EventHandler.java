package controller;

import model.Case;

public class EventHandler {
    


    public EventHandler() {
    }
    
    // Méthode utilitaire pour convertir les énumérations en chaînes lisibles
    public static String toString(Enum<?> enumVal) {
        return String.valueOf(enumVal).replace("_", " ");
    }



    public void handleClicSurCase(Case c) {
        System.out.println("[EventHandler] Clic sur la case: " + c.getX() + ", " + c.getY());
    
        // TODO: A implementer les comportements spécifiques selon le type de la case
        // par ex: affiche le menu a droite avec les options disponibles pour cette case, 
        // ou les informations sur le contenu de la case
        if (c.aBatiment()) {
            System.out.println("  - La case contient un bâtiment: " + toString(c.getBatiment().type()));
        }
        else if (c.aMinerai()) {
            System.out.println("  - La case contient du minerai");
        }
        else if (c.estVide()) {
            System.out.println("  - La case est vide");
        }
    }

    /**
     * x et y sont les coordonnées du clic relatif à la fenêtre de menu
     * et non pas à la grille ou à l'écran global.
     *
     */
    public void handleClicDansMenu(int x, int y) {
        System.out.println("[EventHandler] Clic dans le menu: x=" + x + ", y=" + y);
    }
}
