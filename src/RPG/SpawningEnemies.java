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
    private volatile boolean stopSpawning = false;

    public SpawningEnemies(GamePanel gamePanel, CopyOnWriteArrayList<Enemy> enemies) {
        this.gamePanel = gamePanel;
        this.enemies = enemies;
        this.spawnExecutor = Executors.newSingleThreadExecutor();
    }

    public void spawnEnemies(int normalPerSecond, int giantPerSecond, int smallPerSecond, int shootingPerSecond, int slimePerSecond) {
        allEnemiesSpawned = false;
        stopSpawning = false;

        final int normalRate = normalPerSecond;
        final int giantRate = giantPerSecond;
        final int smallRate = smallPerSecond;
        final int shootingRate = shootingPerSecond;
        final int slimeRate = slimePerSecond;

        spawnExecutor.execute(() -> {
            ArrayList<Point> availableBlocks = getAvailableEdgeBlocks();

            while (!stopSpawning) {
                int totalEnemiesToSpawn = normalRate + giantRate + smallRate + shootingRate + slimeRate;

                for (int i = 0; i < totalEnemiesToSpawn; i++) {
                    if (stopSpawning) return;

                    if (availableBlocks.isEmpty()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            return;
                        }
                        availableBlocks = getAvailableEdgeBlocks();
                    }

                    if (!availableBlocks.isEmpty()) {
                        Point spawnPoint = getSpawnPointBehindCamera();
                        if (spawnPoint != null) {
                            int typeToSpawn = (int) (Math.random() * 5);

                            if (typeToSpawn == 0 && normalRate > 0) {
                                enemies.add(new Enemy(spawnPoint.x, spawnPoint.y, 10, Enemy.Type.NORMAL));
                            } else if (typeToSpawn == 1 && giantRate > 0) {
                                enemies.add(new Enemy(spawnPoint.x, spawnPoint.y, 25, Enemy.Type.GIANT));
                            } else if (typeToSpawn == 2 && smallRate > 0) {
                                enemies.add(new Enemy(spawnPoint.x, spawnPoint.y, 5, Enemy.Type.SMALL));
                            } else if (typeToSpawn == 3 && shootingRate > 0) {
                                enemies.add(new Enemy(spawnPoint.x, spawnPoint.y, 20, Enemy.Type.SHOOTING));
                            } else if (typeToSpawn == 4 && slimeRate > 0) {
                                enemies.add(new Slime(spawnPoint.x, spawnPoint.y, 8));
                            }
                        }
                    }

                    try {
                        Thread.sleep(1000 / totalEnemiesToSpawn);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
            allEnemiesSpawned = true;
        });
    }
    public void spawnBoss() {
        Point spawnPoint = getSpawnPointBehindCamera();
        if (spawnPoint != null) {
            Boss boss = new Boss(spawnPoint.x, spawnPoint.y, 500);
            enemies.add(boss);
        }
    }



    public void stopCurrentSpawn() {
        stopSpawning = true;
        spawnExecutor.shutdownNow();
        spawnExecutor = Executors.newSingleThreadExecutor();
    }


    private ArrayList<Point> getAvailableEdgeBlocks() {
        ArrayList<Point> availableBlocks = new ArrayList<>();

        Player player = gamePanel.getPlayer();
        int cameraX = gamePanel.getCameraX();
        int cameraY = gamePanel.getCameraY();

        int leftBound = cameraX - GamePanel.BLOCK_SIZE;
        int rightBound = cameraX + GamePanel.PANEL_WIDTH + GamePanel.BLOCK_SIZE * 2;
        int topBound = cameraY - GamePanel.BLOCK_SIZE * 2;
        int bottomBound = cameraY + GamePanel.PANEL_HEIGHT + GamePanel.BLOCK_SIZE;

        for (int y = EDGE_OFFSET; y < GamePanel.mapHeight - EDGE_OFFSET; y++) {
            for (int x = EDGE_OFFSET; x < GamePanel.mapWidth - EDGE_OFFSET; x++) {
                int worldX = x * GamePanel.BLOCK_SIZE;
                int worldY = y * GamePanel.BLOCK_SIZE;

                boolean justOutsideCamera =
                        (worldX >= leftBound && worldX <= rightBound &&
                                worldY >= topBound && worldY <= bottomBound &&
                                (worldX < cameraX || worldX > cameraX + GamePanel.PANEL_WIDTH ||
                                        worldY < cameraY || worldY > cameraY + GamePanel.PANEL_HEIGHT));

                if (justOutsideCamera && isBlockAvailable(x, y)) {
                    availableBlocks.add(new Point(worldX, worldY));
                }
            }
        }
        return availableBlocks;
    }

    private Point getSpawnPointBehindCamera() {
        ArrayList<Point> availableBlocks = getAvailableEdgeBlocks();

        if (!availableBlocks.isEmpty()) {
            return availableBlocks.get((int) (Math.random() * availableBlocks.size()));
        }
        return null;
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
