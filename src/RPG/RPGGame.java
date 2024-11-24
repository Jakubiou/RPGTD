package RPG;

import javax.swing.*;

public class RPGGame extends JFrame {
    private GamePanel gamePanel;
    private Player player;

    public RPGGame() {
        player = new Player(Player.PANEL_WIDTH, Player.PANEL_HEIGHT, 100);
        gamePanel = new GamePanel(this, player);
        add(gamePanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
