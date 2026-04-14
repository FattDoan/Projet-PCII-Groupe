package view;

public class Camera {
 
    private int offsetX = 0;
    private int offsetY = 0;

    private float zoom = 1.0f; 

    private int terrainPixelWidth;
    private int terrainPixelHeight;
    private int baseCellSize;
    private int viewWidth;
    private int viewHeight;
 
    private static final Camera INSTANCE = new Camera();

    public Camera() {}

    public void initCamera(int terrainTaille, int baseCellSize, int viewWidth, int viewHeight) {
        this.baseCellSize       = baseCellSize;
        this.terrainPixelWidth  = terrainTaille * baseCellSize;
        this.terrainPixelHeight = terrainTaille * baseCellSize;
        this.viewWidth          = viewWidth;
        this.viewHeight         = viewHeight;
    }

    public static Camera getInstance() {
        return INSTANCE;
    }
    // ── Déplacement caméra ────────────────────────────────────────────────
 
    public void move(int dx, int dy) {
        setOffset(offsetX + dx, offsetY + dy);
    }
 
    public void setOffset(int x, int y) {
        int maxX = Math.max(0, Math.round(terrainPixelWidth  * zoom) - viewWidth);
        int maxY = Math.max(0, Math.round(terrainPixelHeight * zoom) - viewHeight);
        offsetX  = clamp(x, 0, maxX);
        offsetY  = clamp(y, 0, maxY);
    }
 
    // ── Conversion écran -> grille ────────────────────────────────────────
 
    public float screenToWorldX(int screenX) {
        return (screenX + offsetX) / zoom;
    }

    public float screenToWorldY(int screenY) {
        return (screenY + offsetY) / zoom;
    }

    public int screenToGridX(int screenX) {
        return (int)(screenToWorldX(screenX) / baseCellSize);
    }

    public int screenToGridY(int screenY) {
        return (int)(screenToWorldY(screenY) / baseCellSize);
    }

    public int worldToScreenX(float worldX) {
        return Math.round(worldX * zoom - offsetX);
    }

    public int worldToScreenY(float worldY) {
        return Math.round(worldY * zoom - offsetY);
    }

        // ── Accesseurs ─────────────────────────────────────────────────────────
 
    public int getOffsetX()      { return offsetX; }
    public int getOffsetY()      { return offsetY; }
 
    // ── Utilitaires internes ───────────────────────────────────────────────
 
    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    public void updateViewSize(int w, int h) {
        this.viewWidth  = w;   // make these non-final
        this.viewHeight = h;
        setOffset(offsetX, offsetY); // re-clamp with new bounds
    }

    public static final float ZOOM_MIN  = 0.8f;
    public static final float ZOOM_MAX  = 3.0f;
    public static final float ZOOM_STEP = 0.2f;


    public void zoomAt(int screenX, int screenY, float delta) {
        float oldZoom = zoom;
        float newZoom = clamp(oldZoom + delta, ZOOM_MIN, ZOOM_MAX);
        if (newZoom == oldZoom) return;

        // World position under cursor BEFORE zoom
        float worldX = (screenX + offsetX) / oldZoom;
        float worldY = (screenY + offsetY) / oldZoom;

        zoom = newZoom;

        // Adjust offset so cursor stays on same world point
        int newOffsetX = Math.round(worldX * zoom - screenX);
        int newOffsetY = Math.round(worldY * zoom - screenY);

        setOffset(newOffsetX, newOffsetY);
    }


    public float getZoom() { return zoom; }
}
