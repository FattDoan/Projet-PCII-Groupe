package model;

/**
 * Représente la grille de jeu carrée.
 * La grille contient toutes les cases du jeu et gère leur placement.
 * Les bâtiments et minerais sont positionnés sur cette grille.
 */
public class Terrain {
   /** Taille de la grille (nombre de cases par côté) */
   private final int taille;
   
   /** Tableau 2D contenant toutes les cases de la grille */
   private final Case[][] grille;

   /** Liste des unités présentes sur le terrain */
   // TODO : private List<Unite> unites;

   /**
    * Crée une nouvelle grille carrée de la taille spécifiée.
    * Toutes les cases sont initialisées comme cases vides.
    * Note : La taille doit être strictement positive.
    * 
    * @param taille la dimension de la grille (taille x taille), doit être > 0
    * @throws AssertionError si taille <= 0
    */
   public Terrain(int taille) {
      // Validation : la taille doit être strictement positive
      assert taille > 0 : "taille=" + taille;
      
      this.taille = taille;
      this.grille = new Case[taille][taille];

      // Initialisation de toutes les cases de la grille
      // IMPORTANT : grille[i][j] a pour coordonnées (i, j)
      for(int i = 0; i < taille; ++i) {
         for(int j = 0; j < taille; ++j) {
            this.grille[i][j] = new Case(i, j, TypeCase.VIDE); // a faire : initialiser les cases avec des minerais selon une configuration prédéfinie ou aléatoire
         }
      }
   }



   /***** GETTER *****/

   /**
    * Retourne la taille de la grille (nombre de cases par côté).
    * @return la dimension de la grille carrée
    */
   public int getTaille() {
      return taille;
   }

    /**
     * Retourne la case située aux coordonnées spécifiées.
     * Précondition : Les coordonnées doivent être valides et dans les limites de la grille.
     * @return la case à la position (x, y)
     */

    public Case getCase(int x, int y) {
        // Validation : les coordonnées doivent être dans les limites de la grille
        assert x >= 0 && y >= 0 : "Coordonnées négatives: x=" + x + ", y=" + y;
        assert x < taille && y < taille : "Coordonnées hors limites: x=" + x + ", y=" + y + ", taille=" + taille;
        return grille[x][y];
    }


   /***** SETTER *****/

   /** Modifie la case située à la position (x, y) dans la grille 
    * /!\ uniquement utilisée pour les tests, à supprimer ou rendre privée dans la version finale du projet /!\
   */
   public void setCase(int x, int y, Case c) {
      this.grille[x][y] = c;
   }
}
