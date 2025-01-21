package RPG;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Collisions {
    private Player player;
    private CopyOnWriteArrayList<Enemy> enemies;
    private CopyOnWriteArrayList<Arrow> arrows;
    private boolean gameOver;

    public Collisions(Player player, CopyOnWriteArrayList<Enemy> enemies, CopyOnWriteArrayList<Arrow> arrows) {
        this.player = player;
        this.enemies = enemies;
        this.arrows = arrows;
        this.gameOver = false;
    }

    public void checkCollisions() {
        Rectangle playerCollider = player.getCollider();
        List<Enemy> enemiesToRemove = new ArrayList<>();
        List<Arrow> arrowsToRemove = new ArrayList<>();

        // Kontrola kolizí střel nepřátel s hráčem
        for (Enemy enemy : enemies) {
            if (enemy.getType() == Enemy.Type.SHOOTING) {
                Iterator<EnemyProjectile> projectileIterator = enemy.getProjectiles().iterator();
                while (projectileIterator.hasNext()) {
                    EnemyProjectile projectile = projectileIterator.next();
                    if (projectile.isActive() && projectile.checkCollisionWithPlayer(player)) {
                        player.hit(enemy.getDamage());
                        projectileIterator.remove();
                        if (player.getHp() <= 0) {
                            gameOver = true;
                        }
                        break;
                    }
                }
            }
        }

        // Kontrola melee útoku hráče
        if (player.isMeleeAttackActive() && System.currentTimeMillis() - player.getMeleeAttackStartTime() < 50) {
            MeleeAttack meleeAttack = player.getMeleeAttack();
            int meleeX = meleeAttack.getX();
            int meleeY = meleeAttack.getY();
            int meleeRadius = meleeAttack.getRadius();
            int meleeAngle = meleeAttack.getAngle();

            for (Enemy enemy : enemies) {
                if (!(enemy instanceof Boss boss) || !boss.isDying) { // Neútočí na bosse během jeho smrti
                    int dx = enemy.getX() + enemy.getWidth() / 2 - meleeX;
                    int dy = enemy.getY() + enemy.getWidth() / 2 - meleeY;
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance <= meleeRadius) {
                        double enemyAngle = Math.toDegrees(Math.atan2(dy, dx));
                        enemyAngle = (enemyAngle + 360) % 360;
                        double normalizedAttackAngle = (meleeAngle + 360) % 360;
                        double angleDifference = Math.abs(normalizedAttackAngle - enemyAngle);
                        if (angleDifference > 180) angleDifference = 360 - angleDifference;
                        if (angleDifference <= 90) {
                            enemy.hit(player.getDamage());
                            if (enemy.getHp() <= 0 && !(enemy instanceof Boss)) {
                                enemiesToRemove.add(enemy);
                                GamePanel.killCountPlus();
                                player.earnCoins(10);
                            }
                        }
                    }
                }
            }
        }

        // Kontrola smrti bossů
        for (Enemy enemy : enemies) {
            if (enemy instanceof Boss boss) {
                if (boss.isDead()) {
                    enemiesToRemove.add(enemy); // Boss bude odstraněn až po dokončení animace smrti
                }
            }
        }

        // Pohyb nepřátel a jejich kolize s hráčem
        for (Enemy enemy : enemies) {
            if (!(enemy instanceof Boss boss) || !boss.isDying) { // Boss se nehýbe během animace smrti
                enemy.moveTowards(player.getX(), player.getY());
            }
            Rectangle enemyCollider = enemy.getCollider();

            // Exploze hráče
            for (Explosion explosion : player.getExplosions()) {
                if (explosion.isInRange(enemy.getX(), enemy.getY())) {
                    enemy.hit(100);
                    if (enemy.getHp() <= 0 && !(enemy instanceof Boss)) {
                        enemiesToRemove.add(enemy);
                        GamePanel.killCountPlus();
                        player.earnCoins(10);
                        break;
                    }
                }
            }

            if (playerCollider.intersects(enemyCollider)) {
                player.moveAwayFrom(enemy.getX(), enemy.getY());
                enemy.moveAwayFrom(player.getX(), player.getY());
                if (enemy.canAttack()) {
                    player.hit(enemy.getDamage());
                    if (player.getHp() <= 0) {
                        gameOver = true;
                    }
                }
            }
        }

        // Střely hráče
        for (Arrow arrow : arrows) {
            if (arrow.move()) {
                arrowsToRemove.add(arrow);
            } else {
                for (Enemy enemy : enemies) {
                    Rectangle enemyCollider = enemy.getCollider();
                    Rectangle arrowCollider = new Rectangle(arrow.getX(), arrow.getY(), Arrow.SIZE, Arrow.SIZE);
                    if (arrowCollider.intersects(enemyCollider)) {
                        enemy.hit(player.getDamage());
                        arrowsToRemove.add(arrow);
                        if (enemy.getHp() <= 0 && !(enemy instanceof Boss)) {
                            enemiesToRemove.add(enemy);
                            GamePanel.killCountPlus();
                            player.earnCoins(10);
                        }
                        break;
                    }
                }
            }
        }

        // Odstraňování střel a nepřátel
        arrows.removeAll(arrowsToRemove);
        enemies.removeAll(enemiesToRemove);

        // Vyřešení kolizí mezi nepřáteli
        resolveEnemyCollisions();
    }



    private void resolveEnemyCollisions() {
        for (int i = 0; i < enemies.size(); i++) {
            for (int j = i + 1; j < enemies.size(); j++) {
                Rectangle collider1 = enemies.get(i).getCollider();
                Rectangle collider2 = enemies.get(j).getCollider();
                if (collider1.intersects(collider2)) {
                    Enemy enemy1 = enemies.get(i);
                    Enemy enemy2 = enemies.get(j);

                    double dx = enemy1.getX() - enemy2.getX();
                    double dy = enemy1.getY() - enemy2.getY();
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (enemy1.getType() != Enemy.Type.SHOOTING && enemy2.getType() != Enemy.Type.SHOOTING) {
                        if (distance < enemy1.getWidth()) {
                            double moveDistance = (enemy1.getWidth() - distance) / 2;
                            dx = (dx / distance) * moveDistance;
                            dy = (dy / distance) * moveDistance;

                            enemy1.moveAwayFrom(enemy2.getX() - (int) dx, enemy2.getY() - (int) dy);
                            enemy2.moveAwayFrom(enemy1.getX() + (int) dx, enemy1.getY() + (int) dy);
                        }
                    }
                }
            }
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
