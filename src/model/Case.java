package model;

/**
 * Représente une case de la grille de jeu.
 * Chaque case possède des coordonnées (x, y) et un type (vide, minerai).
 */
public class Case {
   /** Coordonnée horizontale de la case dans la grille */
   private final int x;
   
   /** Coordonnée verticale de la case dans la grille */
   private final int y;
   
   /** Type de la case (VIDE, MINERAI) */
   private TypeCase type;

   /** Bâtiment présent sur la case */
   private Batiment batiment;
   
   /**
    * Crée une nouvelle case vide aux coordonnées spécifiées.
    * Par défaut, la case est de type VIDE.
    * Note : Les coordonnées doivent être positives ou nulles.
    * 
    * @param x la coordonnée horizontale (colonne), doit être >= 0
    * @param y la coordonnée verticale (ligne), doit être >= 0
    */
   public Case(int x, int y) {
      // Validation : les coordonnées ne peuvent pas être négatives
      assert x >= 0 && y >= 0 : "x=" + x + ", y=" + y;
      
      this.x = x;
      this.y = y;
      this.type = TypeCase.VIDE;
      this.batiment = null;
   }
}
