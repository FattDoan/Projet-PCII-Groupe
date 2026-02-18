# Conception de la fenêtre principale de l'application

Le but est d'avoir une fenêtre qui représente visuellement une grille de jeu, avec un affichage rudimentaire des éléments du terrain (minerais et différents bâtiments). Le menu et l'affichage des unités seront implémenté plus tard.


## Classe `Fenetre`

La classe `Fenetre` est la classe principale de l'affichage. Elle hérite de `JFrame` et contient un constructeur qui prend en paramètre un titre et un terrain à afficher. Le constructeur initialise la fenêtre, ajoute les composants graphiques nécessaires (comme l'affichage du terrain) et rend la fenêtre visible.


## Classe `Affichage`

La classe `Affichage` est responsable de l'affichage du terrain de jeu. Elle hérite de `JPanel` et contient une référence au terrain à afficher. La méthode `paintComponent(Graphics g)` est surchargée pour dessiner les éléments du terrain (minerais, bâtiments, etc.) en fonction de leur position et de leur type. Les fonctions pour affiches les différents éléments du terrain se trouvent dans les classes `AffichageMinerais` et `AffichageBâtiments`, qui sont appelées depuis `Affichage` pour dessiner les éléments spécifiques. Cela permet ainsi de les modifier facilement sans affecter l'affichage général du terrain.


## Classe `AffichageCases`

Cette classe contient des méthodes statiques pour afficher les différents types de cases (vide, minerai, bâtiment) à une position donnée sur la fenêtre. Chaque méthode prend en paramètre un objet `Graphics` et une `Case` pour dessiner l'élément correspondant à la position de la case sur la grille. Les coordonnées de la case sont converties en pixels pour l'affichage, et différentes couleurs sont utilisées pour différencier les types de cases (par exemple, vert pour les cases vides et orange pour les minerais).

Chaque case a sa propre méthode d'affichage, ce qui permet de facilement ajouter de nouveaux types de cases et facilement modifier l'apparence de chaque type de case sans affecter les autres. Pour l'instant chaque case est seulement un carré de couleur avec un nombre représentant la quantité de minerai qu'elle contient.

`AffichageCases` contient les méthodes suivantes :
- `afficheCase(Graphics g, Case c)`: Affiche une case en fonction de son type (vide, minerai, bâtiment) en appelant la méthode d'affichage correspondante. C'est la méthode principale qui est appelée depuis `Affichage` pour dessiner chaque case du terrain.
- `afficheCaseVide(Graphics g, Case c)`: Affiche une case vide à la position de la case sur la fenêtre.
- `afficheMinerai(Graphics g, Case c)`: Affiche un minerai à la position de la case sur la fenêtre.


## Classe `AffichageBatiments`

Similaire à `AffichageCases`, cette classe contiendra des méthodes pour afficher les différents types de bâtiments (route, foreuse, etc.) à une position donnée sur la fenêtre. Chaque méthode prendra en paramètre un objet `Graphics` et une `Case` pour dessiner le bâtiment correspondant à la position de la case sur la grille. 

Les méthodes de `AffichageBatiments` sont appelées depuis `AffichageCases` en fonction du contenu de la case à afficher. Par exemple, si une case contient une route, la méthode `afficheRoute(Graphics g, Case c)` sera appelée pour dessiner la route à la position de la case sur la fenêtre.


## Classe `TestView`

Cette classe est utilisée pour tester l'affichage du terrain de jeu. Elle crée un terrain de test avec différentes cases (vides, minerais, bâtiments) et vérifie que l'affichage fonctionne correctement en affichant le terrain dans une fenêtre. C'est une classe de test qui permet de s'assurer que les différentes méthodes d'affichage fonctionnent comme prévu avant de les intégrer dans l'application finale.
Actuellement, elle affiche un terrain de 10x10 cases avec 3 batiments maitres, 1 minerai, 2 foreuses et 4 routes pointant chacune dans une direction différente.
Pour l'instant, les batiments de stockage et les unités ne sont pas implémentées, donc pas encore affichées dans les tests. 