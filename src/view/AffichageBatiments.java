package view;

import java.awt.Graphics;
import model.*;

/** Classe qui contient toutes les méthodes d'affichage des bâtiments */
public class AffichageBatiments {

    /** Adresse de base pour les images, récupérée depuis AffichageCases */
    private static final String BASE_ADRESSE_IMAGES = AffichageCases.BASE_ADRESSE_IMAGES;

    /** Images de base pour les bâtiments */

    private static final String[] ADRESSES_ROUTE = {
        BASE_ADRESSE_IMAGES + "sprite_batiment_route_nord.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_route_sud.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_route_est.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_route_ouest.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_route_destroyed_h.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_route_destroyed_v.png"
    };
    private static final String[] ADRESSES_MAITRE = {
        BASE_ADRESSE_IMAGES + "sprite_batiment_maitre_vide.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_maitre_moitie.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_maitre_plein.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_maitre_destroyed.png"
    };
    private static final String[] ADRESSES_STOCKAGE = {
        BASE_ADRESSE_IMAGES + "sprite_batiment_silot_vide.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_silot_moitie.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_silot_plein.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_silot_destroyed.png"
    };
    private static final String[] ADRESSES_FOREUSE = {
        BASE_ADRESSE_IMAGES + "sprite_batiment_foreuse.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_foreuse_working.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_foreuse_destroyed.png"
    };
    private static final String[] ADRESSES_USINE = {
        BASE_ADRESSE_IMAGES + "sprite_batiment_usine.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_usine_working.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_usine_destroyed.png"
    };


    /** Images supplémentaires pour les dégats (à ajouter en plus de l'image de base) */

    private static final String[] ADRESSES_ROUTE_DAMAGED = {
        BASE_ADRESSE_IMAGES + "sprite_batiment_route_damaged1_h.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_route_damaged2_h.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_route_damaged1_v.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_route_damaged2_v.png"
    };
    private static final String[] ADRESSES_MAITRE_DAMAGED = {
        BASE_ADRESSE_IMAGES + "sprite_batiment_maitre_damaged1.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_maitre_damaged2.png"
    };
    private static final String[] ADRESSES_STOCKAGE_DAMAGED = {
        BASE_ADRESSE_IMAGES + "sprite_batiment_silot_damaged1.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_silot_damaged2.png"
    };
    private static final String[] ADRESSES_FOREUSE_DAMAGED = {
        BASE_ADRESSE_IMAGES + "sprite_batiment_foreuse_damaged1.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_foreuse_damaged2.png"
    };
    private static final String[] ADRESSES_USINE_DAMAGED = {
        BASE_ADRESSE_IMAGES + "sprite_batiment_usine_damaged1.png",
        BASE_ADRESSE_IMAGES + "sprite_batiment_usine_damaged2.png"
    };


    /****** FONCTIONS ******/


    /** Affiche le batiment maitre passée en argument sur la case c 
     * @param g le contexte graphique sur lequel dessiner
     * @param c la case sur laquelle dessiner le stockage
     * @param maitre le batiment maitre à afficher, qui contient les informations nécessaires pour déterminer l'image à afficher (niveau de remplissage, dégats, etc)
    */
    public static void afficheBatimentMaitre(Graphics g, Case c, BatimentMaitre maitre) {
        if (maitre.estDetruit()) {
            AffichageCases.afficheImageCase(g, c, ADRESSES_MAITRE[3]);
            return;
        }

        String imageName;
        // Affichage de base du stockage : l'image représente visuellement le niveau de remplissage du stockage
        if (maitre.estVide()) {
            imageName = ADRESSES_MAITRE[0]; // Vide
        } else if (maitre.estPlein()) {
            imageName = ADRESSES_MAITRE[2]; // Plein
        } else {
            imageName = ADRESSES_MAITRE[1]; // Cas de base
        }
        AffichageCases.afficheImageCase(g, c, imageName);

        if (!maitre.atFullHP()) {
            // Si le batiment a prit des dégâts, on affiche une image endommagée par dessus l'image de base pour représenter les dégâts
            float hpRatio = (float) maitre.getHP() / maitre.getHPMax();
            int id = hpRatio > 0.5f ? 0 : 1; // On détermine le niveau de dégâts (léger ou lourd) en fonction du ratio de points de vie restants
            AffichageCases.afficheImageCase(g, c, ADRESSES_MAITRE_DAMAGED[id]);
        }
    }


    /** Affiche le stockage passée en argument sur la case c 
     * @param g le contexte graphique sur lequel dessiner
     * @param c la case sur laquelle dessiner le stockage
     * @param stockage le stockage à afficher, qui contient les informations nécessaires pour déterminer l'image à afficher (niveau de remplissage, dégats, etc)
    */
    public static void afficheStockage(Graphics g, Case c, Stockage stockage) {
        if (stockage.estDetruit()) {
            AffichageCases.afficheImageCase(g, c, ADRESSES_STOCKAGE[3]);
            return;
        }

        String imageName;
        // Affichage de base du stockage : l'image représente visuellement le niveau de remplissage du stockage
        if (stockage.estVide()) {
            imageName = ADRESSES_STOCKAGE[0]; // Stockage vide
        } else if (stockage.estPlein()) {
            imageName = ADRESSES_STOCKAGE[2]; // Stockage plein
        } else {
            imageName = ADRESSES_STOCKAGE[1]; // Cas de base
        }
        AffichageCases.afficheImageCase(g, c, imageName);

        if (!stockage.atFullHP()) {
            // Si le stockage a prit des dégâts, on affiche une image de stockage endommagée par dessus l'image de base du stockage pour représenter les dégâts
            float hpRatio = (float) stockage.getHP() / stockage.getHPMax();
            int id = hpRatio > 0.5f ? 0 : 1; // On détermine le niveau de dégâts (léger ou lourd) en fonction du ratio de points de vie restants
            AffichageCases.afficheImageCase(g, c, ADRESSES_STOCKAGE_DAMAGED[id]);
        }
    }


