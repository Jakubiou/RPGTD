package RPG;

import javax.swing.*;
import java.awt.*;

public class AbilityPanel extends JPanel {
    private Player player;
    private JLabel coinsLabel;
    private JButton damageButton, hpButton, defenseButton;

    public AbilityPanel(Player player) {
        this.player = player;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new GridLayout(4, 1));
        setBackground(Color.DARK_GRAY);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        coinsLabel = new JLabel("Coins: " + player.getCoins(), JLabel.CENTER);
        coinsLabel.setForeground(Color.WHITE);
        coinsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(coinsLabel);

        damageButton = createUpgradeButton("Damage");
        hpButton = createUpgradeButton("HP");
        defenseButton = createUpgradeButton("Defense");

        add(damageButton);
        add(hpButton);
        add(defenseButton);
    }

    private JButton createUpgradeButton(String statName) {
        JButton button = new JButton() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                String text = String.format("%s +%nCurrent: %d", statName, getCurrentStatValue(statName));
                drawCenteredString(g, text, getWidth(), getHeight());
            }
        };
        styleAbilityButton(button);
        return button;
    }

    private void drawCenteredString(Graphics g, String text, int width, int height) {
        FontMetrics metrics = g.getFontMetrics();
        String[] lines = text.split("\n");
        int y = (height - metrics.getHeight() * lines.length) / 2 + metrics.getAscent();
        for (String line : lines) {
            int x = (width - metrics.stringWidth(line)) / 2;
            g.drawString(line, x, y);
            y += metrics.getHeight();
        }
    }

    private int getCurrentStatValue(String statName) {
        switch (statName) {
            case "Damage":
                return player.getDamage();
            case "HP":
                return player.getHp();
            case "Defense":
                return player.getDefense();
            default:
                return 0;
        }
    }

    private void styleAbilityButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setFocusPainted(false);
    }

    public void updateCoins() {
        coinsLabel.setText("Coins: " + player.getCoins());
    }
}
