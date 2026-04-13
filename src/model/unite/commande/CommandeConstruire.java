package model.unite.commande;

import model.*;
import model.unite.Unite;

public class CommandeConstruire extends Commande {

    private final int tx, ty;
    private final TypeBatiment typeBatiment;
    private final Terrain terrain;
    private final double dureeConstruction; // en secondes

    // Internal phases
    private CommandeDeplacement deplacement;  // phase 1: walk there
    private double tempsRestant = -1;         // phase 2: build timer, -1 = not started

    public CommandeConstruire(int tx, int ty, TypeBatiment type, Terrain terrain, double dureeConstruction) {
        this.tx           = tx;
        this.ty           = ty;
        this.typeBatiment = type;
        this.terrain      = terrain;
        this.dureeConstruction = dureeConstruction;
    }

    @Override
    public boolean executer(Unite unite, double dt) {

        // ── Phase 1: walk to the cell ─────────────────────────────────────
        if (tempsRestant < 0) {
            if (deplacement == null) {
                // Target the center of the cell in world-pixel space
                float targetPX = tx * Unite.CASE_SIZE + Unite.CASE_SIZE / 2f;
                float targetPY = ty * Unite.CASE_SIZE + Unite.CASE_SIZE / 2f;
                deplacement = new CommandeDeplacement(targetPX, targetPY);
            }

            boolean arrived = deplacement.executer(unite, dt);
            if (!arrived) return false;

            // Arrived — check the cell is still buildable before starting
            Case c = terrain.getCase(tx, ty);
            if (!canBuild(c)) return true; // abort silently, cell changed

            tempsRestant = dureeConstruction; // start build timer
            return false;
        }

        // ── Phase 2: stand still and build ───────────────────────────────
        tempsRestant -= dt;
        if (tempsRestant > 0) return false;

        // Timer done — place the building
        Case c = terrain.getCase(tx, ty);
        if (canBuild(c)) placerBatiment(c);
        return true;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private boolean canBuild(Case c) {
        if (c.aBatiment()) return false;
        return switch (typeBatiment) {
            case FOREUSE  -> c.aMinerai();
            case STOCKAGE, ROUTE -> c.estVide() || c.aMinerai();
            default       -> false;
        };
    }

    private void placerBatiment(Case c) {
        switch (typeBatiment) {
            case FOREUSE  -> c.construireForeuse(terrain);
            case STOCKAGE -> c.construireStockage(terrain);
            // ROUTE needs a direction — extend constructor if needed
            default -> {}
        }
    }
}
