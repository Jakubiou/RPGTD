package RPG;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpawningEnemies {
    private static final int EDGE_OFFSET = 1;
    private GamePanel gamePanel;
    private CopyOnWriteArrayList<Enemy> enemies;
    private ExecutorService spawnExecutor;
    private boolean allEnemiesSpawned = false;

    public SpawningEnemies(GamePanel gamePanel, CopyOnWriteArrayList<Enemy> enemies) {
        this.gamePanel = gamePanel;
        this.enemies = enemies;
        this.spawnExecutor = Executors.newSingleThreadExecutor();
    }

    public void spawnEnemies(int normalCount, int giantCount, int smallCount, int shootingCount, int slimeCount) {
        allEnemiesSpawned = false;
        final int[] remainingNormalCount = {normalCount};
        final int[] remainingGiantCount = {giantCount};
        final int[] remainingSmallCount = {smallCount};
        final int[] remainingShootingCount = {shootingCount};
        final int[] remainingSlimeCount = {slimeCount};

        spawnExecutor.execute(() -> {
            ArrayList<Point> availableBlocks = getAvailableEdgeBlocks();

            while (remainingNormalCount[0] > 0 || remainingGiantCount[0] > 0 || remainingSmallCount[0] > 0 || remainingShootingCount[0] > 0 || remainingSlimeCount[0] > 0) {
                if (availableBlocks.isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    availableBlocks = getAvailableEdgeBlocks();
                }

                while (!availableBlocks.isEmpty() && (remainingNormalCount[0] > 0 || remainingGiantCount[0] > 0 || remainingSmallCount[0] > 0 || remainingShootingCount[0] > 0 || remainingSlimeCount[0] > 0)) {
                    Point spawnPoint = availableBlocks.remove((int) (Math.random() * availableBlocks.size()));
                    int typeToSpawn = (int) (Math.random() * 5);

                    if (typeToSpawn == 0 && remainingNormalCount[0] > 0) {
                        enemies.add(new Enemy(spawnPoint.x, spawnPoint.y, 10, Enemy.Type.NORMAL));
                        remainingNormalCount[0]--;
                    } else if (typeToSpawn == 1 && remainingGiantCount[0] > 0) {
                        enemies.add(new Enemy(spawnPoint.x, spawnPoint.y, 25, Enemy.Type.GIANT));
                        remainingGiantCount[0]--;
                    } else if (typeToSpawn == 2 && remainingSmallCount[0] > 0) {
                        enemies.add(new Enemy(spawnPoint.x, spawnPoint.y, 5, Enemy.Type.SMALL));
                        remainingSmallCount[0]--;
                    } else if (typeToSpawn == 3 && remainingShootingCount[0] > 0) {
                        enemies.add(new Enemy(spawnPoint.x, spawnPoint.y, 20, Enemy.Type.SHOOTING));
                        remainingShootingCount[0]--;
                    } else if (typeToSpawn == 4 && remainingSlimeCount[0] > 0) {
                        enemies.add(new Slime(spawnPoint.x, spawnPoint.y, 8));
                        remainingSlimeCount[0]--;
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            allEnemiesSpawned = true;
        });
    }

    private ArrayList<Point> getAvailableEdgeBlocks() {
        ArrayList<Point> availableBlocks = new ArrayList<>();
        for (int y = EDGE_OFFSET; y < GamePanel.mapHeight - EDGE_OFFSET; y++) {
            for (int x = EDGE_OFFSET; x < GamePanel.mapWidth - EDGE_OFFSET; x++) {
                if (isEdgeBlock(x, y) && isBlockAvailable(x, y)) {
                    availableBlocks.add(new Point(x * GamePanel.BLOCK_SIZE, y * GamePanel.BLOCK_SIZE));
                }
            }
        }
        return availableBlocks;
    }

    private boolean isEdgeBlock(int x, int y) {
        return (x == EDGE_OFFSET || x == GamePanel.mapWidth - EDGE_OFFSET - 1 || y == EDGE_OFFSET || y == GamePanel.mapHeight - EDGE_OFFSET - 1)
                && !(x == EDGE_OFFSET && y == EDGE_OFFSET)
                && !(x == GamePanel.mapWidth - EDGE_OFFSET - 1 && y == EDGE_OFFSET)
                && !(x == EDGE_OFFSET && y == GamePanel.mapHeight - EDGE_OFFSET - 1)
                && !(x == GamePanel.mapWidth - EDGE_OFFSET - 1 && y == GamePanel.mapHeight - EDGE_OFFSET - 1);
    }

    private boolean isBlockAvailable(int x, int y) {
        Rectangle blockRect = new Rectangle(x * GamePanel.BLOCK_SIZE, y * GamePanel.BLOCK_SIZE, GamePanel.BLOCK_SIZE, GamePanel.BLOCK_SIZE);
        for (Enemy enemy : enemies) {
            if (blockRect.intersects(enemy.getCollider())) {
                return false;
            }
        }
        return true;
    }

    public boolean areAllEnemiesSpawned() {
        return allEnemiesSpawned;
    }
}
