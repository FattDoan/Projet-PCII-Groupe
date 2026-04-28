package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import model.Case;

/** Classe qui contient les méthodes d'affichage des cases du terrain */
public class AffichageCases {

    /****** CONSTANTES *******/

    // Couleur d'une case vide, définie une fois pour toutes pour éviter de recréer un objet Color à chaque affichage
    private static final Color DARK_GREEN = new Color(34, 139, 34); // Couleur d'une case vide

    /**** IMAGES ****/
    // Cache pour stocker les images déjà chargées
    private static final HashMap<String, java.awt.Image> IMAGES_CACHE = new HashMap<>();

    // adresse de base pour les images, à laquelle on ajoute le nom de l'image spécifique pour chaque type de case (ex: gisement de minerai, foreuse, etc.)
    public static final String BASE_ADRESSE_IMAGES = FileSystems.getDefault().getPath("").toAbsolutePath().toString() + "/images/";



    /******* ADRESSES DES IMAGES *******/


    // Divers
    private static final String ADRESSE_MINERAL_DEPOSIT = BASE_ADRESSE_IMAGES + "sprite_gisement_minerai.png";
    private static final String ADRESSE_EN_CONSTRUCTION = BASE_ADRESSE_IMAGES + "sprite_en_construction.png";


    /****** FONCTIONS D'AFFICHAGE *******/

    /** Fonction de base à partir de laquelle on appelle toutes les autres fonctions d'affichage */
    public static void afficheCase(Graphics g, Case c) {
        afficheCaseVide(g, c);
        if (c.aMinerai()) {
            afficheImageCase(g, c, ADRESSE_MINERAL_DEPOSIT);
        }
        if (c.aBatiment()) {
            afficheImageBatiment(g, c);
        }
    }

    private static void afficheImageBatiment(Graphics g, Case c) {
        model.Batiment batiment = c.getBatiment();
        
        // Afficher le bâtiment normalement selon son type
        switch (batiment.getType()) {
            case BATIMENT_MAITRE:
                model.BatimentMaitre maitre = (model.BatimentMaitre) batiment;
                AffichageBatiments.afficheBatimentMaitre(g, c, maitre);
                break;
            case FOREUSE:
                model.Foreuse foreuse = (model.Foreuse) batiment;
                AffichageBatiments.afficheForeuse(g, c, foreuse);
                break;
            case STOCKAGE:
                model.Stockage stockage = (model.Stockage) batiment;
                AffichageBatiments.afficheStockage(g, c, stockage);
                break;
            case USINE:
                model.Usine usine = (model.Usine) c.getBatiment();
                AffichageBatiments.afficheUsine(g, c, usine);
                break;
            case ROUTE:
                model.Route route = (model.Route) c.getBatiment();
                AffichageBatiments.afficheRoute(g, c, route);
                break;
            default:
                System.err.println("Type de bâtiment inconnu: " + batiment.getType());
                return;
        }
        
        // Afficher l'indicateur d'en construction
        if (batiment.estEnConstruction()) {
            AffichageCases.afficheImageCase(g, c, ADRESSE_EN_CONSTRUCTION);
        }
    }

    /** Affiche une case vide à la position (x, y) sur la fenêtre */
    private static void afficheCaseVide(Graphics g, Case c) {
        int cellSize = Case.TAILLE;
        int x = c.getX() * cellSize; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * cellSize;

        // Choix visuel: vert foncé pour évoquer l'herbe.
        g.setColor(DARK_GREEN); // Couleur d'une case vide, ici vert pour faire de l'herbe
        g.fillRect(x, y, cellSize, cellSize); // Dessine un carré dans la case correspondante
    }

    /** Affiche l'image passée en paramètre à la position de la case */
    public static void afficheImageCase(Graphics g, Case c, String adress) {
        // Affichage l'image passée en paramètre à la position de la case, en redimensionnant l'image pour qu'elle remplisse toute la case
        int cellSize = Case.TAILLE;
        int x = c.getX() * cellSize; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * cellSize;

        java.awt.Image img;

        try {
            if (IMAGES_CACHE.containsKey(adress)) {
                // Récupère l'image depuis le cache si elle a déjà été chargée
                img = IMAGES_CACHE.get(adress);
            } else {
                // Chargement de l'image depuis le fichier
                img = javax.imageio.ImageIO.read(new java.io.File(adress));
                IMAGES_CACHE.put(adress, img); // Stocke l'image dans le cache pour les futurs affichages
            }
            g.drawImage(img, x, y, cellSize, cellSize, null); // Dessine l'image du minerai dans la case correspondante

        } catch (IOException e) {
            // Si l'image n'a pas pu être chargée, on n'affiche rien (la case restera vide) et on logue l'erreur pour pouvoir la corriger plus tard
            System.err.println("Erreur lors du chargement de l'image: " + adress + ". Vérifiez que le projet a été lancé depuis sa racine (Projet-PCII-Groupe) et que le dossier images est bien présent.");
            e.printStackTrace(System.err);
            afficheCaseDebug(g, c); // Affiche rien si marche pas
        }
    }

    /** Affiche une case magenta, utilisé si l'image n'a pas pu être chargée 
     * @param g le contexte graphique sur lequel dessiner
     * @param c la case sur laquelle dessiner le debug
    */
    public static void afficheCaseDebug(Graphics g, Case c) {
        int cellSize = Case.TAILLE;
        int x = c.getX() * cellSize; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * cellSize;

        g.setColor(Color.MAGENTA);
        g.drawRect(x, y, cellSize, cellSize); // Dessine un carré magenta autour de la case pour le debug
    }

}
