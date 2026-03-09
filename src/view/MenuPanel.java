package view;

import javax.swing.*;
import java.awt.*;

import model.*;

public class MenuPanel extends JPanel {
    private JLabel title;
    private JTextArea info;

    private int LARGEUR_MENU = (int)(Affichage.LARGEUR_GRILLE * Affichage.RATIO_LARGEUR_MENU);
    private int HAUTEUR_MENU = Affichage.HAUTEUR_GRILLE;
    public MenuPanel() {
        this.setPreferredSize(new Dimension(LARGEUR_MENU, HAUTEUR_MENU));

        setLayout(new BorderLayout());

        title = new JLabel("Informations", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        info = new JTextArea();
        info.setEditable(false);

        add(title, BorderLayout.NORTH);
        add(new JScrollPane(info), BorderLayout.CENTER);
    }

    public void updateCase(Case c) {
        if (c == null) {
            info.setText("No case selected.");
            return;
        }
        // TODO more detailed information about the case


        StringBuilder text = new StringBuilder();
        text.append("Position: (" + c.getX() + ", " + c.getY() + ")\n");
        text.append("Type: " + c.getType() + "\n");

        if (c.getBatiment() != null) {
            switch (c.getBatiment().type()) {
                case USINE:
                    text.append("Usine de production\n");
                    text.append("Stockage actuel: " + c.getBatiment().getStockage() + "\n");
                    break;
                case FOREUSE:
                    text.append("Foreuse d'extraction\n");
                    text.append("Stockage actuel: " + c.getBatiment().getStockage() + "\n");
                    break;
                case STOCKAGE:
                    text.append("Stockage de minerais\n");
                    text.append("Stockage actuel: " + c.getBatiment().getStockage() + "\n");
                    break;
                case BATIMENT_MAITRE:
                    text.append("Bâtiment maître\n");
                    text.append("Stockage actuel: " + c.getBatiment().getStockage() + "\n");
                    break;
                case ROUTE:
                    text.append("Route de transport\n");
                    text.append("Stockage actuel: " + c.getBatiment().getStockage() + "\n");
                    Batiment b = c.getBatiment();
                    if (b instanceof Route) {
                        Route route = (Route) c.getBatiment();
                        text.append("Direction: " + route.getDirection() + "\n");
                    }
                    break;
            }

        }

        if (c.getType() == TypeCase.MINERAI) {
            text.append("Minerai disponible\n");
        }

        info.setText(text.toString());
    }
}
