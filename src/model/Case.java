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
   private final TypeCase type;

   /** Bâtiment présent sur la case */
   private Batiment batiment;
   /**
    * Crée une nouvelle case vide aux coordonnées spécifiées.
    * Par défaut, la case est de type VIDE.
    * Note : Les coordonnées doivent être positives ou nulles.
    * 
    * @param x la coordonnée horizontale (colonne), doit être >= 0
    * @param y la coordonnée verticale (ligne), doit être >= 0
    * @param type le type de case, ne doit pas être null
    * @throws AssertionError si x < 0 ou y < 0 ou type est null
    */
   public Case(int x, int y, TypeCase type) {
      // Validation : les coordonnées ne peuvent pas être négatives
      assert x >= 0 && y >= 0 : "x=" + x + ", y=" + y;
      // Validation : le type ne peut pas être nul
      assert type != null : "type=null";
      
      this.x = x;
      this.y = y;
      this.type = type;
      this.batiment = null;
   }


   /***** GETTER *****/

   /**
    * Retourne le bâtiment présent sur la case.
    * @return le bâtiment présent sur cette case, ou null si aucun bâtiment
    */
   public Batiment getBatiment() {
      return batiment;
   }

   /** Renvoie le type de la case (VIDE, MINERAI) */
   public TypeCase getType() {
      return type;
   }

   /** Renvoie la coordonnée x de la case */
   public int getX() {
      return x;
   }

   /** Renvoie la coordonnée y de la case */
   public int getY() {
      return y;
   }


   /** Renvoie vrai si la case est vide, faux sinon */
   public boolean estVide() {
      return this.type == TypeCase.VIDE && this.batiment == null;
   }

   /** Renvoie vrai si la case contient du minerai, faux sinon */
   public boolean aMinerai() {
      return this.type == TypeCase.MINERAI;
   }

   /** Renvoie vrai si la case contient un bâtiment, faux sinon */
   public boolean aBatiment() {
      return this.batiment != null;
   }



   /***** SETTER *****/

   /**
    * Place un bâtiment sur la case.
    * Note : Un bâtiment ne peut être placé que sur une case vide.
    * 
    * @param batiment le bâtiment à placer, ne doit pas être null
    * @throws AssertionError si la case n'est pas vide ou si le bâtiment est null
    */
   public void setBatiment(Batiment batiment) {
      // Validation : la case doit être vide pour placer un bâtiment
      assert this.estVide() : "La case (" + x + ", " + y + ") n'est pas vide";
      // Validation : le bâtiment ne peut pas être nul
      assert batiment != null : "batiment=null";
      this.batiment = batiment;
   }

}
