package model;

public class TacheDeplacement extends Tache {
    private int x, y;

    public TacheDeplacement(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return this.x; }
    public int getY() { return this.y; }
}
