package controller;
 
import model.Camera;
import model.Terrain;
import view.Affichage;
import view.AffichageTerrain;
 
public class GameController {
 
    private final Camera           camera;
    private final CameraController cameraController;
 
    public GameController(Terrain terrain, Affichage affichage) {
 
        // CRITICAL: pass LARGEUR/HAUTEUR (capped window size),
        // NOT LARGEUR_GRILLE/HAUTEUR_GRILLE (full terrain size).
        // Getting this wrong makes maxOffset = 0 → camera can't move.
        camera = new Camera(
            terrain.getTaille(),
            Affichage.TAILLE_CASE,
            Affichage.LARGEUR,   // window width
            Affichage.HAUTEUR    // window height
        );
 
        centerOnHQ(terrain);
        affichage.setCamera(camera);
 
        AffichageTerrain view = affichage.getAffichageTerrain();
 
        cameraController = new CameraController(camera, affichage);
        new ReactionClic(affichage, terrain, cameraController);
        new ReactionHover(view, terrain, camera);
    }
 
    /**
     * Centers the camera on the HQ tile (terrain.getTaille() / 2).
     *
     * Formula:
     *   HQ pixel center = hqGrid * cellSize + cellSize/2
     *   offsetX = HQ pixel center - viewWidth/2
     *
     * setOffset clamps to [0, maxOffset] so this is always safe.
     */
    private void centerOnHQ(Terrain terrain) {
        int cellSize   = Affichage.TAILLE_CASE;
        int hqGrid     = terrain.getTaille() / 2;
        int hqCenterPx = hqGrid * cellSize + cellSize / 2;
        int offsetX    = hqCenterPx - Affichage.LARGEUR  / 2;
        int offsetY    = hqCenterPx - Affichage.HAUTEUR  / 2;
        camera.setOffset(offsetX, offsetY);
    }
 
    public Camera getCamera() { return camera; }
}
