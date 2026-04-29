package model;

import common.AsyncExecutor;
import common.Validation;

/**
 * Représente une case de la grille de jeu.
 * Chaque case possède des coordonnées (x, y) et un type (vide, minerai).
 */
public class Case implements Selectable {
   /** Coordonnée horizontale de la case dans la grille */
   private final int x;

   /** Coordonnée verticale de la case dans la grille */
   private final int y;
   
   /** Type de la case (VIDE, MINERAI) */
   private final TypeCase type;

   /** Bâtiment présent sur la case */
   private Batiment batiment;

    /** Taille d'une case en pixels */
    public static int TAILLE = 40;

   /**
    * Crée une nouvelle case vide aux coordonnées spécifiées.
    * Par défaut, la case est de type VIDE.
    * Note : Les coordonnées doivent être positives ou nulles.
    * 
    * @param x la coordonnée horizontale (colonne), doit être >= 0
    * @param y la coordonnée verticale (ligne), doit être >= 0
    * @param type le type de case, ne doit pas être null
   * @throws IllegalArgumentException si x < 0, y < 0 ou type est null (en validation stricte)
    */
   public Case(int x, int y, TypeCase type) {
      // Validation : les coordonnées ne peuvent pas être négatives
      Validation.requireArgument(x >= 0 && y >= 0, "x=" + x + ", y=" + y);
      // Validation : le type ne peut pas être nul
      Validation.requireArgument(type != null, "type=null");
      
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

    /** Renvoie la coordonnée x en pixels */
    public float getPX() {
        return x * Case.TAILLE + Case.TAILLE / 2; // Centre de la case
    }

    /** Renvoie la coordonnée y en pixels */
    public float getPY() {
        return y * Case.TAILLE + Case.TAILLE / 2; // Centre de la case
    }

    public boolean isDestroyed() {
        if (batiment != null) {
            return batiment.isDestroyed();
        }
        return false; // Une case sans bâtiment n'est pas détruite
    }

    public void receiveDamage(int damage) {
        if (batiment != null) {
            batiment.receiveDamage(damage);
            if (batiment.isDestroyed()) {
                detruireBatiment();
            }
        }
    }


   /** Renvoi vrai si il a pas de batiment */
   public boolean estAccessible() {
      return this.batiment == null;
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
   * @throws IllegalStateException si la case n'est pas vide (en validation stricte)
   * @throws IllegalArgumentException si le bâtiment est null (en validation stricte)
    */
   public void setBatiment(Batiment batiment) {
      // Validation : la case ne doit pas déjà contenir de bâtiment
      Validation.requireState(!this.aBatiment(), "La case (" + x + ", " + y + ") n'est pas vide");
      // Validation : le bâtiment ne peut pas être nul
      Validation.requireArgument(batiment != null, "batiment=null");
      // Validation : les coordonnées du bâtiment doivent correspondre à la case ciblée.
      Validation.requireArgument(
         batiment.getX() == x && batiment.getY() == y,
         "Coordonnees batiment incoherentes avec la case: batiment=(" + batiment.getX() + ", " + batiment.getY() + ") case=(" + x + ", " + y + ")"
      );
      this.batiment = batiment;
   }


   /***** GESTION DES BATIMENTS *****/

   /** Détruit le bâtiment présent sur la case */
   public void detruireBatiment() {
      if (this.batiment != null) {
          this.batiment.detruire();
          // Ne pas retirer le bâtiment maître de la case pour garder son affichage
          // (même détruit, on veut pouvoir afficher son état)
          if (this.batiment.getType() != TypeBatiment.BATIMENT_MAITRE) {
              this.batiment = null;
          }
      }
   }

   /** Construction de batiments */

   public void construireStockage(Terrain terrain) {
      if (this.getBatiment() != null) return;
      Stockage stockage = new Stockage(this.getX(), this.getY(), terrain);
      this.setBatiment(stockage);
      AsyncExecutor.runAsync(stockage); 
   }

   public void construireForeuse(Terrain terrain) {
      if (this.getBatiment() != null || this.getType() != TypeCase.MINERAI) return;
      Foreuse foreuse = new Foreuse(this.getX(), this.getY(), terrain);
      this.setBatiment(foreuse);
      AsyncExecutor.runAsync(foreuse); // Lance la foreuse dans un thread séparé pour qu'elle puisse extraire le minerai en continu
   }

   public void construireRoute(Terrain terrain, Direction direction) {
      if (this.getBatiment() != null) return;
      Route route = new Route(direction, this.getX(), this.getY(), terrain);
      this.setBatiment(route);
   }

   public void construireUsine(Terrain terrain) {
      if (this.getBatiment() != null || this.getType() != TypeCase.VIDE) return;
      Usine usine = new Usine(this.getX(), this.getY(), terrain);
      this.setBatiment(usine);
      AsyncExecutor.runAsync(usine);
   }

    @Override
    public String getDisplayName() {
        if (aBatiment()) {
            switch (getBatiment().getType()) {
                case USINE           -> { return "USINE";    }
                case FOREUSE         -> { return "FOREUSE";  }
                case STOCKAGE        -> { return "STOCKAGE"; }
                case BATIMENT_MAITRE -> { return "HQ";       }
                case ROUTE           -> { return "ROUTE";    }
            }
        } 
        if (aMinerai()) {
            return "MINERAI";
        }
        return "CASE VIDE";
    }

    @Override
    public String getDescription() {
        if (aBatiment()) {
            switch (getBatiment().getType()) {
                case USINE           -> { return "Fabrique des unités."; }
                case FOREUSE         -> { return "Extrait le minerai."; }
                case STOCKAGE        -> { return "Stocke les minerais."; }
                case BATIMENT_MAITRE -> { return "Sa destruction = défaite. \nIl peut stocker aussi les minerais."; }
                case ROUTE           -> { return "Achemine les minerais."; }
            }
        }
        else if (aMinerai()) {
            return "Gisement extractible.";
        }
        return "Aucun contenu";
    }
}
