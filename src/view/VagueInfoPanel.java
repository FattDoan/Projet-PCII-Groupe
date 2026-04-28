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
        
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        setPreferredSize(new Dimension(width, 40));
        setBackground(new Color(30, 30, 35));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Label d'information
        infoLabel = new JLabel();
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(infoLabel);
        
        // Bouton pour sauter le temps
        skipButton = new JButton("⏭ Sauter le temps");
        skipButton.setFont(new Font("Arial", Font.PLAIN, 14));
        skipButton.setBackground(new Color(80, 80, 90));
        skipButton.setForeground(Color.WHITE);
        skipButton.setFocusPainted(false);
        skipButton.setBorderPainted(false);
        skipButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        skipButton.addActionListener(e -> {
            gestionnaireVagues.declencherProchaineVague();
            majAffichage();
        });
        
        add(skipButton);
        
        // Mettre à jour l'affichage initialement
        majAffichage();
        
        // Démarrer un timer pour mettre à jour l'affichage chaque seconde
        new java.util.Timer("VagueInfoPanel-Refresh", true).scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                majAffichage();
            }
        }, 0, 1000);
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
}
