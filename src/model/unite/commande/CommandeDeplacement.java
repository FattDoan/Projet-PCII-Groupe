package model.unite.commande;

import model.unite.Unite;

public class CommandeDeplacement extends Commande {
    private final float finalPX, finalPY; // destination en coordonnées pixels

    public CommandeDeplacement(float finalPX, float finalPY) {
        this.finalPX = finalPX; this.finalPY = finalPY;
    }

    @Override
    public boolean executer(Unite unite, double dt) {
        float dx = finalPX - unite.getPX();
        float dy = finalPY - unite.getPY();
        float dist = (float) Math.hypot(dx, dy);
        float step = unite.getSpeed() * (float) dt;

        if (dist <= step) {
            unite.avancer(dx, dy);   // ajuste exactement sur la destination
            return true;             // termine
        }

        unite.avancer((dx / dist) * step, (dy / dist) * step);
        return false;
    }

    @Override
    public String getNom() {
        return "Deplacement";
    }
}
