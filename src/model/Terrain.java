/**
 * Représente la grille de jeu carrée.
 * La grille contient toutes les cases du jeu et gère leur placement.
 * Les bâtiments et minerais sont positionnés sur cette grille.
 */
public class Terrain {
   /** Taille de la grille (nombre de cases par côté) */
   private int taille;
   
   /** Tableau 2D contenant toutes les cases de la grille */
   private Case[][] grille;

   /** Liste des unités présentes sur le terrain */
   // a faire : private List<Unite> unites;

   /**
    * Crée une nouvelle grille carrée de la taille spécifiée.
    * Toutes les cases sont initialisées comme cases vides.
    * Note : La taille doit être strictement positive.
    * 
    * @param taille la dimension de la grille (taille x taille), doit être > 0
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
            this.grille[i][j] = new Case(i, j);
         }
      }
   }
}
