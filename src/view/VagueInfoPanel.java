package view;

import javax.swing.*;
import java.awt.*;
import model.GestionnaireVagues;

/**
 * Panneau qui affiche les informations sur les vagues d'ennemis.
 * Affiche le numéro de la prochaine vague et le temps restant.
 * Contient un bouton pour sauter l'attente de la prochaine vague.
 */
public class VagueInfoPanel extends JPanel {
    
    private final GestionnaireVagues gestionnaireVagues;
    private final JLabel infoLabel;
    private final JButton skipButton;
    
    /**
     * Crée un panneau d'information sur les vagues.
     * @param gestionnaireVagues le gestionnaire de vagues à surveiller
     */
    public VagueInfoPanel(GestionnaireVagues gestionnaireVagues, int width) {
        this.gestionnaireVagues = gestionnaireVagues;
                
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setPreferredSize(new Dimension(width, 40));
        setBackground(new Color(30, 30, 35));
        add(Box.createHorizontalGlue());

        // Label d'information
        infoLabel = new JLabel();
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        infoLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        add(infoLabel);
        
        add(Box.createRigidArea(new Dimension(15, 0))); 
        
        // Bouton pour sauter le temps
        skipButton = new JButton("⏭ Sauter le temps") {
            // anti-aliasing pour le texte du bouton
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        skipButton.setFont(new Font("Dialog", Font.PLAIN, 16));
        skipButton.setBackground(new Color(80, 80, 90));
        skipButton.setForeground(Color.WHITE);
        skipButton.setFocusPainted(false);
        skipButton.setBorderPainted(false);
        skipButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // highlight on hover
        skipButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                skipButton.setBackground(new Color(100, 100, 110));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                skipButton.setBackground(new Color(80, 80, 90));
            }
        });


        skipButton.addActionListener(e -> {
            gestionnaireVagues.declencherProchaineVague();
            majAffichage();
        });

        skipButton.setAlignmentY(Component.CENTER_ALIGNMENT); 
        add(skipButton);
        
        add(Box.createHorizontalGlue());

        // Mettre à jour l'affichage initialement
        majAffichage();
        
        // Démarrer un timer pour mettre à jour l'affichage chaque seconde
        new javax.swing.Timer(1000, e -> majAffichage()).start();
    }
    
    /**
     * Met à jour l'affichage du temps restant et du numéro de vague.
     */
    public void majAffichage() {
        if (gestionnaireVagues == null) return;
        
        int prochaineVague = gestionnaireVagues.getProchaineVague();
        int tempsRestant = gestionnaireVagues.getTempsRestantSecondes();
        
        // Formater le temps (MM:SS)
        int minutes = tempsRestant / 60;
        int secondes = tempsRestant % 60;
        String tempsFormate = String.format("%02d:%02d", minutes, secondes);
        
        infoLabel.setText("Prochaine vague : " + prochaineVague + " dans " + tempsFormate);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g2d);
    }
    
}
