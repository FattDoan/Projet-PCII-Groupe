package view;

import model.unite.Unite;

/**
 * Contrat entre le menu et le contrôleur pour les ordres d'une unité.
 * Chaque nouvelle commande future (miner, défendre, attaquer) = une méthode ici.
 * MenuPanel n'importe rien du package controller grâce à cette interface.
 */
public interface UnitActionCallback{
    void onDeplacer(Unite u);
    void onMiner(Unite u); 
    void onConstruire(Unite u);  
    void onDefendre(Unite u);
}
