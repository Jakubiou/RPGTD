package RPG;

import java.awt.*;

public class EnemyProjectile {
    private int x, y;
    private double directionX, directionY;
    private double speed = 10;
    private boolean active = true;
    private static final int SCREEN_WIDTH = 1530;
    private static final int SCREEN_HEIGHT = 800;

    public EnemyProjectile(int x, int y, int targetX, int targetY) {
        this.x = x;
        this.y = y;

        double deltaX = targetX - x;
        double deltaY = targetY - y;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance > 0) {
            directionX = deltaX / distance;
            directionY = deltaY / distance;
        }
    }

    public void move() {
        x += directionX * speed;
        y += directionY * speed;

        if (x < 0 || x > SCREEN_WIDTH || y < 0 || y > SCREEN_HEIGHT) {
            active = false;
        }
    }

    public boolean isActive() {
        return active;
    }

    public boolean checkCollisionWithPlayer(Player player) {
        if (this.getCollider().intersects(player.getCollider())) {
            active = false;
            return true;
        }
        return false;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, 10, 10);
    }

    public Rectangle getCollider() {
        return new Rectangle(x, y, 10, 10);
    }
}
