package model;

import java.util.HashSet;
import java.util.Set;

import common.Validation;

/**
 * Représente la grille de jeu carrée.
 * La grille contient toutes les cases du jeu et gère leur placement.
 * Les bâtiments et minerais sont positionnés sur cette grille.
 */
public class Terrain {

   // CONSTANTES

   // La taille n'est pas une constante, mais on définit le nombre de minerai en fonction de la taille
   // Donc on définit un ratio minerai/(taille*taille) pour ajuster le nombre de minerai en fonction de la taille du terrain
   public static final double RATIO_MINERAIS = 0.1; // 10% des cases contiendront des minerais, à ajuster selon les besoins du jeu


   // ATTRIBUTS

   /** Taille de la grille (nombre de cases par côté) */
   // Pas une constante car on veut pouvoir définir la taille du terrain en début de partie
   private final int taille;
   
   /** Tableau 2D contenant toutes les cases de la grille */
   private final Case[][] grille;

   /** Extension future: unités présentes sur le terrain. */


   /**
    * Crée une nouvelle grille carrée de la taille spécifiée.
    * Toutes les cases sont initialisées comme cases vides.
    * Note : La taille doit être strictement positive.
    * 
    * @param taille la dimension de la grille (taille x taille), doit être > 0
   * @throws IllegalArgumentException si la taille est invalide (en validation stricte)
    */
   public Terrain(int taille) {
      // Validation : la taille doit être strictement positive
      Validation.requireArgument(taille > 0, "taille=" + taille);

      // Initialisation des attributs
      this.taille = taille;
      this.grille = new Case[taille][taille];

      int centre = taille / 2;

      // Génération aléatoire des minerais sur la grille
      int nombreMinerais = (int)(taille * taille * RATIO_MINERAIS);
      // On génère la liste des positions de minerais aléatoirement.
      Set<PositionGrille> positionsMinerais = new HashSet<>();
      while (positionsMinerais.size() < nombreMinerais) {
         int x = (int)(Math.random() * taille);
         int y = (int)(Math.random() * taille);
         PositionGrille pos = new PositionGrille(x, y);
         // S'il n'y a ni minerai ni bâtiment maître à cette position, on l'ajoute à la liste.
         if (!positionsMinerais.contains(pos) && !(x == centre && y == centre)) {
            positionsMinerais.add(pos);
         }
      }

      // Initialisation de toutes les cases de la grille
      // IMPORTANT : grille[i][j] a pour coordonnées (i, j)
      for(int i = 0; i < taille; ++i) {
         for(int j = 0; j < taille; ++j) {
            PositionGrille pos = new PositionGrille(i, j);
            if (positionsMinerais.contains(pos)) {
               this.grille[i][j] = new Case(i, j, TypeCase.MINERAI); // Case contenant un minerai
            } else {
               this.grille[i][j] = new Case(i, j, TypeCase.VIDE); // Case vide
            }
         }
      }

      // On place le bâtiment maître au centre de la grille
      this.grille[centre][centre].setBatiment(new BatimentMaitre(centre, centre, this));

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
      validateCoordonnees(x, y);
        return grille[x][y];
    }

   /**
    * API explicite pour les scénarios de démonstration et de setup manuel.
    * Remplace le type de case sans toucher au bâtiment présent.
    */
   public void definirTypeCase(int x, int y, TypeCase type) {
      validateCoordonnees(x, y);
      Validation.requireArgument(type != null, "type=null");

      Case actuelle = grille[x][y];
      Case nouvelle = new Case(x, y, type);
      if (actuelle.getBatiment() != null) {
         nouvelle.setBatiment(actuelle.getBatiment());
      }
      grille[x][y] = nouvelle;
   }


   /***** SETTER *****/

   /**
    * @deprecated API de mutation brute conservée pour compatibilité.
    * Préférer {@link #definirTypeCase(int, int, TypeCase)}.
    */
   @Deprecated
   public void setCase(int x, int y, Case c) {
      Validation.requireArgument(c != null, "case=null");
      validateCoordonnees(x, y);
      Validation.requireArgument(
         c.getX() == x && c.getY() == y,
         "Case incoherente avec la position cible: case=(" + c.getX() + ", " + c.getY() + ") cible=(" + x + ", " + y + ")"
      );
      this.grille[x][y] = c;
   }

   private void validateCoordonnees(int x, int y) {
      Validation.requireArgument(x >= 0 && y >= 0, "Coordonnées négatives: x=" + x + ", y=" + y);
      Validation.requireArgument(x < taille && y < taille, "Coordonnées hors limites: x=" + x + ", y=" + y + ", taille=" + taille);
   }
}
