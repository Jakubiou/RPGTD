package RPG;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class AbilityPanel extends JPanel {
    private final Player player;
    private final GamePanel gamePanel;

    public AbilityPanel(GamePanel gamePanel,Player player) {
        this.gamePanel = gamePanel;
        this.player = player;
        initializeAbilityPanel();
    }

    private void initializeAbilityPanel() {
        setLayout(new GridLayout(3, 1));

        int panelWidth = 400;
        int panelHeight = 300;
        setBounds((GamePanel.PANEL_WIDTH - panelWidth) / 2, (GamePanel.PANEL_HEIGHT - panelHeight) / 2, panelWidth, panelHeight);

        setBackground(Color.DARK_GRAY);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

        JLabel coinsLabel = new JLabel("Coins: " + player.getCoins(), JLabel.CENTER);
        coinsLabel.setForeground(Color.WHITE);
        coinsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(coinsLabel);

        JButton doubleShotButton = createUpgradeButton("Double Shot");
        JButton forwardBackwardShotButton = createUpgradeButton("Backward Shot");

        doubleShotButton.addActionListener(e -> upgradeStat("double shot", 10));
        forwardBackwardShotButton.addActionListener(e -> upgradeStat("backward shot", 10));

        add(doubleShotButton);
        add(forwardBackwardShotButton);

        setVisible(false);
    }

    private JButton createUpgradeButton(String statName) {
        JButton button = new JButton() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                String text = String.format(statName);
                drawCenteredString(g, text, getWidth(), getHeight());
            }
        };
        styleAbilityButton(button);
        return button;
    }

    private void drawCenteredString(Graphics g, String text, int width, int height) {
        FontMetrics metrics = g.getFontMetrics();
        String[] lines = text.split("\\n");
        int y = (height - metrics.getHeight() * lines.length) / 2 + metrics.getAscent();
        for (String line : lines) {
            int x = (width - metrics.stringWidth(line)) / 2;
            g.drawString(line, x, y);
            y += metrics.getHeight();
        }
    }


    private void styleAbilityButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setFocusPainted(false);
    }

    private void upgradeStat(String stat, int cost) {
        if (player.getCoins() >= cost) {
            player.setCoins(player.getCoins() - cost);
            switch (stat.toLowerCase()) {
                case "double shot":
                    player.setDoubleShotActive(true);
                    break;
                case "backward shot":
                    player.setForwardBackwardShotActive(true);
                    break;
                default:
                    System.err.println("Unknown stat: " + stat);
            }
            updateAbilityPanel();
        } else {
            System.out.println("Not enough coins!");
        }
    }

    public void updateAbilityPanel() {
        ((JLabel) getComponent(0)).setText("Coins: " + player.getCoins());
        repaint();
    }
    public void showPanel() {
        setVisible(true);
        updateAbilityPanel();
    }

    public void hidePanel() {
        setVisible(false);
    }
}