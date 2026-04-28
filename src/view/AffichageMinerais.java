package view;

import java.awt.*;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import javax.imageio.ImageIO;
import model.Minerai;
import model.Case;

/**
 * Classe qui contient les méthodes d'affichage des minerais en transit sur les routes.
 */
public class AffichageMinerais {
    
    /** Taille d'affichage d'un minerai (en pixels) */
    public static final int TAILLE_MINERAI = Case.TAILLE / 2; // Plus petit
    
    // Cache pour stocker les images déjà chargées
    private static final HashMap<String, java.awt.Image> IMAGES_CACHE = new HashMap<>();
    
    // Adresse de base pour les images
    public static final String BASE_ADRESSE_IMAGES = FileSystems.getDefault().getPath("").toAbsolutePath().toString() + "/images/";
    
    // Adresse de l'image du minerai en transit
    private static final String ADRESSE_MINERAI_TRANSPORT = BASE_ADRESSE_IMAGES + "sprite_crystal_centre.png";
    
    /**
     * Affiche un minerai en transit sur le terrain.
     * @param g le contexte graphique
     * @param minerai le minerai à afficher
     */
    public static void afficheMinerai(Graphics g, Minerai minerai) {
        float px = minerai.getPX();
        float py = minerai.getPY();
        float progression = minerai.getProgression();
        
        Graphics2D g2 = (Graphics2D) g;
        
        // Sauvegarder l'anti-aliasing actuel
        Object oldAntiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Calculer la position pour dessiner l'image (centrée sur px, py)
        int x = Math.round(px - TAILLE_MINERAI / 2.0f);
        int y = Math.round(py - TAILLE_MINERAI / 2.0f);
        
        try {
            // Charger ou récupérer l'image du minerai depuis le cache
            java.awt.Image img;
            if (IMAGES_CACHE.containsKey(ADRESSE_MINERAI_TRANSPORT)) {
                img = IMAGES_CACHE.get(ADRESSE_MINERAI_TRANSPORT);
            } else {
                img = ImageIO.read(new java.io.File(ADRESSE_MINERAI_TRANSPORT));
                IMAGES_CACHE.put(ADRESSE_MINERAI_TRANSPORT, img);
            }
            
            // Dessiner l'image du minerai
            if (img != null) {
                g2.drawImage(img, x, y, TAILLE_MINERAI, TAILLE_MINERAI, null);
            } else {
                // Fallback : dessiner un cercle jaune si l'image n'est pas chargée
                g2.setColor(new Color(255, 215, 0));
                g2.fillOval(x, y, TAILLE_MINERAI, TAILLE_MINERAI);
            }
        } catch (IOException | NullPointerException e) {
            // Si l'image n'a pas pu être chargée, dessiner un cercle jaune
            System.err.println("Erreur lors du chargement de l'image pour les minerais en transit: " + ADRESSE_MINERAI_TRANSPORT);
            g2.setColor(new Color(255, 215, 0));
            g2.fillOval(x, y, TAILLE_MINERAI, TAILLE_MINERAI);
        }
        

        
        // Restaurer l'anti-aliasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntiAlias);
    }
    
    /**
     * Affiche une barre de progression circulaire autour du minerai
     * pour indiquer l'état du déplacement.
     */
    private static void afficherBarreProgression(Graphics2D g2, float centerX, float centerY, int size, float progression) {
        int radius = size / 2 + 4; // Rayon légèrement plus grand que l'image
        int diameter = radius * 2;
        
        // Dessiner l'arrière-plan de la barre (arc gris)
        g2.setColor(new Color(60, 60, 60, 180));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(
            Math.round(centerX - radius),
            Math.round(centerY - radius),
            diameter,
            diameter,
            0,
            360
        );
        
        // Dessiner l'arc de progression (arc orange/jaune)
        int arcAngle = Math.round(progression * 360);
        g2.setColor(new Color(255, 200, 50, 200));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(
            Math.round(centerX - radius),
            Math.round(centerY - radius),
            diameter,
            diameter,
            -90, // Commencer à 12h (haut)
            arcAngle
        );
    }
    
    /**
     * Affiche tous les minerais en transit sur le terrain.
     * @param g le contexte graphique
     * @param minerais la liste des minerais à afficher
     */
    public static void afficheTousMinerais(Graphics g, java.util.List<Minerai> minerais) {
        for (Minerai minerai : minerais) {
            afficheMinerai(g, minerai);
        }
    }
}
