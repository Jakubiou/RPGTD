package TD;
import TD.Bullet;
import TD.TDEnemy;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Tower {
    protected int x, y;
    protected int range;
    protected int damage;
    protected int fireRate;
    private long lastFireTime;
    private List<Bullet> bullets;

    public Tower(int x, int y,int range) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.damage = 10;
        this.fireRate = 1000;
        this.lastFireTime = System.currentTimeMillis();
        this.bullets = new ArrayList<>();
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setFireRate(int fireRate) {
        this.fireRate = fireRate;
    }

    public int getFireRate() {
        return fireRate;
    }

    public void shoot(List<TDEnemy> enemies) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFireTime >= fireRate) {
            TDEnemy target = findClosestEnemy(enemies);
            if (target != null && isInRange(target)) {
                bullets.add(new Bullet(x, y, target, damage));
                lastFireTime = currentTime;
            }
        }
        updateBullets();
    }

    private TDEnemy findClosestEnemy(List<TDEnemy> enemies) {
        TDEnemy closest = null;
        double minDistance = Double.MAX_VALUE;
        for (TDEnemy enemy : enemies) {
            double distance = Math.hypot(enemy.getX() - x, enemy.getY() - y);
            if (distance < minDistance) {
                minDistance = distance;
                closest = enemy;
            }
        }
        return closest;
    }

    private boolean isInRange(TDEnemy enemy) {
        return Math.hypot(enemy.getX() - x, enemy.getY() - y) <= range;
    }

    private void updateBullets() {
        for (Bullet bullet : bullets) {
            bullet.update();
        }
        bullets.removeIf(Bullet::isToRemove);
    }

    public abstract void draw(Graphics g);

    public void drawBullets(Graphics g) {
        g.setColor(Color.RED);
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRange() {
        return range;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x - 10 && mouseX <= x + 10 && mouseY >= y - 10 && mouseY <= y + 10;
    }
}

