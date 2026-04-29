package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

import model.Terrain;
import model.GestionnaireVagues;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.BorderLayout;

/** La fenêtre principale de l'application */
public class Fenetre extends JFrame {
    private final Affichage affichage;    // pour afficher la grille de jeu
    
    private final Terrain terrain;          // le terrain de jeu affiché dans la fenêtre
    private boolean gameOver = false;
    private VagueInfoPanel vagueInfoPanel;
    
    /** Constructeur de la fenêtre principale. Génère un affichage de la grille et du menu à partir du terrain donné.
     * @param titre le titre de la fenêtre
     * @param terrain le terrain à afficher dans la fenêtre
     */
    public Fenetre(String titre, Terrain terrain) {
        super(titre);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Add font anti-aliasing for smoother text rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        this.terrain = terrain;
        
        // Utiliser un BorderLayout pour organiser les composants
        setLayout(new BorderLayout());
        
        affichage = new Affichage(terrain);
        this.add(affichage, BorderLayout.CENTER);

        // TODO : ajouter ici les autres elements de fenetre (barre d'outils,
        // raccourcis clavier globaux, etc.) selon les besoins produit.

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        // Si la fenetre est redimensionnee, on met a jour les composants
        // pour s'adapter a la nouvelle taille (notamment MenuPanel)
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                affichage.revalidate();
                affichage.repaint();
            }
        });
    }
    
    /**
     * Définit le gestionnaire de vagues pour afficher le compteur.
     * Doit être appelé après la création de la fenêtre.
     */
    public void setGestionnaireVagues(GestionnaireVagues gestionnaire) {
        if (vagueInfoPanel != null) {
            remove(vagueInfoPanel);
        }
        vagueInfoPanel = new VagueInfoPanel(gestionnaire, getWidth());
        add(vagueInfoPanel, BorderLayout.NORTH);
        revalidate();
        repaint();
    }



    public Affichage getAffichage() {
        return this.affichage;
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    /**
     * Affiche un écran de Game Over par-dessus le jeu.
     */
    public void afficherEcranGameOver() {
        if (gameOver) {
            return; // Déjà affiché
        }
        
        gameOver = true;
        
        // Configurer le GlassPane pour afficher l'écran Game Over
        JPanel glassPane = (JPanel) this.getGlassPane();
        glassPane.setVisible(true);
        glassPane.setLayout(new BorderLayout());
        
        // Créer le panneau Game Over
        JPanel gameOverPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Fond rouge semi-transparent
                g.setColor(new Color(180, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gameOverPanel.setOpaque(false);
        
        // Ajouter les labels
        JLabel gameOverLabel = new JLabel("GAME OVER", JLabel.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 48));
        gameOverLabel.setForeground(Color.WHITE);
        
        JLabel messageLabel = new JLabel("Le bâtiment maître a été détruit !", JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        messageLabel.setForeground(Color.WHITE);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(gameOverLabel, BorderLayout.CENTER);
        textPanel.add(messageLabel, BorderLayout.SOUTH);
        
        gameOverPanel.add(textPanel, BorderLayout.CENTER);
        glassPane.add(gameOverPanel, BorderLayout.CENTER);
        
        // Rafraîchir l'affichage
        this.revalidate();
        this.repaint();
    }

    /**
     * Vérifie si le jeu est en état de Game Over.
     */
    public boolean isGameOver() {
        return gameOver;
    }
}