    public static void afficheUsine(Graphics g, Case c, Usine usine) {
        if (usine.estDetruit()) {
            AffichageCases.afficheImageCase(g, c, ADRESSES_USINE[2]);
            return;
        }

        String imageName;
        // Affichage de base du stockage : l'image représente visuellement le niveau de remplissage du stockage
        if (usine.isRunning()) {
            imageName = ADRESSES_USINE[1]; // Usine en marche (extrait du minerai)
        } else {
            imageName = ADRESSES_USINE[0]; // Usine à l'arrêt (soit parce qu'elle est pleine, soit parce qu'elle n'est pas en état de fonctionner)
        }
        AffichageCases.afficheImageCase(g, c, imageName);

        if (!usine.atFullHP()) {
            // Si l'usine a prit des dégâts, on affiche une image d'usine endommagée par dessus l'image de base de l'usine pour représenter les dégâts
            float hpRatio = (float) usine.getHP() / usine.getHPMax();
            int id = hpRatio > 0.5f ? 0 : 1; // On détermine le niveau de dégâts (léger ou lourd) en fonction du ratio de points de vie restants
            AffichageCases.afficheImageCase(g, c, ADRESSES_USINE_DAMAGED[id]);
        }
    }


    /** Affiche la foreuse passée en argument sur la case c 
     * @param g le contexte graphique sur lequel dessiner
     * @param c la case sur laquelle dessiner la foreuse
     * @param foreuse la foreuse à afficher, qui contient les informations nécessaires pour déterminer l'image à afficher (niveau de remplissage, dégats, etc)
    */
    public static void afficheForeuse(Graphics g, Case c, Foreuse foreuse) {
        if (foreuse.estDetruit()) {
            AffichageCases.afficheImageCase(g, c, ADRESSES_FOREUSE[2]);
            return;
        }

        String imageName;
        // Affichage de base du stockage : l'image représente visuellement le niveau de remplissage du stockage
        if (foreuse.isRunning()) {
            imageName = ADRESSES_FOREUSE[1]; // Foreuse en marche (extrait du minerai)
        } else {
            imageName = ADRESSES_FOREUSE[0]; // Foreuse à l'arrêt (soit parce qu'elle est pleine, soit parce qu'elle n'est pas en état de fonctionner)
        }
        AffichageCases.afficheImageCase(g, c, imageName);

        if (!foreuse.atFullHP()) {
            // Si la foreuse a prit des dégâts, on affiche une image de foreuse endommagée par dessus l'image de base de la foreuse pour représenter les dégâts
            float hpRatio = (float) foreuse.getHP() / foreuse.getHPMax();
            int id = hpRatio > 0.5f ? 0 : 1; // On détermine le niveau de dégâts (léger ou lourd) en fonction du ratio de points de vie restants
            AffichageCases.afficheImageCase(g, c, ADRESSES_FOREUSE_DAMAGED[id]);
        }
    }


    /** Affiche la route passée en argument sur la case c 
     * @param g le contexte graphique sur lequel dessiner
     * @param c la case sur laquelle dessiner la route
     * @param route la route à afficher, qui contient les informations nécessaires pour déterminer l'image à afficher (orientation, dégats, présence de minerai en transit, etc)
    */
    public static void afficheRoute(Graphics g, Case c, Route route) {
        String imageName;

        if (route.estDetruit()) {
            // Si la route est détruite, on affiche une image de route détruite à la place de l'image de base de la route pour représenter la destruction
            if (route.getDirection() == model.Direction.NORD || route.getDirection() == model.Direction.SUD) {
                imageName = ADRESSES_ROUTE[4] ; // Image de route détruite pour les routes verticales
            } else {
                imageName = ADRESSES_ROUTE[5] ; // Image de route détruite pour les routes horizontales
            }
            AffichageCases.afficheImageCase(g, c, imageName);
            return; // Si la route est détruite, on n'affiche pas les autres éléments (dégats, minerai en transit) pour éviter les incohérences visuelles (ex: route détruite mais avec un minerai en transit dessus)
        }

        // On affiche la route de base avec la bonne orientation
        AffichageCases.afficheImageCase(g, c, ADRESSES_ROUTE[route.getDirection().toInt()]);

        if (!route.atFullHP()) {
            // Si la route a prit des dégâts, on affiche une image de route endommagée par dessus l'image de base de la route pour représenter les dégâts
            float hpRatio = (float) route.getHP() / route.getHPMax();
            int id = hpRatio > 0.5f ? 0 : 1; // On détermine le niveau de dégâts (léger ou lourd) en fonction du ratio de points de vie restants
            if (route.getDirection() == model.Direction.NORD || route.getDirection() == model.Direction.SUD) {
                imageName = ADRESSES_ROUTE_DAMAGED[id + 2] ; // Images de dégats pour les routes verticales
            } else {
                imageName = ADRESSES_ROUTE_DAMAGED[id] ; // Images de dégats pour les routes horizontales
            }
            AffichageCases.afficheImageCase(g, c, imageName);
        }


    }

}
