package TD;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static TD.Direction.*;

public class FastEnemy extends TDEnemy {
    private static final int TILE_SIZE = GameMap.TILE_SIZE;
    private static final int TILE_SIZE_HALF = TILE_SIZE / 2;

    private int targetX;
    private int targetY;
    private BufferedImage[] rightImages;
    private BufferedImage[] leftImages;
    private BufferedImage[] downImages;
    private BufferedImage[] upImages;
    private int animationFrame = 0;

    public FastEnemy(int x, int y) {
        super(x, y, 25, 8,10);
        this.direction = RIGHT;
        setTargetPosition(x, y);

        try {
            rightImages = new BufferedImage[] {
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost9.png")),
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost10.png")),
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost11.png")),
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost12.png"))
            };

            downImages = new BufferedImage[] {
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost1.png")),
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost2.png")),
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost3.png")),
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost4.png"))
            };

            upImages = new BufferedImage[] {
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost5.png")),
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost6.png")),
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost7.png")),
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost8.png"))
            };

            leftImages = new BufferedImage[] {
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost13.png")),
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost14.png")),
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost15.png")),
                    ImageIO.read(new File("res/td/enemy/Enemy_FastGhost16.png"))
            };
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void move(GameMap gameMap, TDGamePanel gamePanel) {
        int row = (y + TILE_SIZE_HALF) / TILE_SIZE;
        int col = (x + TILE_SIZE_HALF) / TILE_SIZE;

        if (gameMap.getMapValue(row, col) == 3) {
            this.health = 0;
            gamePanel.decreaseHealth(this.hpDamage);
            return;
        }

        if (x == targetX && y == targetY) {
            direction = findNewDirection(gameMap, row, col);
            setTargetPosition(x, y);
        }

        moveInDirection(direction);
        updateAnimationFrame();
    }

    private void setTargetPosition(int currentX, int currentY) {
        switch (direction) {
            case RIGHT:
                targetX = currentX + TILE_SIZE;
                targetY = currentY;
                break;
            case LEFT:
                targetX = currentX - TILE_SIZE;
                targetY = currentY;
                break;
            case DOWN:
                targetX = currentX;
                targetY = currentY + TILE_SIZE;
                break;
            case UP:
                targetX = currentX;
                targetY = currentY - TILE_SIZE;
                break;
        }
    }

    private Direction findNewDirection(GameMap gameMap, int row, int col) {
        if (direction == RIGHT || direction == LEFT) {
            if (gameMap.isPath(row + 1, col)) return DOWN;
            if (gameMap.isPath(row - 1, col)) return UP;
        }
        if (direction == DOWN || direction == UP) {
            if (gameMap.isPath(row, col + 1)) return RIGHT;
            if (gameMap.isPath(row, col - 1)) return LEFT;
        }

        return direction;
    }

    private void moveInDirection(Direction dir) {
        switch (dir) {
            case RIGHT:
                x += speed;
                if (x >= targetX) x = targetX;
                break;
            case LEFT:
                x -= speed;
                if (x <= targetX) x = targetX;
                break;
            case DOWN:
                y += speed;
                if (y >= targetY) y = targetY;
                break;
            case UP:
                y -= speed;
                if (y <= targetY) y = targetY;
                break;
        }
    }

    private void updateAnimationFrame() {
        animationFrame = (animationFrame + 1) % 4;
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage currentImage = null;

        switch (direction) {
            case RIGHT:
                currentImage = rightImages[animationFrame];
                break;
            case LEFT:
                currentImage = leftImages[animationFrame];
                break;
            case DOWN:
                currentImage = downImages[animationFrame];
                break;
            case UP:
                currentImage = upImages[animationFrame];
                break;
        }

        if (currentImage != null) {
            g.drawImage(currentImage, x + 12, y - 32, 58, TILE_SIZE, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(x + 12, y + 12, 40, TILE_SIZE);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("HP: " + getHealth(), x, y + TILE_SIZE + 15);
    }
}
