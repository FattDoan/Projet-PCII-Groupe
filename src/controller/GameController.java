package controller;
 
import view.Camera;
import model.Terrain;
import view.Affichage;
import view.AffichageTerrain;
 
public class GameController {
 
    // Caméra unique de la session de jeu.
    private final Camera           camera;
    // Conserve une référence pour le cycle de vie des listeners (drag clavier/souris).
    private final CameraController cameraController;
 
    public GameController(Terrain terrain, Affichage affichage) {
 
        // Important: on initialise la caméra avec la taille de vue visible,
        // pas avec la taille totale de la map, sinon la caméra ne se déplace pas.
        camera = new Camera(
            terrain.getTaille(),
            Affichage.TAILLE_CASE,
            affichage.getLargeurVue(),
            affichage.getHauteurVue()
        );
 
        // Positionnement initial sur le bâtiment maître.
        centerOnHQ(terrain, affichage);
        // Injection de la caméra dans la couche vue.
        affichage.setCamera(camera);
 
        AffichageTerrain view = affichage.getAffichageTerrain();
 
        // Orchestration des interactions utilisateur.
        cameraController = new CameraController(camera, affichage);
        new ReactionClic(affichage, terrain, cameraController);
        new ReactionHover(affichage, terrain, camera);
    }
 
    /**
        * Centre la caméra sur la case du QG (terrain.getTaille() / 2).
     *
        * Formule:
        *   centrePixelQG = hqGrid * tailleCase + tailleCase/2
        *   offsetX = centrePixelQG - largeurVue/2
     *
        * setOffset borne automatiquement dans [0, maxOffset].
     */
    private void centerOnHQ(Terrain terrain, Affichage affichage) {
        int cellSize   = Affichage.TAILLE_CASE;
        int hqGrid     = terrain.getTaille() / 2;
        // Centre pixel de la case HQ.
        int hqCenterPx = hqGrid * cellSize + cellSize / 2;
        // Décalage pour que le centre HQ arrive au centre de la vue.
        int offsetX    = hqCenterPx - affichage.getLargeurVue() / 2;
        int offsetY    = hqCenterPx - affichage.getHauteurVue() / 2;
        // La caméra clamp automatiquement dans les bornes valides.
        camera.setOffset(offsetX, offsetY);
    }
 
    public Camera getCamera() { return camera; }
}
