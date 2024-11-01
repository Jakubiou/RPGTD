package TD;

import TD.GameMap;

import java.awt.*;

import static TD.Direction.*;

public abstract class TDEnemy {
    protected int x, y;
    protected int health;
    protected int speed;
    protected int hpDamage;
    protected Direction direction;

    public TDEnemy(int x, int y, int health, int speed, int hpDamage) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.speed = speed;
        this.hpDamage = hpDamage;
    }

    public void move(GameMap gameMap, TDGamePanel gamePanel) {
        int tileSize = GameMap.TILE_SIZE;
        int row = y / tileSize;
        int col = x / tileSize;

        if (x % tileSize == 0 && y % tileSize == 0) {
            if (gameMap.getMapValue(row, col) == 3) {
                gamePanel.decreaseHealth(this.hpDamage);
                this.health = 0;
                return;
            }

            updateDirection(gameMap, row, col);
        }

        switch (direction) {
            case RIGHT:
                x += speed;
                break;
            case DOWN:
                y += speed;
                break;
            case LEFT:
                x -= speed;
                break;
            case UP:
                y -= speed;
                break;
        }
    }
    private void updateDirection(GameMap gameMap, int row, int col) {
        boolean rightPath = gameMap.isPath(row, col + 1);
        boolean downPath = gameMap.isPath(row + 1, col);
        boolean leftPath = gameMap.isPath(row, col - 1);
        boolean upPath = gameMap.isPath(row - 1, col);

        switch (direction) {
            case RIGHT:
            case LEFT:
                if (upPath) {
                    direction = UP;
                } else if (downPath) {
                    direction = DOWN;
                }
                break;
            case UP:
            case DOWN:
                if (rightPath) {
                    direction = RIGHT;
                } else if (leftPath) {
                    direction = LEFT;
                }
                break;
        }
    }


    public void draw(Graphics g) {
    }

    public int getX() {
        return x + GameMap.TILE_SIZE / 2;
    }

    public int getY() {
        return y + GameMap.TILE_SIZE / 2;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public int getSpeed() {
        return speed;
    }
}
