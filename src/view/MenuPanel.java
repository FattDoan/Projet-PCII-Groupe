package view;

import javax.swing.*;
import java.awt.*;

import model.Case;
import model.TypeCase;

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

        StringBuilder text = new StringBuilder();
        text.append("Position: (" + c.getX() + ", " + c.getY() + ")\n");
        text.append("Type: " + c.getType() + "\n");

        if (c.getBatiment() != null) {
            text.append("Batiment: " + c.getBatiment().getClass().getSimpleName() + "\n");
        }

        if (c.getType() == TypeCase.MINERAI) {
            text.append("Minerai disponible\n");
        }

        info.setText(text.toString());
    }
}
