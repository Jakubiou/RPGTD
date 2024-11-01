package TD;

import java.awt.*;

public class FastTower extends Tower {
    public FastTower(int x, int y) {
        super(x, y,100);
        this.damage = 1;
        this.fireRate = 200;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x - 10, y - 10, 20, 20);
    }
}