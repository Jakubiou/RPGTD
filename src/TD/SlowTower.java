package TD;

import java.awt.*;

public class SlowTower extends Tower {
    public SlowTower(int x, int y) {
        super(x, y,200);
        this.damage = 50;
        this.fireRate = 1000;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x - 10, y - 10, 20, 20);
    }
}