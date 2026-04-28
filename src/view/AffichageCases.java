package view;

import java.awt.Graphics;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.awt.Color;
import model.Case;
import model.TypeBatiment;

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
    private static final String ADRESSE_MINERAL_INGOT   = BASE_ADRESSE_IMAGES + "sprite_crystal_terne.png";
    private static final String ADRESSE_EN_CONSTRUCTION = BASE_ADRESSE_IMAGES + "sprite_en_construction.png";


    private static final String ADRESSE_FOREUSE         = BASE_ADRESSE_IMAGES + "drill_sprite.png";
    private static final String ADRESSE_MAITRE          = BASE_ADRESSE_IMAGES + "mineral_ingot_raw.jpg";
    private static final String ADRESSE_USINE           = BASE_ADRESSE_IMAGES + "fhuifsberlukfberufkse(placeholder).png";



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
        switch (c.getBatiment().getType()) {
            case BATIMENT_MAITRE:
                afficheImageCase(g, c, ADRESSE_MAITRE);
                break;
            case FOREUSE:
                model.Foreuse foreuse = (model.Foreuse) c.getBatiment();
                AffichageBatiments.afficheForeuse(g, c, foreuse);
                break;
            case STOCKAGE:
                model.Stockage stockage = (model.Stockage) c.getBatiment();
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
                System.err.println("Type de bâtiment inconnu: " + c.getBatiment().getType());
                return; // Si le type de bâtiment est inconnu, on n'affiche rien et on logue l'erreur pour pouvoir la corriger plus tard
        }
        if (c.getBatiment().estEnConstruction()) {
            AffichageCases.afficheImageCase(g, c, ADRESSE_EN_CONSTRUCTION); // Affiche une image d'en construction par dessus le bâtiment pour indiquer qu'il n'est pas encore fini
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

    /** Affiche un gisement de minerai (/!\ PAS UN MINERAI QUI SE DEPLACE SUR UNE ROUTE/!\) à la position (x, y) sur la fenêtre */
    private static void _afficheMinerai(Graphics g, Case c) {
        int cellSize = Case.TAILLE;
        int x = c.getX() * cellSize; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * cellSize;

        g.setColor(java.awt.Color.BLUE); // Couleur du minerai
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
            _AffichageBatiments.afficheBatiment(g, c); // Affichage de base du bâtiment si l'image n'a pas pu être chargée
        }
    }

    public static void afficheMineralIngot(Graphics g, Case c) { 
        int cellSize = Case.TAILLE;
        int N = cellSize / 3;
        int x = c.getX() * cellSize; // conversion des coordonnées de la case en pixels pour l'affichage
        int y = c.getY() * cellSize;

        java.awt.Image img;

        try {
            if (IMAGES_CACHE.containsKey(ADRESSE_MINERAL_INGOT)) {
                // Récupère l'image depuis le cache si elle a déjà été chargée
                img = IMAGES_CACHE.get(ADRESSE_MINERAL_INGOT);
            } else {
                // Chargement de l'image depuis le fichier
                img = javax.imageio.ImageIO.read(new java.io.File(ADRESSE_MINERAL_INGOT));
                IMAGES_CACHE.put(ADRESSE_MINERAL_INGOT, img); // Stocke l'image dans le cache pour les futurs affichages
            }
            g.drawImage(img, x + N, y + N, cellSize/3, cellSize/3, null); // Dessine l'image du minerai dans la case correspondante

        } catch (IOException e) {
            // Si l'image n'a pas pu être chargée, on n'affiche rien (la case restera vide) et on logue l'erreur pour pouvoir la corriger plus tard
            System.err.println("Erreur lors du chargement de l'image: " + ADRESSE_MINERAL_INGOT + ". Vérifiez que le projet a été lancé depuis sa racine (Projet-PCII-Groupe) et que le dossier images est bien présent.");
            e.printStackTrace(System.err);
            _AffichageBatiments.afficheMinerai(g, c); // Affichage de base du bâtiment si l'image n'a pas pu être chargée
        }
    }

}
