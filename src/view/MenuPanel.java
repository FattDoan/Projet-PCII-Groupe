package view;
 
import model.*;
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


    /** Définit les types de rendu pour une meilleure qualité d'affichage (antialiasing, interpolation...) */
    private static void hint(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    // ── Etat ──────────────────────────────────────────────────────────────
    /** La case actuellement sélectionnée */
    private volatile Case selectedCase = null;
    /** L'instance d'affichage utilisée pour le rendu */
    private final Affichage affichage;
    /** Terrain du jeu pour créer des bâtiments sur la case sélectionnée */
    private final Terrain terrain;

    // ── Sous-panneaux ─────────────────────────────────────────────────────
    /** Contient l'en-tête du menu/les infos de base sur la case sélectionnée (logo, nom) */
    private final HeaderPanel  header;
    /** Contient les statistiques de la case sélectionnée */
    private final StatsPanel   stats;
    /** Contient les actions disponibles pour la case sélectionnée */
    private final ActionsPanel actions;

    public MenuPanel(int width, int height, Affichage affichage, Terrain terrain) {
        // On fixe la taille du panneau et les propriétés de base.
        setPreferredSize(new Dimension(width, height));
        setBackground(C_BASE);
        setLayout(new BorderLayout(0, 0));

        // On initialise les variables
        this.affichage = affichage;
        this.terrain = terrain;

        header  = new HeaderPanel();
        stats   = new StatsPanel();
        actions = new ActionsPanel();


        // Le panneau de stats est placé dans un JScrollPane pour gérer les cas de contenu trop grand.
        JScrollPane scroll = new JScrollPane(stats,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(C_SURFACE);

        // Personnalisation de la scrollbar pour l'intégration visuelle.
        JScrollBar vsb = scroll.getVerticalScrollBar();
        vsb.setPreferredSize(new Dimension(4, 0));

        vsb.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = C_BORDER_LIT; trackColor = C_BASE;
            }
            // Pas de boutons de défilement, on les remplace par des composants vides.
            @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
            private JButton zeroBtn() {
                JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0)); return b;
            }
        });


        // Bordure d'accent à gauche, volontairement statique.
        setBorder(new MatteBorder(0, 3, 0, 0, C_BORDER_LIT));

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);


        // Pré-initialisation visuelle pour éviter un premier affichage saccadé.
        stats.update((Case)null); 
        header.update((Case)null);
    
        // Force le calcul de layout immédiatement.
        this.doLayout();
        
        // Pré-rendu hors écran pour chauffer le rendu des polices.
        BufferedImage warmUp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.paint(warmUp.getGraphics());
        //----------------------------------------------------------------------------------
    }



    /***** GETTER *****/

    public void setSelectedCase(Case c) { 
        this.selectedCase = c; 
    }

    /***** SETTER *****/

    public Case getSelectedCase() {
        return this.selectedCase;
    }

    /** Met à jour les informations affichées (uniquement si une case est sélectionnée) */
    public void refresh() {
        if (selectedCase == null) return;
        header.update(selectedCase);
        stats.update(selectedCase);
        actions.update(selectedCase);
    }




    // ═════════════════════════════════════════════════════════════════════
    // Panneau d'en-tête
    // ═════════════════════════════════════════════════════════════════════
    private class HeaderPanel extends JPanel {

        /**** CONSTANTES VISUELLES ****/

        // taille du logo correpondant au type de la case
        private static final int ICON_SIZE = 52;
        // hauteur fixe du panneau d'en-tête
        private static final int H        = 80;


        /**** VARIABLES ****/

        private String        title   = "";
        private String[]      desc    = {};
        private Color         accent  = C_TEXT_DIM;
        private BufferedImage icon    = null;

        // Cache pour éviter de reconstruire les icônes à chaque fois. Les clés sont soit des TypeBatiment, soit la chaîne "MINERAI".
        private static final java.util.Map<Object, BufferedImage> ICON_CACHE = new java.util.HashMap<>();
        // La dernière case affichée pour éviter les recalculs inutiles.
        private Case lastCase = null;
        // Dernier bâtiment observé pour détecter les changements sur une même case.
        private Batiment lastBatiment = null;

        // Constructeur: initialise le layout, le bouton de fermeture et les éléments de base.
        /*package-private*/ HeaderPanel() {
            setLayout(null);
            setPreferredSize(new Dimension(0, H));
            setBackground(C_RAISED);
 
            // Bouton pour fermer le panneau en haut à droite.
            JButton close = new JButton("×") {
                {   // Style visuel du bouton de fermeture.
                    setFont(new Font("Dialog", Font.PLAIN, 20));
                    setMargin(new Insets(0, 0, 0, 0));
                    setForeground(C_TEXT_SEC); setBackground(C_RAISED);
                    setFocusPainted(false); setBorderPainted(false);
                    setContentAreaFilled(false);
                    // TODO : demander à chatgpt vu que apparemment c'est lui qui a fait ce code
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    // Action de fermeture: cache le menu et réinitialise la sélection.
                    addActionListener(e -> affichage.hideMenu());
                    // Modifie la couleur de l'arrière-plan lorsqu'on passe la souris sur le bouton.
                    addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override public void mouseEntered(java.awt.event.MouseEvent e) { setForeground(C_TEXT_PRI); repaint(); }
                        @Override public void mouseExited (java.awt.event.MouseEvent e) { setForeground(C_TEXT_SEC); repaint(); }
                  }); }
            };            
            close.setBounds(0, 0, 28, 28); // Position finale ajustée dans doLayout.

            // ajout du bouton de fermeture au panneau d'en-tête
            add(close);
            // Stocke la référence pour le repositionnement dynamique.
            this.putClientProperty("closeBtn", close);
        }

        @Override public void doLayout() {
            super.doLayout();
            JButton b = (JButton) getClientProperty("closeBtn");
            if (b != null) b.setBounds(getWidth() - 40, 6, 36, 36);
        }

        // met à jour le contenu du panneau d'en-tête en fonction de la case sélectionnée
        void update(Case c) {
            // Évite un recalcul complet si la case est identique.
            Batiment currentBatiment = (c != null) ? c.getBatiment() : null;
            if (c == lastCase && currentBatiment == lastBatiment) return;
            lastCase = c;
            lastBatiment = currentBatiment;

            // Détermine le nom, la couleur et la description en fonction du type/batiment dans la case. Utilise le cache pour les icônes.
            // Si aucune case n'est sélectionnée, affiche un état vide.
            if (c == null) { title = ""; desc = new String[]{}; accent = C_TEXT_DIM; icon = null; }
            // Si la case contient un bâtiment, on affiche les infos du bâtiment.
            else if (c.getBatiment() != null) {
                TypeBatiment type = c.getBatiment().type();
                switch (type) {
                    case USINE           -> { title = "USINE";    desc = new String[]{"Fabrique des unités."}; accent = C_BLUE; }
                    case FOREUSE         -> { title = "FOREUSE";  desc = new String[]{"Extrait le minerai."}; accent = C_AMBER; }
                    case STOCKAGE        -> { title = "STOCKAGE"; desc = new String[]{"Stocke les minerais."}; accent = C_GREEN; }
                    case BATIMENT_MAITRE -> { title = "HQ";       desc = new String[]{"Sa destruction = défaite. \nIl peut stocker aussi les minerais."}; accent = C_AMBER; }
                    case ROUTE           -> { title = "ROUTE";    desc = new String[]{"Achemine les minerais."}; accent = C_TEXT_SEC; }
                }
                // Récupère l'icone dans le cache si elle y est déjà présente, sinon la construit et la stocke dans le cache pour les prochaines fois.
                icon = ICON_CACHE.computeIfAbsent(type, t -> buildIcon((TypeBatiment)t, accent)); 
            }
            // Si la case contient un minerai, on affiche les infos du minerai.
            else if (c.getType() == TypeCase.MINERAI) {
                title = "MINERAI"; desc = new String[]{"Gisement extractible."}; accent = C_AMBER;
                icon = ICON_CACHE.computeIfAbsent("MINERAI", k -> buildMineraiIcon());
            }
            // Si la case est vide 
            else {
                title = "CASE VIDE"; desc = new String[]{"Aucun contenu."}; accent = C_TEXT_DIM; icon = null;
                icon = ICON_CACHE.computeIfAbsent("CASE VIDE", k -> buildCaseVideIcon());
            }

            // met à jour l'affichage avec les nouvelles informations.
            repaint();
        }

        /** Construit une icône simple pour un type de bâtiment donné (icones simples, peut-être modifier plus tard) */
        private static BufferedImage buildIcon(TypeBatiment type, Color accent) {
            // la taille de l'icône
            int s = ICON_SIZE;
            // On créé une image transparente sur laquelle on va dessiner l'icône du bâtiment. On utilise un Graphics2D pour bénéficier des options de rendu avancées.
            BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            // on définit les hints pour un rendu de meilleure qualité
            hint(g);

            // Fond de l'icône (couleur partiellement transparente)
            g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
            g.fillRoundRect(0, 0, s, s, 10, 10);
            g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 110));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(1, 1, s-2, s-2, 10, 10);

            // Dessine une forme simple représentant le type de bâtiment
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
                    // Dessine une étoile pour le bâtiment maître.
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
            // Libère les ressources graphiques et retourne l'image de l'icône construite.
            g.dispose(); 
            return img;
        }

        /** Construit une icône simple pour le minerai (un losange orange) (a modifier plus tard). */
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

        /** Construit une icône simple pour une case vide. */
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
 
            // Barre d'accent en partie haute.
            g2.setColor(accent); g2.fillRect(0, 0, w, 3);
 
            // Icône du type sélectionné.
            int ix = 12, iy = (h - ICON_SIZE) / 2;
            if (icon != null) g2.drawImage(icon, ix, iy, null);
            else { g2.setColor(C_BORDER); g2.drawRoundRect(ix, iy, ICON_SIZE, ICON_SIZE, 8, 8); }
 
            // Titre et description de l'élément.
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
    // Panneau statistiques
    // ═════════════════════════════════════════════════════════════════════
    /** Panneau affichant les statistiques d'une case sélectionnée. */
    private class StatsPanel extends JPanel {
        // Suivi d'état pour limiter les reconstructions de l'UI.
        private CapacityBar liveCapBar = null;
        private JLabel liveStockageLabel = null;
        private Case lastCase = null;
        private Batiment lastBatiment = null;

        /** Constructeur du panneau de statistiques. */
        StatsPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(C_SURFACE);
            setBorder(new EmptyBorder(6, 0, 8, 0));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g; hint(g2);
        }

        /** Met à jour les statistiques du panneau. */
        void update(Case c) {
            // Optimisation: même case => mise à jour légère des valeurs.
            Batiment currentBatiment = (c != null) ? c.getBatiment() : null;
            if (c != null && c == lastCase && currentBatiment == lastBatiment) {
                updateLiveStats(c);
                return;
            }

            // La case a changé: on reconstruit le contenu du panneau.
            lastCase = c;
            lastBatiment = currentBatiment;
            removeAll();
            liveCapBar = null;
            liveStockageLabel = null;

            // Si aucune case n'est sélectionnée, on affiche un message d'invite.
            if (c == null) { addMsg("Cliquez sur une case."); revalidate(); repaint(); return; }

            // S'il y a un bâtiment, on affiche ses statistiques principales.
            if (c.getBatiment() != null) {
                Batiment b = c.getBatiment();

                // Conserve une référence pour les mises à jour incrémentales.
                liveCapBar = new CapacityBar(b.getStockage(), b.getCapaciteMax());
                add(liveCapBar);

                addSpacer(6);
                addSection("BÂTIMENT");
                addRow("Classe", labelType(b.type()), C_TEXT_PRI);

                // Référence du label de stockage pour MAJ sans reconstruire.
                liveStockageLabel = addRow("Stockage", String.valueOf(b.getStockage()), C_AMBER);

                // Statistiques spécifiques selon le type de bâtiment.
                switch (b.type()) {
                    case FOREUSE: 
                            String cadence = Foreuse.DELAI_EXTRACTION_MS > 0 ? String.format("%.1f", 1000.0 / Foreuse.DELAI_EXTRACTION_MS) : "∞";
                            addRow("Cadence",  cadence+"/sec", C_TEXT_SEC);
                            break;
                    case ROUTE: 
                            String speed = Minerai.DELAI_TRANSPORT_MS > 0 ? String.format("%.1f", 1000.0 / Minerai.DELAI_TRANSPORT_MS) : "∞";
                            if (b instanceof Route r) addRow("Direction", labelDir(r.getDirection()), C_BLUE); 
                            addRow("Vitesse", speed+"/sec", C_TEXT_SEC);
                            break;
                    case USINE:
                            addRow("État",      "ACTIF",            C_GREEN);
                            break;
                    case BATIMENT_MAITRE:
                            addRow("Rôle",      "HQ", C_AMBER);
                            break;
                    default:
                            break;
                }
                addSpacer(8);
            }
            // Si la case contient du minerai, on affiche les infos du minerai.
            if (c.getType() == TypeCase.MINERAI) {
                addSection("RESSOURCE");
                addRow("Minerai", "Disponible", C_AMBER);
            }
            // On force la mise à jour de l'affichage après avoir reconstruit le contenu.
            revalidate(); 
            repaint();
        }

        /** Met à jour les statistiques du bâtiment de la case sélectionée */
        private void updateLiveStats(Case c) {
            if (c == null) return;
            if (c.getBatiment() == null) return;
            Batiment b = c.getBatiment();
            
            if (liveCapBar != null) liveCapBar.updateValues(b.getStockage(), b.getCapaciteMax());
            if (liveStockageLabel != null) liveStockageLabel.setText(String.valueOf(b.getStockage()));
        }

        /** Ajoute une section au panneau */
        private void addSection(String text) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(C_RAISED);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            row.setPreferredSize(new Dimension(0, 28));
            row.setBorder(new EmptyBorder(0, 12, 0, 12));
            JLabel lbl = new JLabel(text); lbl.setFont(F_SECT); lbl.setForeground(C_TEXT_DIM);
            row.add(lbl, BorderLayout.WEST); add(row);
        }
 
        /** Ajoute une ligne de statistiques au panneau */
        private JLabel addRow(String key, String value, Color vc) { 
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setBackground(C_SURFACE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            row.setPreferredSize(new Dimension(0, 36));
            row.setBorder(new EmptyBorder(0, 12, 0, 12));
            JLabel kl = new JLabel(key);   kl.setFont(F_KEY); kl.setForeground(C_TEXT_SEC);
            JLabel vl = new JLabel(value); vl.setFont(F_VAL); vl.setForeground(vc); // Retourné pour mise à jour live.
            row.add(kl, BorderLayout.WEST); row.add(vl, BorderLayout.EAST);
            add(row);
            JSeparator sep = new JSeparator();
            sep.setForeground(C_BORDER); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            add(sep);
            return vl;
        }

        /** Ajoute un espace vide au panneau */
        private void addSpacer(int h) {
            JPanel sp = new JPanel(); sp.setBackground(C_BASE);
            sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
            sp.setPreferredSize(new Dimension(0, h)); add(sp);
        }

        /** Ajoute un message au panneau */
        private void addMsg(String msg) {
            JLabel lbl = new JLabel(msg, SwingConstants.CENTER);
            lbl.setFont(F_SMALL); lbl.setForeground(C_TEXT_DIM);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            lbl.setBorder(new EmptyBorder(24, 0, 0, 0)); add(lbl);
        }

        /** Retourne le libellé correspondant au type de bâtiment */
        private static String labelType(TypeBatiment t) {
            return switch (t) {
                case USINE -> "Usine"; case FOREUSE -> "Foreuse";
                case STOCKAGE -> "Stockage"; case BATIMENT_MAITRE -> "Bâtiment maître"; case ROUTE -> "Route";
            };
        }
        /** Retourne le libellé correspondant à la direction */
        private static String labelDir(Direction d) {
            return switch (d) { case NORD -> "Nord ↑"; case SUD -> "Sud ↓"; case EST -> "Est →"; case OUEST -> "Ouest ←"; };
        }
    }




    // ═════════════════════════════════════════════════════════════════════
    // Barre de capacité
    // ═════════════════════════════════════════════════════════════════════
    /** Barre qui montre la capacité actuelle d'un bâtiment de stockage */
    private class CapacityBar extends JPanel {
        private float ratio; 
        private String label; 
        private Color bar;

        /** Constructeur */
        CapacityBar(int stock, int cap) {
            // Détermine la taille de la barre
            setPreferredSize(new Dimension(0, 28));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            // Couleur de fond
            setBackground(C_BASE);

            // Initialise l'état visuel.
            updateValues(stock, cap);
        }

        /** Met à jour la barre dynamiquement sans reconstruire le composant. */
        public void updateValues(int stock, int cap) {
            // Pourcentage (stock actuel / capacité max)
            float newRatio = cap > 0 ? Math.min(1f, (float)stock / cap) : 0f;
            // Message affiché au centre de la barre (ex: "3 / 10")
            String newLabel = stock + " / " + cap;
            // Couleur de la barre change en fonction du pourcentage: (devient + rouge moins il reste de place)
            //      rouge (>85%), ambre (50-85%), verte (<50%)
            Color newBar = newRatio > 0.85f ? C_RED : newRatio > 0.5f ? C_AMBER : C_GREEN;

            // Met à jour l'affichage uniquement si le ratio ou le label ont changé (pour éviter les repaint inutiles).
            if (newRatio != ratio || !newLabel.equals(label)) {
                this.ratio = newRatio;
                this.label = newLabel;
                this.bar = newBar;
                repaint();
            }
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g; hint(g2);
            // longueur et largeur de la barre, et longueur de la partie remplie de la barre en fonction du ratio
            int w = getWidth(), h = getHeight(), f = (int)(w * ratio);

            // arrière plan de la barre transparent
            g2.setColor(new Color(bar.getRed(), bar.getGreen(), bar.getBlue(), 40)); g2.fillRect(0, 0, w, h);
            // partie remplie de la barre avec une couleur plus opaque
            g2.setColor(new Color(bar.getRed(), bar.getGreen(), bar.getBlue(), 100)); g2.fillRect(0, 0, f, h);
            g2.setColor(bar); g2.fillRect(0, h-3, f, 3);

            // texte centré sur la barre
            g2.setFont(F_SMALL); g2.setColor(C_TEXT_SEC);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, (w - fm.stringWidth(label))/2, (h + fm.getAscent() - fm.getDescent())/2);
        }
    }





 
    // ═════════════════════════════════════════════════════════════════════
    // Panneau actions
    // ═════════════════════════════════════════════════════════════════════
    /** Panneau affichant les actions disponibles pour une case sélectionnée */
    private class ActionsPanel extends JPanel {
        private Case lastCase = null; // Suit l'état courant pour éviter le travail inutile.
        private Batiment lastBatiment = null;


        /** Constructeur */
        ActionsPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(C_BASE);
            setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, C_BORDER_LIT),
                new EmptyBorder(10, 12, 12, 12)));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g; hint(g2);
        }

        /** Met à jour les actions affichées en fonction de la case sélectionnée */
        void update(Case c) {
            // Optimisation: ne fait rien si la case n'a pas changé.
            Batiment currentBatiment = (c != null) ? c.getBatiment() : null;
            if (c == lastCase && currentBatiment == lastBatiment) return;
            
            // Si la case a changé, on reconstruit entièrement le panneau d'actions
            lastCase = c;
            lastBatiment = currentBatiment;
            removeAll();

            // Si aucune case n'est sélectionnée, on n'affiche rien.
            if (c == null) { revalidate(); repaint(); return; }

            // Le label contenant les actions
            JLabel hdr = new JLabel("ACTIONS"); 
            hdr.setFont(F_SECT); // Police d'écriture
            hdr.setForeground(C_TEXT_DIM); // Couleur du texte
            hdr.setBorder(new EmptyBorder(0, 0, 8, 0)); // Marge en dessous du label
            add(hdr); // On ajoute le label au panneau

            // Si la case est vide, on peut y construire un bâtiment (sauf une foreuse qui nécessite un minerai)
            // Si la case contient un minerai, on peut y construire n'importe quel bâtiment.
            if (c.estVide() || (c.aMinerai() && !c.aBatiment())) {
                if (c.getType() == TypeCase.MINERAI) {
                    addBtn("CONSTRUIRE FOREUSE", C_AMBER, () -> construireBatiment(c, TypeBatiment.FOREUSE));
                }
                addBtn("CONSTRUIRE STOCKAGE", C_GREEN, () -> construireBatiment(c, TypeBatiment.STOCKAGE));
                addBtn("CONSTRUIRE ROUTE", C_BLUE, () -> construireBatiment(c, TypeBatiment.ROUTE));
                // addBtn("CONSTRUIRE", C_GREEN, ()->{
                //     // TODO : ouvrir un menu de construction avec les différents types de bâtiments disponibles, vérifier la validité de la construction (ex: foreuse uniquement sur minerai) et afficher les coûts de construction.
                // });
            }

            // Si la case contient un bâtiment, les actions disponibles dépendent du type de bâtiment.
            // TODO : définir les actions réelles pour chaque type de bâtiment, actuellement ce sont des placeholders.
            if (c.aBatiment()) {
                switch (c.getBatiment().type()) {
                    case USINE           -> {
                        addBtn("AMÉLIORER",       C_BLUE, ()->{}); // pas prévu
                        addBtn("PAUSE",     C_BORDER_LIT, ()->{});
                    }
                    case FOREUSE         -> {
                        addBtn("RÉGLER VITESSE", C_AMBER, ()->{}); // pourquoi?
                        addBtn("PAUSE",     C_BORDER_LIT, ()->{}); // potentiellement mettre en pause pendant que les routes sont modifiées, mais déjà fait automatiquement lorsque la capacité est pleine, donc pas sûr que ce soit nécessaire.
                    }
                    case ROUTE           -> {
                        addBtn("INVERSER",        C_BLUE, ()->{}); // modifier la direction au lieu de seulement inverser?
                    }
                    case BATIMENT_MAITRE -> {
                        addBtn("INVENTAIRE",     C_GREEN, ()->{}); // sert à rien vu que c'est déjà affiché
                    }
                    case STOCKAGE -> { }
                }
                // Démolition disponible pour tous les bâtiments sauf le bâtiment maître (pour éviter les situations de blocage/défaite auto).
                if (c.getBatiment().type() != TypeBatiment.BATIMENT_MAITRE) {
                    addBtn("DÉMOLIR", C_RED, ()->{
                        // TODO : implémenter la démolition d'un bâtiment, vérifier les conditions de démolition.
                        // Pour chaque bâtiment pouvant contenir du minerai (stockage, bâtiment maître), le minerai qu'il contient est perdu à la démolition
                        c.detruireBatiment();
                    });
                }
            }
            revalidate(); 
            repaint();
        }



        /******** Méthodes pour la construction de nouveaux bâtiments ********/
        // TODO : ces méthodes sont actuellement appelées depuis des boutons dans le panneau d'actions, mais il faudrait 
        // peut-être les déplacer dans une classe de gestion de la logique de jeu pour séparer le modèle de l'affichage.

        /** Construit un bâtiment de type [type] sur une case donnée, puis rafraîchit l'affichage */
        private void construireBatiment(Case c, TypeBatiment type) {
            switch (type) {
                case USINE -> {}
                case FOREUSE -> c.construireForeuse(terrain);
                case STOCKAGE -> c.construireStockage(terrain);
                case BATIMENT_MAITRE -> { throw new IllegalArgumentException("Le bâtiment maître ne peut pas être construit manuellement."); }
                case ROUTE -> c.construireRoute(terrain, demanderDirectionRoute());
            }
            affichage.getAffichageTerrain().repaint();
            MenuPanel.this.refresh();
        }

        /** Demande à l'utilisateur la direction de la route qu'il souhaite construire */
        private Direction demanderDirectionRoute() {
            Direction[] options = {Direction.NORD, Direction.EST, Direction.SUD, Direction.OUEST};
            return (Direction) JOptionPane.showInputDialog(
                MenuPanel.this,
                "Choisissez la direction de la route :",
                "Construction route",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                Direction.NORD
            );
        }



 
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
 
