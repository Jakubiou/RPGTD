package RPG;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Player {
    public static int WIDTH = 31;
    private ArrayList<Explosion> explosions = new ArrayList<>();
    private long explosionCooldown = 5000;
    private long lastExplosionTime = 0;
    private int explosionRange = 100;
    public static final int HEIGHT = 45;
    private int x, y, hp;
    private int speed = 5;
    private int heal = 0;
    private int coins = 0;
    private int damage = 5;
    private int attackSpeed = 5;
    private int defense = 0;
    private boolean up, down, left, right;
    private Image[] rightTextures, leftTextures, idleTextures, upTextures, downTextures;
    private int currentFrame = 0;
    private long lastFrameChange = 0;
    private long frameDuration = 100;
    private Image hpBarFrame1;
    private Image hpBarFrame2;
    private Image hpBarFrame3;

    public static final int PANEL_WIDTH = 2700;
    public static final int PANEL_HEIGHT = 2200;
    private boolean dashing = false;
    private int dashDistance = 1000;
    private int dashSpeed = 20;
    private int dashDirectionX = 0, dashDirectionY = 0;
    private int dashProgress = 0;
    private long dashCooldown = 5000;
    private long lastDashTime = 0;

    private int maxHp;

    public Player(int x, int y, int hp) {
        this.x = x;
        this.y = y;
        this.hp = Math.min(hp, 500);
        this.maxHp = Math.min(hp, 500);

        rightTextures = loadTextures("Player1", "Player2", "Player3", "Player4");
        leftTextures = loadTextures("Player5", "Player6", "Player7", "Player8");
        upTextures = loadTextures("Player9", "Player10", "Player11", "Player12");
        downTextures = loadTextures("Player13", "Player14", "Player15", "Player16");
        idleTextures = loadTextures("Player17","Player18","Player19","Player20");
        try {
            hpBarFrame1 = ImageIO.read(new File("res/rpg/background/HPBar1.png"));
            hpBarFrame2 = ImageIO.read(new File("res/rpg/background/HPBar2.png"));
            hpBarFrame3 = ImageIO.read(new File("res/rpg/background/HPBar3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Image[] loadTextures(String... filenames) {
        Image[] textures = new Image[filenames.length];
        try {
            for (int i = 0; i < filenames.length; i++) {
                textures[i] = ImageIO.read(new File("res/rpg/player/" + filenames[i] + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textures;
    }

    public void draw(Graphics g) {
        Image[] textures = idleTextures;
        WIDTH = 31;

        if (right) {
            textures = rightTextures;
            WIDTH = 24;
        } else if (left) {
            textures = leftTextures;
            WIDTH = 24;
        } else if (up) {
            textures = upTextures;
        } else if (down) {
            textures = downTextures;
        }

        g.drawImage(textures[currentFrame], x, y, WIDTH, HEIGHT, null);

        drawDashCooldown(g);
        for (Explosion explosion : explosions) {
            explosion.draw(g);
        }
        drawExplosionCooldown(g);

        int hpBarWidth = 200;
        int hpBarHeight = 20;
        int hpBarX = 1300;
        int hpBarY = 29;


        g.setColor(Color.BLACK);
        g.drawRect(hpBarX, hpBarY, hpBarWidth, hpBarHeight);

        if (hp > 0) {
            int redWidth = Math.min(hp, 100) * hpBarWidth / 100;
            g.setColor(Color.RED);
            g.fillRect(hpBarX, hpBarY, redWidth, hpBarHeight);
            if (hpBarFrame1 != null) {
                g.drawImage(hpBarFrame1, hpBarX - 35, hpBarY - 30, hpBarWidth + 35, hpBarHeight + 45, null);
            }
        }

        if (hp > 100) {
            int purpleWidth = Math.min(hp - 100, 200) * hpBarWidth / 200;
            g.setColor(Color.MAGENTA);
            g.fillRect(hpBarX, hpBarY, purpleWidth, hpBarHeight);
            if (hpBarFrame2 != null) {
                g.drawImage(hpBarFrame2, hpBarX - 35, hpBarY - 30, hpBarWidth + 35, hpBarHeight + 45, null);
            }
        }

        if (hp > 300) {
            int goldWidth = Math.min(hp - 300, 200) * hpBarWidth / 200;
            g.setColor(Color.YELLOW);
            g.fillRect(hpBarX, hpBarY, goldWidth, hpBarHeight);
            if (hpBarFrame3 != null) {
                g.drawImage(hpBarFrame3, hpBarX - 35, hpBarY - 30, hpBarWidth + 35, hpBarHeight + 45, null);
            }
        }

        g.setColor(Color.BLACK);
        for (int i = 1; i <= 9; i++) {
            int dividerX = hpBarX + (i * hpBarWidth / 10);
            g.drawLine(dividerX, hpBarY - 3, dividerX, hpBarY + hpBarHeight);
        }
    }
    private void drawExplosionCooldown(Graphics g) {
        long timeSinceLastExplosion = System.currentTimeMillis() - lastExplosionTime;
        if (timeSinceLastExplosion < explosionCooldown) {
            double percentage = 1 - (double) timeSinceLastExplosion / explosionCooldown;

            int radius = 30;
            int centerX = 100, centerY = PANEL_HEIGHT - 50;

            g.setColor(Color.ORANGE);
            g.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, 90, (int) (360 * percentage));
        }
    }
    public ArrayList<Explosion> getExplosions() {
        return explosions;
    }

    private void drawDashCooldown(Graphics g) {
        long timeSinceLastDash = System.currentTimeMillis() - lastDashTime;
        if (timeSinceLastDash < dashCooldown) {
            double percentage = 1 - (double) timeSinceLastDash / dashCooldown;

            int radius = 30;
            int centerX = 50, centerY = PANEL_HEIGHT - 50;

            g.setColor(Color.RED);
            g.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, 90, (int) (360 * percentage));
        }
    }


    public void move() {
        boolean moving = false;
        int edgeLimit = 61;

        if (dashing) {
            x += dashDirectionX * dashSpeed;
            y += dashDirectionY * dashSpeed;
            dashProgress += dashSpeed;

            if (dashProgress >= dashDistance || x < edgeLimit || y < edgeLimit ||
                    x + WIDTH > PANEL_WIDTH - edgeLimit || y + HEIGHT > PANEL_HEIGHT - edgeLimit) {
                dashing = false;
            }
            return;
        }

        if (up && y - speed >= edgeLimit) {
            y -= speed;
            moving = true;
        }

        if (down && y + speed + HEIGHT <= PANEL_HEIGHT - edgeLimit + 25) {
            y += speed;
            moving = true;
        }

        if (left && x - speed >= edgeLimit) {
            x -= speed;
            moving = true;
        }

        if (right && x + speed + WIDTH <= PANEL_WIDTH - edgeLimit) {
            x += speed;
            moving = true;
        }

        if (moving) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFrameChange >= frameDuration) {
                currentFrame = (currentFrame + 1) % 4;
                lastFrameChange = currentTime;
            }
        } else {
            currentFrame = 0;
        }
        updateExplosions();
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) { up = true; }
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) { down = true; }
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) { left = true; }
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) { right = true; }
        if (key == KeyEvent.VK_SHIFT && canDash()) {
            startDash();
        }
        if (key == KeyEvent.VK_Q && canUseExplosion()) {
            triggerExplosion();
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) { up = false; }
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) { down = false; }
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) { left = false; }
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) { right = false; }
    }
    private boolean canUseExplosion() {
        return System.currentTimeMillis() - lastExplosionTime >= explosionCooldown;
    }

    public void triggerExplosion() {
        explosions.add(new Explosion(x + WIDTH / 2, y + HEIGHT / 2, explosionRange));
        lastExplosionTime = System.currentTimeMillis();
    }

    private void updateExplosions() {
        Iterator<Explosion> iterator = explosions.iterator();
        while (iterator.hasNext()) {
            Explosion explosion = iterator.next();
            explosion.update();
            if (explosion.isComplete()) {
                iterator.remove();
            }
        }
    }
    private boolean canDash() {
        return System.currentTimeMillis() - lastDashTime >= dashCooldown;
    }

    private void startDash() {
        dashing = true;
        dashProgress = 0;
        lastDashTime = System.currentTimeMillis();

        dashDirectionX = 0;
        dashDirectionY = 0;

        if (up) dashDirectionY = -1;
        if (down) dashDirectionY = 1;
        if (left) dashDirectionX = -1;
        if (right) dashDirectionX = 1;

        if (up && left) {
            dashDirectionX = -1;
            dashDirectionY = -1;
        } else if (up && right) {
            dashDirectionX = 1;
            dashDirectionY = -1;
        } else if (down && left) {
            dashDirectionX = -1;
            dashDirectionY = 1;
        } else if (down && right) {
            dashDirectionX = 1;
            dashDirectionY = 1;
        }
    }

    public Rectangle getCollider() {
        return new Rectangle(x, y, 45, 45);
    }

    public void hit(int damage) {
        hp -= (damage - defense > 0) ? damage - defense : 0;
        if (hp < 0) hp = 0;
    }
    public void moveAwayFrom(int x, int y) {
        int dx = this.x - x;
        int dy = this.y - y;

        if (Math.abs(dx) > Math.abs(dy)) {
            this.x += (dx > 0) ? 1 : -1;
        } else {
            this.y += (dy > 0) ? 1 : -1;
        }
    }

    public int getHp() {
        return hp;
    }
    public int getCoins() {
        return coins;
    }
    public void earnCoins(int amount) {
        coins += amount;
    }
    public void spendCoins(int amount) {
        coins -= amount;
    }
    public void increaseDamage() {
        damage += 1;
    }
    public void increaseAttackSpeed() {
        attackSpeed -= 50;
    }
    public void increaseSpeed() {
        speed += 1;
    }
    public void increaseHeal() {
        hp += 1;
        if (hp > 500) hp = 500;
    }
    public void increaseDefense() {
        defense += 1;
    }
    public void increaseHp() {
        hp += 10;
        if (hp > 500) hp = 500;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getAttackSpeed() { return attackSpeed; }
    public int getSpeed() { return speed; }
    public int getDefense() { return defense; }

    public int getDamage() {
        return damage;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getHeal() {
        return heal;
    }
}
