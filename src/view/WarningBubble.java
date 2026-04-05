package view;

import javax.swing.Timer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WarningBubble {

    // ── Dimensions ────────────────────────────────────────────────────────
    private static final int W       = 230;
    private static final int PAD     = 12;
    private static final int RADIUS  = 8;
    private static final int LINE_H  = 17;
    private static final int MAX_TEXT_W = W - PAD * 2 - 26; // room for X button

    // ── Palette ───────────────────────────────────────────────────────────
    private static final Color BG     = new Color( 40,  30,  30, 235);
    private static final Color BORDER = new Color(200,  70,  60, 210);
    private static final Color FG     = new Color(240, 200, 195);
    private static final Color X_COL  = new Color(160, 130, 130);
    private static final Color X_HOV  = new Color(220,  80,  70);

    // ── Fonts ─────────────────────────────────────────────────────────────
    private static final Font FONT   = new Font("Dialog", Font.PLAIN, 12);
    private static final Font FONT_X = new Font("Dialog", Font.BOLD,  13);

    // ── State ─────────────────────────────────────────────────────────────
    private String       message    = "";
    private List<String> lines      = new ArrayList<>();
    private int          screenX, screenY;
    private int          currentH   = 44;   // recomputed each paint()
    private boolean      visible    = false;
    private boolean      xHovered   = false;
    private Timer        timer;

    // ── Public API ────────────────────────────────────────────────────────

    public boolean isVisible() { return visible; }

    public void setXHovered(boolean b) { xHovered = b; }

    /** Close-button rect in panel (screen) coords. */
    public Rectangle getCloseRect() {
        return new Rectangle(screenX + W - 22, screenY + 4, 18, 18);
    }

    /**
     * Show the toast near the cursor.
     * cursorX/Y are raw mouse coords inside AffichageTerrain (screen space).
     * panelW/H are the panel dimensions for overflow clamping.
     */
    public void show(String msg, int cursorX, int cursorY, int panelW, int panelH) {
        this.message = msg;

        // Pre-wrap with a dummy FontMetrics so we can estimate height before paint.
        // We use a Canvas as a cheap FM source — no component needed.
        Canvas dummy = new Canvas();
        FontMetrics fm = dummy.getFontMetrics(FONT);
        lines = wrapText(fm, msg);
        currentH = PAD + lines.size() * LINE_H + PAD;

        // Position: bottom-right of cursor; flip if it would overflow.
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

        // ── Background ────────────────────────────────────────────────────
        g2.setColor(BG);
        g2.fillRoundRect(screenX, screenY, W, currentH, RADIUS, RADIUS);

        // ── Border ────────────────────────────────────────────────────────
        g2.setColor(BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(screenX, screenY, W, currentH, RADIUS, RADIUS);

        // ── Left accent bar ───────────────────────────────────────────────
        g2.setColor(BORDER);
        g2.fillRoundRect(screenX, screenY, 3, currentH, 3, 3);

        // ── Message lines ─────────────────────────────────────────────────
        g2.setFont(FONT);
        g2.setColor(FG);
        int textY = screenY + PAD + fm.getAscent() - 1;
        for (String line : lines) {
            g2.drawString(line, screenX + PAD, textY);
            textY += LINE_H;
        }

        // ── X button ──────────────────────────────────────────────────────
        Rectangle xr = getCloseRect();
        if (xHovered) {
            g2.setColor(X_HOV);
            g2.fillRoundRect(xr.x, xr.y, xr.width, xr.height, 4, 4);
        }
        g2.setFont(FONT_X);
        g2.setColor(xHovered ? Color.WHITE : X_COL);
        g2.drawString("×", xr.x + 3, xr.y + fm.getAscent());
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private List<String> wrapText(FontMetrics fm, String text) {
        List<String> result = new ArrayList<>();
        // Respect explicit newlines first
        for (String paragraph : text.split("\n")) {
            String[] words = paragraph.split(" ");
            StringBuilder cur = new StringBuilder();
            for (String word : words) {
                String test = cur.isEmpty() ? word : cur + " " + word;
                if (fm.stringWidth(test) <= MAX_TEXT_W) {
                    cur = new StringBuilder(test);
                } else {
                    if (!cur.isEmpty()) result.add(cur.toString());
                    // Word itself wider than max? Hard-clip it.
                    cur = new StringBuilder(hardClip(fm, word));
                }
            }
            if (!cur.isEmpty()) result.add(cur.toString());
        }
        return result;
    }

    /** Truncates a single word that is wider than MAX_TEXT_W. */
    private String hardClip(FontMetrics fm, String word) {
        while (word.length() > 1 && fm.stringWidth(word + "…") > MAX_TEXT_W)
            word = word.substring(0, word.length() - 1);
        return word;
    }
}
