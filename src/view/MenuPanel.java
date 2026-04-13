package view;

import model.*;
import model.unite.Unite;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/** Panneau du menu contenant toutes les données relatives à la case sélectionnée */
public class MenuPanel extends JPanel {

    // ── Palette ───────────────────────────────────────────────────────────
    private static final Color C_BASE       = new Color(18,  18,  20);
    private static final Color C_SURFACE    = new Color(26,  27,  30);
    private static final Color C_RAISED     = new Color(38,  39,  45);
    private static final Color C_BORDER     = new Color(55,  57,  65);
    private static final Color C_BORDER_LIT = new Color(80,  83,  98);
    private static final Color C_TEXT_PRI   = new Color(220, 222, 228);
    private static final Color C_TEXT_SEC   = new Color(130, 133, 145);
    private static final Color C_TEXT_DIM   = new Color(72,  74,  84);
    private static final Color C_AMBER      = new Color(255, 185,  30);
    private static final Color C_GREEN      = new Color( 85, 210, 130);
    private static final Color C_RED        = new Color(225,  75,  65);
    private static final Color C_BLUE       = new Color( 90, 160, 255);

    // ── Polices ───────────────────────────────────────────────────────────
    private static final Font F_TITLE = new Font("Dialog", Font.BOLD,  18);
    private static final Font F_DESC  = new Font("Dialog", Font.PLAIN, 14);
    private static final Font F_SECT  = new Font("Dialog", Font.BOLD,  13);
    private static final Font F_KEY   = new Font("Dialog", Font.PLAIN, 14);
    private static final Font F_VAL   = new Font("Dialog", Font.BOLD,  16);
    private static final Font F_BTN   = new Font("Dialog", Font.BOLD,  14);
    private static final Font F_SMALL = new Font("Dialog", Font.PLAIN, 11);

    /** Définit les types de rendu pour une meilleure qualité d'affichage */
    private static void hint(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,     RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    // ── État ──────────────────────────────────────────────────────────────
    /** L'élément sélectionné (Case ou Unite), ou null si aucune sélection */
    private volatile Selectable selectedElement = null;
    private UnitActionCallback unitCallback = null;
    
    private final Affichage affichage;
    private final Terrain   terrain;

    // ── Sous-panneaux ─────────────────────────────────────────────────────
    private final HeaderPanel  header;
    private final StatsPanel   stats;
    private final ActionsPanel actions;

    public MenuPanel(int width, int height, Affichage affichage, Terrain terrain) {
        setPreferredSize(new Dimension(width, height));
        setBackground(C_BASE);
        setLayout(new BorderLayout(0, 0));

        this.affichage = affichage;
        this.terrain   = terrain;

        header  = new HeaderPanel();
        stats   = new StatsPanel();
        actions = new ActionsPanel();

        JScrollPane scroll = new JScrollPane(stats,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(C_SURFACE);
        JScrollBar vsb = scroll.getVerticalScrollBar();
        vsb.setPreferredSize(new Dimension(4, 0));
        vsb.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { thumbColor = C_BORDER_LIT; trackColor = C_BASE; }
            @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
            private JButton zeroBtn() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
        });

        setBorder(new MatteBorder(0, 3, 0, 0, C_BORDER_LIT));
        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        // Pré-initialisation visuelle pour éviter un premier affichage saccadé
        header.afficher(null);
        stats.afficher(null);

        this.doLayout();
        BufferedImage warmUp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.paint(warmUp.getGraphics());
    }
    
    // ── API publique ──────────────────────────────────────────────────────

    public void setSelectedElement(Selectable s) { this.selectedElement = s; }
    public Selectable getSelectedElement()        { return this.selectedElement; }
    public void setUnitCallback(UnitActionCallback cb) { this.unitCallback = cb; }

    /** Met à jour les informations affichées. Appelé par TaskRedessine via refreshMenuIfSelected(). */
    public void refresh() {
        if (selectedElement == null) return;
        header.afficher(selectedElement);
        stats.afficher(selectedElement);
        actions.afficher(selectedElement);
    }

