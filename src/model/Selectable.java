package model;

/** Interface pour les éléments sélectionnables (Unité et Case) */
public interface Selectable {
    /** Affiche les informations de l'élément sélectionné dans le menu latéral */
    String getDisplayName();
    String getDescription();
}
