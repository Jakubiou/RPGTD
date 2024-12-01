package RPG;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class Boss extends Enemy {
    private static final int BOSS_SIZE = 50;
    private long lastSpecialAttackTime = 0;
    private static final long SPECIAL_ATTACK_INTERVAL = 3000;
    private long dashStartTime = 0;
    private boolean isPreparingForDash = false;
    protected boolean isDashing = false;
    private int dashTargetX;
    private int dashTargetY;
    protected boolean isAreaAttacking = false;
    private long areaAttackStartTime = 0;
    private static final int DASH_DURATION = 1000;
    private static final long DASH_PREPARE_TIME = 1000;
    private static final int AREA_ATTACK_DURATION = 500;
    private Image texture3;

    public Boss(int x, int y, int hp) {
        super(x, y, hp, Type.BOSS);
        this.speed = 1;
        try {
            texture3 = ImageIO.read(new File("res/rpg/enemy/Enemy_small.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(texture3, x, y, BOSS_SIZE, BOSS_SIZE, null);

        if (isAreaAttacking) {
            g.setColor(new Color(255, 0, 0, 100));
            int attackRadius = 400;
            g.fillOval(x - attackRadius / 2 + BOSS_SIZE / 2,
                    y - attackRadius / 2 + BOSS_SIZE / 2,
                    attackRadius, attackRadius);
        } else if (isPreparingForDash) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillOval(x - BOSS_SIZE / 2, y - BOSS_SIZE / 2, BOSS_SIZE * 2, BOSS_SIZE * 2);
        }

        if (this.hp > 0) {
            int hpBarWidth = 300;
            int hpBarHeight = 30;
            int hpBarX = (GamePanel.PANEL_WIDTH - hpBarWidth) / 2;
            int hpBarY = GamePanel.PANEL_HEIGHT - hpBarHeight - 10;

            g.setColor(new Color(50, 50, 50));
            g.fillRect(hpBarX + GamePanel.cameraX, hpBarY + GamePanel.cameraY, hpBarWidth, hpBarHeight);

            g.setColor(Color.RED);
            int redWidth = (int) (Math.min(this.hp, 500) * hpBarWidth / 500);
            g.fillRect(hpBarX + GamePanel.cameraX, hpBarY + GamePanel.cameraY, redWidth, hpBarHeight);

            g.setColor(Color.BLACK);
            int numSections = 10;
            int sectionWidth = hpBarWidth / numSections;

            for (int i = 1; i < numSections; i++) {
                int sectionX = hpBarX + sectionWidth * i + GamePanel.cameraX;
                g.drawLine(sectionX, hpBarY + GamePanel.cameraY, sectionX, hpBarY + hpBarHeight + GamePanel.cameraY);
            }

            g.setColor(Color.BLACK);
            g.drawRect(hpBarX + GamePanel.cameraX, hpBarY + GamePanel.cameraY, hpBarWidth, hpBarHeight);
        }
    }



    public void dashAttack(int playerX, int playerY) {
        long currentTime = System.currentTimeMillis();

        if (isPreparingForDash) {
            if (currentTime - dashStartTime >= DASH_PREPARE_TIME) {
                isPreparingForDash = false;
                isDashing = true;
                dashStartTime = currentTime;
            }
        } else if (isDashing) {
            double progress = (double) (currentTime - dashStartTime) / DASH_DURATION;

            if (progress < 1.0) {
                x += (dashTargetX - x) * 0.1;
                y += (dashTargetY - y) * 0.1;
            } else {
                isDashing = false;
            }
        }
    }


    @Override
    public void moveTowards(int playerX, int playerY) {
        if (!isPreparingForDash && !isDashing) {
            super.moveTowards(playerX, playerY);
        }
    }


    public void summonMinions(CopyOnWriteArrayList<Enemy> enemies) {
        int radius = 150;
        int minionCount = 8;

        for (int i = 0; i < minionCount; i++) {
            double angle = 2 * Math.PI / minionCount * i;
            int offsetX = (int) (Math.cos(angle) * radius);
            int offsetY = (int) (Math.sin(angle) * radius);

            enemies.add(new Enemy(x + offsetX, y + offsetY, 50, Type.NORMAL));
        }
    }


    public void areaAttack(Player player) {
        long currentTime = System.currentTimeMillis();

        if (!isAreaAttacking) {
            isAreaAttacking = true;
            areaAttackStartTime = currentTime;
        }

        if (currentTime - areaAttackStartTime <= AREA_ATTACK_DURATION) {
            int attackRadius = 400;
            double distance = Math.sqrt(Math.pow(player.getX() - (x + BOSS_SIZE / 2), 2) + Math.pow(player.getY() - (y + BOSS_SIZE / 2), 2));
            if (distance <= attackRadius / 2) {
                player.hit(1);
            }
        } else {
            isAreaAttacking = false;
        }
    }

    public void updateBossBehavior(Player player, CopyOnWriteArrayList<Enemy> enemies) {
        long currentTime = System.currentTimeMillis();

        if (!isDashing && !isAreaAttacking && !isPreparingForDash) {
            moveTowards(player.getX(), player.getY());
        }

        if (currentTime - lastSpecialAttackTime >= SPECIAL_ATTACK_INTERVAL) {
            int attackType = (int) (Math.random() * 3);
            switch (attackType) {
                case 0 -> {
                    isPreparingForDash = true;
                    dashTargetX = player.getX();
                    dashTargetY = player.getY();
                    dashStartTime = currentTime;
                }
                case 1 -> areaAttack(player);
                case 2 -> summonMinions(enemies);
            }
            lastSpecialAttackTime = currentTime;
        }

        if (isPreparingForDash || isDashing) {
            dashAttack(dashTargetX, dashTargetY);
        }
    }


    @Override
    public Rectangle getCollider() {
        return new Rectangle(x, y, BOSS_SIZE, BOSS_SIZE);
    }

    protected int getWidth() {
        return BOSS_SIZE;
    }
}