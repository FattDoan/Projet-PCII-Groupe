package model;

/** Interface pour les éléments sélectionnables (Unité et Case) */
public interface Selectable {
    /** Affiche les informations de l'élément sélectionné dans le menu latéral */
    String getDisplayName();
    String getDescription();
   

    float getPX(); // Position en pixels
    float getPY();
    
    boolean isDestroyed(); // Indique si l'élément est détruit (pour les unités)
                           // ou pour les cases ayant un bâtiment détruit

    void receiveDamage(int damage); // Permet de recevoir des dégâts (pour les unités et les bâtiments)
}
