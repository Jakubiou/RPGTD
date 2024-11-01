package TD;

import java.awt.*;

public class Bullet {
    private int startX, startY;
    private double currentX, currentY;
    private double speed = 25;
    private int damage;
    TDEnemy target;
    private boolean isToRemove = false;

    public Bullet(int startX, int startY, TDEnemy target, int damage) {
        this.startX = startX;
        this.startY = startY;
        this.currentX = startX;
        this.currentY = startY;
        this.target = target;
        this.damage = damage;
    }

    public void update() {
        if (target != null) {
            double angle = Math.atan2(target.getY() - currentY, target.getX() - currentX);
            currentX += speed * Math.cos(angle);
            currentY += speed * Math.sin(angle);

            double distance = Math.hypot(currentX - target.getX(), currentY - target.getY());
            if (distance <= 30) {
                target.takeDamage(damage);
                isToRemove = true;
            }
        } else {
            isToRemove = true;
        }
    }

    public boolean isToRemove() {
        return isToRemove || isOutOfRange();
    }

    public boolean isOutOfRange() {
        return Math.hypot(currentX - startX, currentY - startY) >= 1000;
    }

    public void draw(Graphics g) {
        g.fillOval((int) currentX, (int) currentY, 10, 10);
    }
}
