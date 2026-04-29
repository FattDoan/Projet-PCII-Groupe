package model.unite.commande;

import model.*;
import model.unite.Unite;
import java.util.Set;

public class CommandeConstruire extends Commande {
    private static final int TEMPS = 500;
    private int progression = 0; // nombre de ticks déjà passés à miner le minerai actuel
    private float hpParTick = 0; // nombre de points de vie ajoutés à chaque tick de construction, 
                                 // calculé en fonction du nombre de matériaux nécessaires
    private final Batiment batiment; // le bâtiment à construire
    private final Terrain terrain;


    private float getHPParTick(TypeBatiment type) {
        int quantity = switch (type) {
            case USINE -> Usine.COUT_CONSTRUCTION;
            case STOCKAGE -> Stockage.COUT_CONSTRUCTION;
            case ROUTE -> Route.COUT_CONSTRUCTION;
            case FOREUSE -> Foreuse.COUT_CONSTRUCTION;
            case BATIMENT_MAITRE -> BatimentMaitre.COUT_CONSTRUCTION;
            default -> throw new IllegalArgumentException("Type de bâtiment inconnu: " + type);
        };

        return (float)batiment.getHPMax() / quantity;
    }

    public CommandeConstruire(Batiment batiment, Terrain terrain) {
        this.batiment = batiment;
        this.hpParTick = getHPParTick(batiment.getType());
        this.terrain = terrain;
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float)Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private Batiment scanForDamagedBuilding(Unite unite) {
        // get the closest damaged building in the terrain
        Batiment closest = null;
        float closestDist = Float.MAX_VALUE;
        Set<Batiment> batiments = terrain.getBatimentsEndommages();
        for (Batiment b : batiments) {
            float dist = distance(unite.getPX(), unite.getPY(), 
                                  b.getX() * Case.TAILLE + Case.TAILLE/2, 
                                  b.getY() * Case.TAILLE + Case.TAILLE/2);
            if (closest == null || dist < closestDist) {
                closest = b;
                closestDist = dist;
            }
        }
        return closest;
    }

    @Override
    public boolean executer(Unite unite, double dt) {
        // If the unit doesnt have enough material to build, 
        // go back to nearest the building to get more
        if (unite.getStockage() == 0) {
            Batiment nearest = terrain.getNearestBatimentStockage(unite.getGX(), unite.getGY());
            if (nearest != null) {
                float targetPX = nearest.getX() * Case.TAILLE + Case.TAILLE/2;
                float targetPY = nearest.getY() * Case.TAILLE + Case.TAILLE/2;
                unite.ajouterCommande(new CommandeDeplacement(targetPX, targetPY)); // retourne au bâtiment pour se réapprovisionner
                unite.ajouterCommande(new CommandeRetrieve(nearest)); // ajoute une commande de récupération après le déplacement
                unite.ajouterCommande(new CommandeDeplacement(batiment.getX() * Case.TAILLE + Case.TAILLE/2, 
                                                            batiment.getY() * Case.TAILLE + Case.TAILLE/2)); // retourne sur le chantier après le réapprovisionnement
                unite.ajouterCommande(new CommandeConstruire(batiment, terrain)); // ajoute une commande de construction après le réapprovisionnement
                return true;
            }
            return false; // pas de bâtiment trouvé, on attend
        }

        if (batiment.atFullHP()) {
            Batiment nextTarget = scanForDamagedBuilding(unite); 
            if (nextTarget != null) {
                // si il y a un autre bâtiment endommagé, change la cible de construction pour celui-ci
                float targetPX = nextTarget.getX() * Case.TAILLE + Case.TAILLE/2;
                float targetPY = nextTarget.getY() * Case.TAILLE + Case.TAILLE/2;
                unite.ajouterCommande(new CommandeDeplacement(targetPX, targetPY));
                unite.ajouterCommande(new CommandeConstruire(nextTarget, terrain));
            }
            return true; // déjà plein, on considère la commande terminée
        }
 
        
        if (progression < TEMPS) {
            progression += dt;
            return false; // pas encore terminé
        }

        if (batiment.ajouterHP(hpParTick)) { // ajoute des points de vie au bâtiment en construction
            unite.retirerStockage(); // retire un matériau de l'unité pour la construction
        }
        progression = 0; // reset pour le prochain tick de construction
        return false; // terminé
    }

    @Override
    public String getNom() {
        return "Construire";
    }
}
