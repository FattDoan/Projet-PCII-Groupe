package view;


public class Camera {
 
    private int offsetX = 0;
    private int offsetY = 0;
 
    private final int terrainPixelWidth;
    private final int terrainPixelHeight;
    private final int baseCellSize;
    private final int viewWidth;
    private final int viewHeight;
 
    public Camera(int terrainTaille, int baseCellSize, int viewWidth, int viewHeight) {
        this.baseCellSize       = baseCellSize;
        this.terrainPixelWidth  = terrainTaille * baseCellSize;
        this.terrainPixelHeight = terrainTaille * baseCellSize;
        this.viewWidth          = viewWidth;
        this.viewHeight         = viewHeight;
    }
 
    // ── Déplacement caméra ────────────────────────────────────────────────
 
    public void move(int dx, int dy) {
        setOffset(offsetX + dx, offsetY + dy);
    }
 
    public void setOffset(int x, int y) {
        int maxX = Math.max(0, terrainPixelWidth  - viewWidth);
        int maxY = Math.max(0, terrainPixelHeight - viewHeight);
        offsetX  = clamp(x, 0, maxX);
        offsetY  = clamp(y, 0, maxY);
    }
 
    // ── Conversion écran -> grille ────────────────────────────────────────
 
    public int screenToGridX(int screenX) {
        return (screenX + offsetX) / baseCellSize;
    }
 
    public int screenToGridY(int screenY) {
        return (screenY + offsetY) / baseCellSize;
    }
 
    // ── Accesseurs ─────────────────────────────────────────────────────────
 
    public int getOffsetX()      { return offsetX; }
    public int getOffsetY()      { return offsetY; }
    public int getBaseCellSize() { return baseCellSize; }
 
    // Conservé pour compatibilité avec le rendu existant.
    public float effectiveCellSize() { return baseCellSize; }
 
    // ── Utilitaires internes ───────────────────────────────────────────────
 
    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
