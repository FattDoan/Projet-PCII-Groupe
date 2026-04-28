package controller;
 
import view.Camera;
import model.Terrain;
import model.Case;
import view.Affichage;
import view.AffichageTerrain;
 
public class GameController {
 
    // Caméra unique de la session de jeu.
    // Conserve une référence pour le cycle de vie des listeners (drag clavier/souris).
    private final CameraController cameraController;
 
    public GameController(Terrain terrain, Affichage affichage) {
 
        // Important : on initialise la camera avec la taille de vue visible,
        // pas avec la taille totale de la map, sinon la caméra ne se déplace pas.
        Camera.getInstance().initCamera(
            terrain.getTaille(),
            Case.TAILLE,
            affichage.getLargeurVue(),
            affichage.getHauteurVue()
        );

        // Positionnement initial sur le bâtiment maître.
        centerOnHQ(terrain, affichage);
  
        // Orchestration des interactions utilisateur.
        cameraController = new CameraController(affichage);
        new ReactionClic(affichage, terrain, cameraController);
        new ReactionHover(affichage, terrain);
    }
 
     /**
      * Centre la camera sur la case du QG (terrain.getTaille() / 2).
      *
      * Formule :
      *   centrePixelQG = hqGrid * tailleCase + tailleCase/2
      *   offsetX = centrePixelQG - largeurVue/2
      *
      * setOffset borne automatiquement dans [0, maxOffset].
      */
    private void centerOnHQ(Terrain terrain, Affichage affichage) {
        int cellSize   = Case.TAILLE;
        int hqGrid     = terrain.getTaille() / 2;
        // Centre pixel de la case HQ.
        int hqCenterPx = hqGrid * cellSize + cellSize / 2;
        // Décalage pour que le centre HQ arrive au centre de la vue.
        int offsetX    = hqCenterPx - affichage.getLargeurVue() / 2;
        int offsetY    = hqCenterPx - affichage.getHauteurVue() / 2;
        // La caméra clamp automatiquement dans les bornes valides.
        Camera.getInstance().setOffset(offsetX, offsetY);
    }
 
}
