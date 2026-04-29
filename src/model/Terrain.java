package model;

import model.unite.Unite;
import common.AsyncExecutor;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

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

   /** batiment maître */
   private BatimentMaitre batimentMaitre;
   private final List<Unite> unites = new CopyOnWriteArrayList<>();
   private final List<Minerai> mineraisEnTransit = new CopyOnWriteArrayList<>();

    /** La liste des batiments qui n'ont pas hp plein, pour que les ouvriers puissent les réparer. */
    private final Set<Batiment> batimentsEndommages = ConcurrentHashMap.newKeySet();

    /** La liste de tous les bâtiments présents sur le terrain, pour itérer facilement dessus. */
    // NOTE: on n'ajoute pas les routes à cette liste, car cette liste servira principalement pour
    // savoir les minerais stockés dans les bâtiments, et les routes ne stockent pas de minerai.
    private final Set<Batiment> batiments = ConcurrentHashMap.newKeySet();
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
      this.batimentMaitre = (BatimentMaitre) this.grille[centre][centre].getBatiment();

   }

   public boolean isPositionValide(int x, int y) {
      return x >= 0 && y >= 0 && x < taille && y < taille;
   }

   /***** GETTER *****/

   /**
    * Retourne la taille de la grille (nombre de cases par côté).
    * @return la dimension de la grille carrée
    */
   public int getTaille() {
       return taille;
   }

   public BatimentMaitre getBatimentMaitre() {
      return batimentMaitre;
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


    public Set<Batiment> getBatimentsEndommages() {
        return Set.copyOf(batimentsEndommages);
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

   // TODO: temp func pour tester 
   public void addUnite(Unite u) {
      Validation.requireArgument(u != null, "unite=null");
      unites.add(u);
      AsyncExecutor.runAsync(u);
   }

    public void removeUnite(Unite u) {
        Validation.requireArgument(u != null, "unite=null");
        unites.remove(u);
    }

   public List<Unite> getUnites() {
      return List.copyOf(unites);
   }

    public void updateUnites(double dt) {
        for (Unite u : unites) {
            u.update(dt);
        }
    }

    // Gestion des minerais en transit
    public void addMinerai(Minerai minerai) {
        Validation.requireArgument(minerai != null, "minerai=null");
        mineraisEnTransit.add(minerai);
        AsyncExecutor.runAsync(minerai);
    }
    
    public void removeMinerai(Minerai minerai) {
        mineraisEnTransit.remove(minerai);
    }
    
    public List<Minerai> getMineraisEnTransit() {
        return List.copyOf(mineraisEnTransit);
    }

    public Unite getUniteAt(int x, int y) {
        if (x < 0 || y < 0 || x >= taille || y >= taille) {
            return null; // coordonnées hors limites
        }

        for (Unite u : unites) {
            if (u.getGX() == x && u.getGY() == y) {
                return u;
            }
        }
        return null; // Aucune unité trouvée à cette position
    }

   // (x,y) sont les coordonnées de la case ciblée, pas les coordonnées en pixels
    public Selectable getSelectableAt(int x, int y) {
        if (x < 0 || y < 0 || x >= taille || y >= taille) {
            return null; // coordonnées hors limites
        }

        // Vérifie d'abord les unités présentes sur le terrain
        for (Unite u : unites) {
            if (u.getGX() == x && u.getGY() == y) {
                return (Selectable)u;
            }
        }

        // Si aucune unité n'est trouvée, vérifie la case pour un bâtiment
        Case c = getCase(x, y);
        if (c != null && c.getBatiment() != null) {
            return (Selectable)c;
        }

        return null; // Aucun selectable trouvé à cette position
    }

    public void notifyBatimentUpdated(Batiment b) {
        Validation.requireArgument(b != null, "batiment=null");
        if (b.isDestroyed()) {
            batimentsEndommages.remove(b);
            batiments.remove(b);
            return;
        }
        if (b.atFullHP()) {
            batimentsEndommages.remove(b);
        } else {
            batimentsEndommages.add(b);
        }
        
        if (!batiments.contains(b) && 
            (b.getType() == TypeBatiment.STOCKAGE || b.getType() == TypeBatiment.BATIMENT_MAITRE)) {
            batiments.add(b);
        }
    }

    public Batiment getNearestBatimentStockage(int x, int y) {
        Batiment nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Batiment b : batiments) {
            if (b.getType() == TypeBatiment.STOCKAGE || b.getType() == TypeBatiment.BATIMENT_MAITRE) {
                double distance = Math.sqrt(Math.pow(b.getX() - x, 2) + Math.pow(b.getY() - y, 2));
                if (distance < minDistance && b.getStockage() > 0) { // On vérifie aussi que le bâtiment a du minerai à stocker
                    minDistance = distance;
                    nearest = b;
                }
            }
        }

        return nearest;
    }

}
