package TD;

import java.awt.*;

public class NormalTower extends Tower {
    public NormalTower(int x, int y) {
        super(x, y,1500);
        this.damage = 200;
        this.fireRate = 50;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x - 10, y - 10, 20, 20);
    }
}