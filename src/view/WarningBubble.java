package view;

import javax.swing.Timer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WarningBubble {

    // ── Dimensions ─────────────────────────────────────────────────────
    private static final int W       = 230;
    private static final int PAD     = 12;
    private static final int RADIUS  = 8;
    private static final int LINE_H  = 17;
    private static final int MAX_TEXT_W = W - PAD * 2 - 26; // reserve pour le bouton X

    // ── Palette ───────────────────────────────────────────────────────────
    private static final Color BG     = new Color( 40,  30,  30, 235);
    private static final Color BORDER = new Color(200,  70,  60, 210);
    private static final Color FG     = new Color(240, 200, 195);
    private static final Color X_COL  = new Color(160, 130, 130);
    private static final Color X_HOV  = new Color(220,  80,  70);

    // ── Polices ───────────────────────────────────────────────────────
    private static final Font FONT   = new Font("Dialog", Font.PLAIN, 12);
    private static final Font FONT_X = new Font("Dialog", Font.BOLD,  13);

    // ── Etat ───────────────────────────────────────────────────────────
    private String       message    = "";
    private List<String> lines      = new ArrayList<>();
    private int          screenX, screenY;
    private int          currentH   = 44;   // recomputed each paint()
    private boolean      visible    = false;
    private boolean      xHovered   = false;
    private Timer        timer;

    // ── API publique ──────────────────────────────────────────────────

    public boolean isVisible() { return visible; }

    public void setXHovered(boolean b) { xHovered = b; }

    /** Rectangle du bouton de fermeture en coordonnees ecran. */
    public Rectangle getCloseRect() {
        return new Rectangle(screenX + W - 22, screenY + 4, 18, 18);
    }

    /**
     * Affiche la bulle pres du curseur.
     * cursorX/Y : coordonnees souris brutes dans AffichageTerrain (espace ecran).
     * panelW/H : dimensions du panneau pour limiter le debordement.
     */
    public void show(String msg, int cursorX, int cursorY, int panelW, int panelH) {
        this.message = msg;

        // Pre-wrap avec un FontMetrics factice pour estimer la hauteur.
        // Canvas sert de source simple, sans composant visible.
        Canvas dummy = new Canvas();
        FontMetrics fm = dummy.getFontMetrics(FONT);
        lines = wrapText(fm, msg);
        currentH = PAD + lines.size() * LINE_H + PAD;

        // Position : bas-droite du curseur ; on inverse si depassement.
        int tx = cursorX + 14;
        int ty = cursorY + 14;
        if (tx + W       > panelW) tx = cursorX - W - 6;
        if (ty + currentH > panelH) ty = cursorY - currentH - 6;
        screenX = Math.max(0, tx);
        screenY = Math.max(0, ty);

        visible = true;

        if (timer != null && timer.isRunning()) timer.stop();
        timer = new Timer(3000, e -> { visible = false; });
        timer.setRepeats(false);
        timer.start();
    }

    public void dismiss() {
        visible = false;
        if (timer != null) timer.stop();
    }


    public void paint(Graphics2D g2) {
        if (!visible || message == null || message.isEmpty()) return;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        FontMetrics fm = g2.getFontMetrics(FONT);
        lines = wrapText(fm, message);
        currentH = PAD + lines.size() * LINE_H + PAD;

        // ── Fond ───────────────────────────────────────────────────────
        g2.setColor(BG);
        g2.fillRoundRect(screenX, screenY, W, currentH, RADIUS, RADIUS);

        // ── Bordure ───────────────────────────────────────────────────
        g2.setColor(BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(screenX, screenY, W, currentH, RADIUS, RADIUS);

        // ── Barre d'accent a gauche ────────────────────────────────────
        g2.setColor(BORDER);
        g2.fillRoundRect(screenX, screenY, 3, currentH, 3, 3);

        // ── Lignes de message ──────────────────────────────────────────
        g2.setFont(FONT);
        g2.setColor(FG);
        int textY = screenY + PAD + fm.getAscent() - 1;
        for (String line : lines) {
            g2.drawString(line, screenX + PAD, textY);
            textY += LINE_H;
        }

        // ── Bouton X ───────────────────────────────────────────────────
        Rectangle xr = getCloseRect();
        if (xHovered) {
            g2.setColor(X_HOV);
            g2.fillRoundRect(xr.x, xr.y, xr.width, xr.height, 4, 4);
        }
        g2.setFont(FONT_X);
        g2.setColor(xHovered ? Color.WHITE : X_COL);
        g2.drawString("×", xr.x + 3, xr.y + fm.getAscent());
    }

    // ── Aides ─────────────────────────────────────────────────────────

    private List<String> wrapText(FontMetrics fm, String text) {
        List<String> result = new ArrayList<>();
        // Respecte d'abord les retours a la ligne explicites
        for (String paragraph : text.split("\n")) {
            String[] words = paragraph.split(" ");
            StringBuilder cur = new StringBuilder();
            for (String word : words) {
                String test = cur.isEmpty() ? word : cur + " " + word;
                if (fm.stringWidth(test) <= MAX_TEXT_W) {
                    cur = new StringBuilder(test);
                } else {
                    if (!cur.isEmpty()) result.add(cur.toString());
                    // Mot plus large que la limite : on le tronque.
                    cur = new StringBuilder(hardClip(fm, word));
                }
            }
            if (!cur.isEmpty()) result.add(cur.toString());
        }
        return result;
    }

    /** Tronque un mot plus large que MAX_TEXT_W. */
    private String hardClip(FontMetrics fm, String word) {
        while (word.length() > 1 && fm.stringWidth(word + "…") > MAX_TEXT_W)
            word = word.substring(0, word.length() - 1);
        return word;
    }
}