    // ═════════════════════════════════════════════════════════════════════
    // HeaderPanel — icône + titre + description + bouton ×
    // ═════════════════════════════════════════════════════════════════════
    private class HeaderPanel extends JPanel {

        private static final int ICON_SIZE = 52;
        private static final int H         = 80;

        private String        title   = "";
        private String[]      desc    = {};
        private Color         accent  = C_TEXT_DIM;
        private BufferedImage icon    = null;

        // Cache des icônes pour éviter de les reconstruire à chaque refresh
        private static final java.util.Map<Object, BufferedImage> ICON_CACHE = new java.util.HashMap<>();

        // Suivi de l'état affiché pour éviter les repaint inutiles
        private Selectable lastElement  = null;
        private Batiment   lastBatiment = null;

        HeaderPanel() {
            setLayout(null);
            setPreferredSize(new Dimension(0, H));
            setBackground(C_RAISED);

            // Bouton de fermeture en haut à droite
            JButton close = new JButton("×");
            close.setFont(new Font("Dialog", Font.PLAIN, 20));
            close.setMargin(new Insets(0, 0, 0, 0));
            close.setForeground(C_TEXT_SEC);
            close.setBackground(C_RAISED);
            close.setFocusPainted(false);
            close.setBorderPainted(false);
            close.setContentAreaFilled(false);
            close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            close.addActionListener(e -> affichage.hideMenu());
            close.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { close.setForeground(C_TEXT_PRI); close.repaint(); }
                @Override public void mouseExited (java.awt.event.MouseEvent e) { close.setForeground(C_TEXT_SEC); close.repaint(); }
            });
            add(close);
            putClientProperty("closeBtn", close);
        }

        @Override public void doLayout() {
            super.doLayout();
            JButton b = (JButton) getClientProperty("closeBtn");
            if (b != null) b.setBounds(getWidth() - 40, 6, 36, 36);
        }

        void afficher(Selectable s) {
            // Extrait les parties pertinentes pour la comparaison
            Batiment currentBatiment = (s instanceof Case c) ? c.getBatiment() : null;

            // Évite un repaint complet si rien n'a changé
            if (s == lastElement && currentBatiment == lastBatiment) return;
            lastElement  = s;
            lastBatiment = currentBatiment;

            if (s == null) {
                title = ""; desc = new String[]{}; accent = C_TEXT_DIM; icon = null;

            } else if (s instanceof Unite u) {
                title  = u.getDisplayName();
                desc   = new String[]{ u.getDescription() };
                accent = C_GREEN;
                icon   = ICON_CACHE.computeIfAbsent("UNITE", k -> buildUnitIcon());

            } else if (s instanceof Case c) {
                title = c.getDisplayName();
                desc  = new String[]{ c.getDescription() };

                if (c.getBatiment() != null) {
                    TypeBatiment type = c.getBatiment().type();
                    accent = switch (type) {
                        case USINE           -> C_BLUE;
                        case FOREUSE         -> C_AMBER;
                        case STOCKAGE        -> C_GREEN;
                        case BATIMENT_MAITRE -> C_AMBER;
                        case ROUTE           -> C_TEXT_SEC;
                    };
                    icon = ICON_CACHE.computeIfAbsent(type, k -> buildBatimentIcon(type, accent));

                } else if (c.getType() == TypeCase.MINERAI) {
                    accent = C_AMBER;
                    icon   = ICON_CACHE.computeIfAbsent("MINERAI", k -> buildMineraiIcon());

                } else {
                    accent = C_TEXT_DIM;
                    icon   = ICON_CACHE.computeIfAbsent("CASE_VIDE", k -> buildCaseVideIcon());
                }
            }
            repaint();
        }

        // ── Constructeurs d'icônes ────────────────────────────────────────

        private static BufferedImage buildUnitIcon() {
            int s = ICON_SIZE;
            BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics(); hint(g);
            g.setColor(new Color(150, 100, 255));
            g.fillOval(4, 4, s-8, s-8);
            g.dispose(); return img;
        }

        private static BufferedImage buildBatimentIcon(TypeBatiment type, Color accent) {
            int s = ICON_SIZE;
            BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics(); hint(g);
            g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
            g.fillRoundRect(0, 0, s, s, 10, 10);
            g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 110));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(1, 1, s-2, s-2, 10, 10);
            g.setColor(accent);
            switch (type) {
                case USINE -> {
                    g.fillOval(s/4, s/4, s/2, s/2);
                    g.setColor(C_RAISED); g.fillOval(s*3/8, s*3/8, s/4, s/4);
                    g.setColor(accent);
                    g.fillRect(s/2-3, 4, 6, 10); g.fillRect(s/2-3, s-14, 6, 10);
                    g.fillRect(4, s/2-3, 10, 6); g.fillRect(s-14, s/2-3, 10, 6);
                }
                case FOREUSE -> {
                    int[] xp={s/2,s-8,8}, yp={s-8,8,8};
                    g.fillPolygon(xp, yp, 3);
                    g.setColor(C_RAISED); g.fillRect(s/2-2, 14, 4, 14);
                }
                case STOCKAGE -> { g.fillRect(8, s/2, s-16, s/2-8); g.fillRect(6, s/2-4, s-12, 8); }
                case BATIMENT_MAITRE -> {
                    int[] xp={s/2, s*3/4-4, s-8, s*3/4-4, s/2, s/4+4, 8, s/4+4};
                    int[] yp={8, s/2-4, s/2, s*3/4-4, s-8, s*3/4-4, s/2, s/2-4};
                    g.fillPolygon(xp, yp, 8);
                }
                case ROUTE -> {
                    int[] xp={s/2,s-8,s*3/4,s*3/4,s/4,s/4,8};
                    int[] yp={8,s/2,s/2,s*3/4-4,s*3/4-4,s/2,s/2};
                    g.fillPolygon(xp, yp, 7);
                }
            }
            g.dispose(); return img;
        }

        private static BufferedImage buildMineraiIcon() {
            int s = ICON_SIZE;
            BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics(); hint(g);
            g.fillRoundRect(0, 0, s, s, 10, 10);
            g.setColor(C_AMBER);
            int[] xp={s/2,s-6,s/2,6}, yp={6,s/2,s-6,s/2};
            g.fillPolygon(xp, yp, 4);
            g.dispose(); return img;
        }

        private static BufferedImage buildCaseVideIcon() {
            int s = ICON_SIZE;
            BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics(); hint(g);
            g.fillRoundRect(0, 0, s, s, 10, 10);
            g.setColor(C_TEXT_DIM);
            g.setStroke(new BasicStroke(2.0f));
            g.drawRoundRect(1, 1, s-2, s-2, 10, 10);
            g.dispose(); return img;
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g; hint(g2);
            int w = getWidth(), h = getHeight();

            g2.setColor(accent); g2.fillRect(0, 0, w, 3);

            int ix = 12, iy = (h - ICON_SIZE) / 2;
            if (icon != null) g2.drawImage(icon, ix, iy, null);
            else { g2.setColor(C_BORDER); g2.drawRoundRect(ix, iy, ICON_SIZE, ICON_SIZE, 8, 8); }

            int tx = ix + ICON_SIZE + 12;
            g2.setFont(F_TITLE); g2.setColor(C_TEXT_PRI);
            g2.drawString(title, tx, 26);
            g2.setFont(F_DESC); g2.setColor(C_TEXT_SEC);
            int ly = 44;
            for (String line : desc) { g2.drawString(line, tx, ly); ly += 17; }

            g2.setColor(C_BORDER); g2.drawLine(0, h-1, w, h-1);
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    // StatsPanel
    // ═════════════════════════════════════════════════════════════════════
    private class StatsPanel extends JPanel {

        // Références live pour les mises à jour légères (même case, stockage change)
        private CapacityBar liveCapBar           = null;
        private HPBar       liveHpBar            = null;
        private JLabel      liveStockageLabel    = null;
        
        // Unit info
        private JLabel      liveUnitPosLabel     = null;
        private JLabel      liveUnitStockageLabel   = null;

        // Suivi de l'état pour différencier reconstruction complète / mise à jour légère
        private Selectable lastElement  = null;

        StatsPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(C_SURFACE);
            setBorder(new EmptyBorder(6, 0, 8, 0));
        }

        void afficher(Selectable s) {
            // Extraire les parties concrètes
            Case c = (s instanceof Case cas) ? cas : null;
            Unite u = (s instanceof Unite unit) ? unit : null;

            // Mise à jour légère :
            // si c'est batiment -> mise à jour du stockage (extraction ou transport)
            // si c'est une unité -> mise à jour de la position (déplacement)
            if (s != null && s == lastElement) {
                if (s instanceof Case) {
                    if (c != null && c.getBatiment() != null) updateLiveStats(c.getBatiment());
                    return;
                }
                else if (u != null) {
                    updateUnitInfo(u); 
                    return;
                }
            }

            // Reconstruction complète
            lastElement  = s;
            removeAll();
            liveCapBar          = null;
            liveHpBar           = null;
            liveStockageLabel   = null; 
            if (s == null) {
                addMsg("Cliquez sur une case.");
                revalidate(); return;
            }

            // ── Stats d'une unité ─────────────────────────────────────────
            if (u != null) {
                addSection("UNITÉ");
                addRow("Type",     u.getDisplayName(), C_TEXT_PRI);
                liveUnitPosLabel = addRow("Position", "(" + u.getGX() + ", " + u.getGY() + ")", C_TEXT_SEC);
                addRow("Vitesse",  String.valueOf(u.getSpeed()), C_BLUE);
                liveUnitStockageLabel = addRow("Stockage", String.valueOf(u.getStockage()), C_AMBER);
                addSpacer(8);
                revalidate(); return;
            }

            // ── Stats d'une case ──────────────────────────────────────────
            if (c != null && c.getBatiment() != null) {
                Batiment b = c.getBatiment();

                liveHpBar = new HPBar(b.getHP(), b.getHPMax());
                add(liveHpBar);
                addSpacer(4);

                liveCapBar = new CapacityBar(b.getStockage(), b.getCapaciteMax());
                add(liveCapBar);
                addSpacer(6);

                addSection("BÂTIMENT");
                addRow("Classe", labelType(b.type()), C_TEXT_PRI);
                liveStockageLabel = addRow("Stockage", String.valueOf(b.getStockage()), C_AMBER);

                switch (b.type()) {
                    case FOREUSE -> {
                        String cadence = Foreuse.DELAI_EXTRACTION_MS > 0
                            ? String.format("%.1f", 1000.0 / Foreuse.DELAI_EXTRACTION_MS) : "∞";
                        addRow("Cadence", cadence + "/sec", C_TEXT_SEC);
                    }
                    case ROUTE -> {
                        if (b instanceof Route r) addRow("Direction", r.getDirection().toString(), C_BLUE);
                        String speed = Minerai.DELAI_TRANSPORT_MS > 0
                            ? String.format("%.1f", 1000.0 / Minerai.DELAI_TRANSPORT_MS) : "∞";
                        addRow("Vitesse", speed + "/sec", C_TEXT_SEC);
                    }
                    case USINE           -> addRow("État", "ACTIF", C_GREEN);
                    case BATIMENT_MAITRE -> addRow("Rôle", "HQ", C_AMBER);
                    default -> {}
                }
                addSpacer(8);
            }

            if (c != null && c.getType() == TypeCase.MINERAI) {
                addSection("RESSOURCE");
                addRow("Minerai", "Disponible", C_AMBER);
            }

            revalidate();
        }

        /** Mise à jour légère : met à jour seulement les valeurs qui changent à runtime */
        private void updateLiveStats(Batiment b) {
            if (liveCapBar        != null) liveCapBar.updateValues(b.getStockage(), b.getCapaciteMax());
            if (liveHpBar         != null) liveHpBar.updateValues(b.getHP(), b.getHPMax());
            if (liveStockageLabel != null) liveStockageLabel.setText(String.valueOf(b.getStockage()));
        }
        private void updateUnitInfo(Unite u) {
            if (liveUnitPosLabel != null) liveUnitPosLabel.setText("(" + u.getGX() + ", " + u.getGY() + ")");
            if (liveUnitStockageLabel != null) liveUnitStockageLabel.setText(String.valueOf(u.getStockage()));
        }

        // ── Helpers de construction ───────────────────────────────────────

        private void addSection(String text) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(C_RAISED);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            row.setPreferredSize(new Dimension(0, 28));
            row.setBorder(new EmptyBorder(0, 12, 0, 12));
            JLabel lbl = new JLabel(text); lbl.setFont(F_SECT); lbl.setForeground(C_TEXT_DIM);
            row.add(lbl, BorderLayout.WEST); add(row);
        }

        private JLabel addRow(String key, String value, Color vc) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setBackground(C_SURFACE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            row.setPreferredSize(new Dimension(0, 36));
            row.setBorder(new EmptyBorder(0, 12, 0, 12));
            JLabel kl = new JLabel(key);   kl.setFont(F_KEY); kl.setForeground(C_TEXT_SEC);
            JLabel vl = new JLabel(value); vl.setFont(F_VAL); vl.setForeground(vc);
            row.add(kl, BorderLayout.WEST); row.add(vl, BorderLayout.EAST);
            add(row);
            JSeparator sep = new JSeparator();
            sep.setForeground(C_BORDER); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            add(sep);
            return vl; // retourné pour les mises à jour live
        }

        private void addSpacer(int h) {
            JPanel sp = new JPanel(); sp.setBackground(C_BASE);
            sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
            sp.setPreferredSize(new Dimension(0, h)); add(sp);
        }

        private void addMsg(String msg) {
            JLabel lbl = new JLabel(msg, SwingConstants.CENTER);
            lbl.setFont(F_SMALL); lbl.setForeground(C_TEXT_DIM);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            lbl.setBorder(new EmptyBorder(24, 0, 0, 0)); add(lbl);
        }

        private static String labelType(TypeBatiment t) {
            return switch (t) {
                case USINE -> "Usine"; case FOREUSE -> "Foreuse";
                case STOCKAGE -> "Stockage"; case BATIMENT_MAITRE -> "Bâtiment maître"; case ROUTE -> "Route";
            };
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    // CapacityBar
    // ═════════════════════════════════════════════════════════════════════
    private static class CapacityBar extends JPanel {
        private float  ratio;
        private String label;
        private Color  bar;

        CapacityBar(int stock, int cap) {
            setPreferredSize(new Dimension(0, 28));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            setBackground(C_BASE);
            updateValues(stock, cap);
        }

        /** Mise à jour sans reconstruction du composant */
        public void updateValues(int stock, int cap) {
            float newRatio = cap > 0 ? Math.min(1f, (float)stock / cap) : 0f;
            String newLabel = stock + " / " + cap;
            Color newBar = newRatio > 0.85f ? C_RED : newRatio > 0.5f ? C_AMBER : C_GREEN;
            if (newRatio != ratio || !newLabel.equals(label)) {
                ratio = newRatio; label = newLabel; bar = newBar;
                repaint();
            }
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g; hint(g2);
            int w = getWidth(), h = getHeight(), f = (int)(w * ratio);
            g2.setColor(new Color(bar.getRed(), bar.getGreen(), bar.getBlue(), 40)); g2.fillRect(0, 0, w, h);
            g2.setColor(new Color(bar.getRed(), bar.getGreen(), bar.getBlue(), 100)); g2.fillRect(0, 0, f, h);
            g2.setColor(bar); g2.fillRect(0, h-3, f, 3);
            g2.setFont(F_SMALL); g2.setColor(C_TEXT_SEC);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, (w - fm.stringWidth(label))/2, (h + fm.getAscent() - fm.getDescent())/2);
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    // HPBar
    // ═════════════════════════════════════════════════════════════════════
    private static class HPBar extends JPanel {
        private float  ratio;
        private String label;
        private Color  bar;

        HPBar(int hp, int maxHP) {
            setPreferredSize(new Dimension(0, 28));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            setBackground(C_BASE);
            updateValues(hp, maxHP);
        }

        /** Mise à jour sans reconstruction du composant */
        public void updateValues(int hp, int maxHP) {
            float newRatio = maxHP > 0 ? Math.min(1f, (float)hp / maxHP) : 0f;
            String newLabel = hp + " / " + maxHP;
            Color newBar = newRatio > 0.85f ? C_GREEN : newRatio > 0.5f ? C_AMBER : C_RED;
            if (newRatio != ratio || !newLabel.equals(label)) {
                ratio = newRatio; label = newLabel; bar = newBar;
                repaint();
            }
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g; hint(g2);
            int w = getWidth(), h = getHeight(), f = (int)(w * ratio);
            g2.setColor(new Color(bar.getRed(), bar.getGreen(), bar.getBlue(), 40)); g2.fillRect(0, 0, w, h);
            g2.setColor(new Color(bar.getRed(), bar.getGreen(), bar.getBlue(), 100)); g2.fillRect(0, 0, f, h);
            g2.setColor(bar); g2.fillRect(0, h-3, f, 3);
            g2.setFont(F_SMALL); g2.setColor(C_TEXT_SEC);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, (w - fm.stringWidth(label))/2, (h + fm.getAscent() - fm.getDescent())/2);
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    // ActionsPanel
    // ═════════════════════════════════════════════════════════════════════
    private class ActionsPanel extends JPanel {

        // Suivi pour éviter les reconstructions inutiles
        private Selectable lastElement  = null;
        private Batiment   lastBatiment = null;

        ActionsPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(C_BASE);
            setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, C_BORDER_LIT),
                new EmptyBorder(10, 12, 12, 12)));
        }

        void afficher(Selectable s) {
            Batiment currentBatiment = (s instanceof Case c) ? c.getBatiment() : null;

            // Même élément, même bâtiment → rien à faire
            if (s == lastElement && currentBatiment == lastBatiment) return;
            lastElement  = s;
            lastBatiment = currentBatiment;

            removeAll();
            if (s == null) { revalidate(); repaint(); return; }

            JLabel hdr = new JLabel("ACTIONS");
            hdr.setFont(F_SECT); hdr.setForeground(C_TEXT_DIM);
            hdr.setBorder(new EmptyBorder(0, 0, 8, 0));
            add(hdr);

            // ── Actions pour les unités ───────────────────────────────────
            if (s instanceof Unite u) {
                addBtn("DÉPLACER",  C_BLUE,  () -> {
                    if (unitCallback != null) {
                        unitCallback.onDeplacer(u);
                    }
                }); 
                addBtn("MINER",  C_AMBER, () -> {
                    if (unitCallback != null) unitCallback.onMiner(u);
                }); // TODO
                addBtn("CONSTRUIRE", C_GREEN, () -> {
                    if (unitCallback != null) unitCallback.onConstruire(u);
                }); // TODO
                addBtn("DÉFENDRE",  C_GREEN, () -> {
                    if (unitCallback != null) unitCallback.onDefendre(u);
                }); // TODO
            }

            // ── Actions pour les cases ────────────────────────────────────
            else if (s instanceof Case c) {
                // Construction disponible sur case vide ou minerai sans bâtiment
                if (c.estVide() || (c.aMinerai() && !c.aBatiment())) {
                    if (c.getType() == TypeCase.MINERAI)
                        addBtn("CONSTRUIRE FOREUSE",  C_AMBER, () -> construireBatiment(c, TypeBatiment.FOREUSE));
                    addBtn("CONSTRUIRE STOCKAGE",     C_GREEN, () -> construireBatiment(c, TypeBatiment.STOCKAGE));
                    addBtn("CONSTRUIRE ROUTE",        C_BLUE,  () -> construireBatiment(c, TypeBatiment.ROUTE));
                }

                // Actions spécifiques au bâtiment présent
                if (c.aBatiment()) {
                    switch (c.getBatiment().type()) {
                        case USINE -> {
                            addBtn("AMÉLIORER", C_BLUE,       () -> {});
                            addBtn("PAUSE",     C_BORDER_LIT, () -> {});
                        }
                        case FOREUSE -> {
                            addBtn("RÉGLER VITESSE", C_AMBER,       () -> {});
                            addBtn("PAUSE",          C_BORDER_LIT,  () -> {});
                        }
                        case ROUTE -> {
                            addBtn("CHANGER DIRECTION", C_BLUE, () -> {
                                Direction newD = demanderDirectionRoute();
                                if (newD != null) ((Route) c.getBatiment()).changeDirection(newD);
                            });
                        }
                        case BATIMENT_MAITRE -> addBtn("INVENTAIRE", C_GREEN, () -> {});
                        case STOCKAGE -> {}
                    }
                    // Démolition pour tous sauf le bâtiment maître
                    if (c.getBatiment().type() != TypeBatiment.BATIMENT_MAITRE)
                        addBtn("DÉMOLIR", C_RED, () -> c.detruireBatiment());
                }
            }

            revalidate(); repaint();
        }

        // ── Construction ─────────────────────────────────────────────────

        private void construireBatiment(Case c, TypeBatiment type) {
            switch (type) {
                case USINE    -> {}
                case FOREUSE  -> c.construireForeuse(terrain);
                case STOCKAGE -> c.construireStockage(terrain);
                case BATIMENT_MAITRE -> throw new IllegalArgumentException("Le bâtiment maître ne peut pas être construit manuellement.");
                case ROUTE -> {
                    Direction newD = demanderDirectionRoute();
                    if (newD != null) c.construireRoute(terrain, newD);
                }
            }
            affichage.getAffichageTerrain().repaint();
            MenuPanel.this.refresh();
        }

        private Direction demanderDirectionRoute() {
            Direction[] options = {Direction.NORD, Direction.EST, Direction.SUD, Direction.OUEST};
            return (Direction) JOptionPane.showInputDialog(
                MenuPanel.this, "Choisissez la direction de la route :",
                "Construction route", JOptionPane.QUESTION_MESSAGE,
                null, options, Direction.NORD
            );
        }

        // ── Bouton HUD ────────────────────────────────────────────────────

        void addBtn(String label, Color accent, Runnable action) {
            JButton btn = new JButton(label) {
                private boolean hov = false;
                {
                    setFont(F_BTN); setForeground(C_TEXT_PRI); setBackground(C_RAISED);
                    setFocusPainted(false); setBorderPainted(false); setContentAreaFilled(false);
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
                    setPreferredSize(new Dimension(0, 32));
                    setHorizontalAlignment(SwingConstants.LEFT);
                    addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override public void mouseEntered(java.awt.event.MouseEvent e) { hov=true;  repaint(); }
                        @Override public void mouseExited (java.awt.event.MouseEvent e) { hov=false; repaint(); }
                    });
                    addActionListener(e -> action.run());
                }
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D)g; hint(g2);
                    int w=getWidth(), h=getHeight();
                    g2.setColor(hov ? C_RAISED.brighter() : C_RAISED); g2.fillRect(0,0,w,h);
                    g2.setColor(hov ? accent.brighter() : accent);      g2.fillRect(0,0,4,h);
                    g2.setColor(hov ? C_BORDER_LIT : C_BORDER);         g2.drawRect(0,0,w-1,h-1);
                    g2.setFont(getFont()); g2.setColor(hov ? Color.WHITE : C_TEXT_PRI);
                    FontMetrics fm=g2.getFontMetrics();
                    g2.drawString(getText(), 14, (h+fm.getAscent()-fm.getDescent())/2);
                }
            };
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(btn); add(Box.createVerticalStrut(6));
        }
    }
}
