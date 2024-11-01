package TD;

import TD.GameMap;

import javax.swing.*;
import java.io.IOException;

public class TDGame {
    public TDGame() {
        JFrame frame = new JFrame("Tower Defense");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1550, 850);

        try {
            GameMap gameMap = new GameMap("PathMap1.txt");
            TDGamePanel panel = new TDGamePanel(gameMap);
            frame.add(panel);
            frame.setVisible(true);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}
