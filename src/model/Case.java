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
   
   /**
    * Retourne le bâtiment présent sur la case. 
    * @return le bâtiment présent sur cette case, ou null si aucun bâtiment
    */
   public Batiment getBatiment() {
      return batiment;
   }

   /**
    * Retourne les coordonnées horizontales (x) de la case.
    * @return la coordonnée x de la case
    */
   public int getX() {
      return x;
    }
   
   /**
    * Retourne les coordonnées verticales (y) de la case.
    * @return la coordonnée y de la case
    */
    public int getY() {
      return y;
    }

    /**
     * Predicat qui vérifie si la case contient un bâtiment. 
     * @return true si la case contient un bâtiment, false sinon
     */
    public boolean aBatiment() {
        return batiment != null;
    }
    
    /**
     * Predicat qui vérifie si la case est vide (ne contient ni bâtiment ni minerai).
     * @return true si la case est vide, false sinon
     */
    public boolean estVide() {
        return type == TypeCase.VIDE;
    }
    
    /**
     * Predicat qui vérifie si la case contient un minerai.
     * @return true si la case contient un minerai, false sinon
     */
    public boolean aMinerai() {
        return type == TypeCase.MINERAI;
    } 


}
