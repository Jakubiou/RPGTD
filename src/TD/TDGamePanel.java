package TD;

import TD.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TDGamePanel extends JPanel {
    private GameMap gameMap;
    public static final int BLOCK_SIZE = 64;
    private Image[] blockImages;
    private List<Tower> towers;
    private List<TDEnemy> enemies;
    private int coins;
    public static int[][] map;
    public static int mapWidth, mapHeight;
    private Tower selectedTower;
    private boolean selectingTower;
    private JPanel buttonPanel;
    private JButton toggleButton;
    private boolean panelVisible;
    private Tower hoveredTower;
    private int waveNumber = 0;
    private JButton nextWaveButton;

    private boolean gameOver = false;
    private boolean isPaused = false;
    private int health = 100;
    Timer gameTimer;

    public TDGamePanel(GameMap gameMap) {
        this.gameMap = gameMap;
        this.towers = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.coins = 100;
        this.selectedTower = null;
        this.selectingTower = false;
        this.panelVisible = true;
        this.hoveredTower = null;
        loadBlockImages();
        loadMap("TDMap1.txt");

        setLayout(null);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1));
        buttonPanel.setBounds(1440, 0, 100, 200);

        JButton normalTowerButton = new JButton("Normal Tower");
        JButton fastTowerButton = new JButton("Fast Tower");
        JButton slowTowerButton = new JButton("Slow Tower");

        normalTowerButton.setPreferredSize(new Dimension(100, 50));
        fastTowerButton.setPreferredSize(new Dimension(100, 50));
        slowTowerButton.setPreferredSize(new Dimension(100, 50));

        buttonPanel.add(normalTowerButton);
        buttonPanel.add(fastTowerButton);
        buttonPanel.add(slowTowerButton);

        toggleButton = new JButton("-");
        toggleButton.setPreferredSize(new Dimension(50, 50));
        buttonPanel.add(toggleButton);

        normalTowerButton.addActionListener(e -> selectTower(new NormalTower(0, 0)));
        fastTowerButton.addActionListener(e -> selectTower(new FastTower(0, 0)));
        slowTowerButton.addActionListener(e -> selectTower(new SlowTower(0, 0)));

        toggleButton.addActionListener(e -> toggleButtonPanel());

        add(buttonPanel);
        add(toggleButton);
        toggleButton.setBounds(1390, 0, 50, 50);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && selectingTower) {
                    placeTower(e.getX(), e.getY());
                }
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                checkTowerHover(e.getX(), e.getY());
            }
        });

        gameTimer = new Timer(100, e -> {
            updateGame();
            repaint();
        });
        waveNumber = 0;
        nextWave();
        gameTimer.start();

        nextWaveButton = new JButton();
        nextWaveButton.setBounds(10, 60, 200, 50);

        ImageIcon normalIcon = new ImageIcon(new ImageIcon("res/buttons/NextWave_button1.png").getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH));
        ImageIcon rolloverIcon = new ImageIcon(new ImageIcon("res/buttons/NextWave_button2.png").getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH));
        nextWaveButton.setIcon(normalIcon);
        nextWaveButton.setRolloverIcon(rolloverIcon);

        nextWaveButton.setBorderPainted(false);
        nextWaveButton.setContentAreaFilled(false);
        nextWaveButton.setFocusPainted(false);
        nextWaveButton.setOpaque(false);

        nextWaveButton.setVisible(false);
        nextWaveButton.addActionListener(e -> startNextWave());
        add(nextWaveButton);

    }

    private void loadMap(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            ArrayList<int[]> mapList = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] tokens = line.split(" ");
                int[] row = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    try {
                        row[i] = Integer.parseInt(tokens[i].trim());
                    } catch (NumberFormatException e) {
                        row[i] = 0;
                        System.err.println("Error parsing number: " + tokens[i]);
                    }
                }
                mapList.add(row);
            }

            mapHeight = mapList.size();
            mapWidth = mapList.isEmpty() ? 0 : mapList.get(0).length;
            map = new int[mapHeight][mapWidth];
            for (int i = 0; i < mapHeight; i++) {
                map[i] = mapList.get(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadBlockImages() {
        blockImages = new Image[20];

        try {
            blockImages[0] = ImageIO.read(new File("res/td/background/Block16.png"));
            blockImages[1] = ImageIO.read(new File("res/td/background/Block17.png"));
            blockImages[2] = ImageIO.read(new File("res/td/background/Block18.png"));
            blockImages[3] = ImageIO.read(new File("res/td/background/Block19.png"));
            blockImages[4] = ImageIO.read(new File("res/td/background/Block20.png"));
            blockImages[5] = ImageIO.read(new File("res/td/background/Block21.png"));
            blockImages[6] = ImageIO.read(new File("res/td/background/Block22.png"));
            blockImages[7] = ImageIO.read(new File("res/td/background/Block23.png"));
            blockImages[8] = ImageIO.read(new File("res/td/background/Block24.png"));
            blockImages[9] = ImageIO.read(new File("res/td/background/Block25.png"));
            blockImages[10] = ImageIO.read(new File("res/td/background/Block26.png"));
            blockImages[11] = ImageIO.read(new File("res/td/background/Block27.png"));
            blockImages[12] = ImageIO.read(new File("res/td/background/Block28.png"));
            blockImages[13] = ImageIO.read(new File("res/td/background/Block29.png"));
            blockImages[14] = ImageIO.read(new File("res/td/background/Block30.png"));
            blockImages[15] = ImageIO.read(new File("res/td/background/Block31.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void stopGame() {
        gameTimer.stop();
        isPaused = true;
    }

    private void startGame() {
        gameTimer.start();
        isPaused = false;
    }
    private void toggleButtonPanel() {
        panelVisible = !panelVisible;
        buttonPanel.setVisible(panelVisible);
        if (panelVisible) {
            toggleButton.setBounds(1390, 0, 50, 50);
        } else {
            toggleButton.setBounds(1490, 0, 50, 50);
        }
        toggleButton.setText(panelVisible ? "-" : "+");
    }

    private void selectTower(Tower tower) {
        this.selectedTower = tower;
        this.selectingTower = true;
    }

    private void placeTower(int x, int y) {
        int row = y / GameMap.TILE_SIZE;
        int col = x / GameMap.TILE_SIZE;
        if (selectedTower != null && gameMap.isPlaceable(row, col)) {
            selectedTower.setPosition(col * GameMap.TILE_SIZE + GameMap.TILE_SIZE / 2,
                    row * GameMap.TILE_SIZE + GameMap.TILE_SIZE / 2);
            towers.add(selectedTower);
            coins -= 50;
            selectedTower = null;
            selectingTower = false;
        }
    }


    private void checkTowerHover(int x, int y) {
        for (Tower tower : towers) {
            if (tower.isHovered(x, y)) {
                hoveredTower = tower;
                return;
            }
        }
        hoveredTower = null;
    }

    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(3);

    private boolean spawningEnemies = false;

    private void spawnEnemies(int normalCount, int normalInterval, int slowCount, int slowInterval, int fastCount, int fastInterval, int boss1Count, int boss1Interval) {
        final int[] remainingNormalCount = {normalCount};
        final int[] remainingSlowCount = {slowCount};
        final int[] remainingFastCount = {fastCount};
        final int[] remainingBoss1Count = {boss1Count};

        spawningEnemies = true;

        // Normal enemies
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (remainingNormalCount[0] > 0) {
                spawnEnemyOfType("normal");
                remainingNormalCount[0]--;
            } else {
                checkIfAllEnemiesSpawned(remainingNormalCount,remainingSlowCount,remainingFastCount,remainingBoss1Count);
            }
        }, 0, normalInterval, TimeUnit.SECONDS);

        // Slow enemies
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (remainingSlowCount[0] > 0) {
                spawnEnemyOfType("slow");
                remainingSlowCount[0]--;
            } else {
                checkIfAllEnemiesSpawned(remainingNormalCount,remainingSlowCount,remainingFastCount,remainingBoss1Count);
            }
        }, 0, slowInterval, TimeUnit.SECONDS);

        // Fast enemies
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (remainingFastCount[0] > 0) {
                spawnEnemyOfType("fast");
                remainingFastCount[0]--;
            } else {
                checkIfAllEnemiesSpawned(remainingNormalCount,remainingSlowCount,remainingFastCount,remainingBoss1Count);
            }
        }, 0, fastInterval, TimeUnit.SECONDS);

        // Boss enemies
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (remainingBoss1Count[0] > 0) {
                spawnEnemyOfType("boss1");
                remainingBoss1Count[0]--;
            } else {
                checkIfAllEnemiesSpawned(remainingNormalCount,remainingSlowCount,remainingFastCount,remainingBoss1Count);
            }
        }, 0, boss1Interval, TimeUnit.SECONDS);
    }

    private synchronized void checkIfAllEnemiesSpawned(int[] remainingNormalCount,int[] remainingSlowCount, int[] remainingFastCount ,int[] remainingBoss1Count) {
        if (remainingNormalCount[0] == 0 && remainingSlowCount[0] == 0 && remainingFastCount[0] == 0 && remainingBoss1Count[0] == 0) {
            spawningEnemies = false;
        }
    }


    private void spawnEnemyOfType(String type) {
        for (int i = 0; i < gameMap.getRows(); i++) {
            for (int j = 0; j < gameMap.getCols(); j++) {
                if (gameMap.getMapValue(i, j) == 2) {
                    switch (type) {
                        case "normal":
                            enemies.add(new NormalEnemy(j * GameMap.TILE_SIZE, i * GameMap.TILE_SIZE));
                            break;
                        case "slow":
                            enemies.add(new SlowEnemy(j * GameMap.TILE_SIZE, i * GameMap.TILE_SIZE));
                            break;
                        case "fast":
                            enemies.add(new FastEnemy(j * GameMap.TILE_SIZE, i * GameMap.TILE_SIZE));
                            break;
                        case "boss1":
                            enemies.add(new BossEnemy1(j * GameMap.TILE_SIZE, i * GameMap.TILE_SIZE));
                            break;
                    }
                    return;
                }
            }
        }
    }

    public void decreaseHealth(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            stopGame();
            gameOver = true;
        }
    }


    private boolean waveComplete = false;

    private void updateGame() {
        for (TDEnemy enemy : enemies) {
            enemy.move(gameMap, this);
        }

        for (Tower tower : towers) {
            tower.shoot(enemies);
        }

        enemies.removeIf(enemy -> enemy.getHealth() <= 0);

        // Check if wave is complete only if no enemies are spawning and no enemies are left
        if (!spawningEnemies && enemies.isEmpty() && !waveComplete) {
            checkIfWaveReallyComplete();
        }
    }

    private void checkIfWaveReallyComplete() {
        if (enemies.isEmpty() && !spawningEnemies) {
            waveComplete = true;
            nextWaveButton.setVisible(true);
            stopGame();
        }
    }
    private void nextWave() {
        enemies.clear();
        waveNumber++;

        spawningEnemies = true;
        waveComplete = false;

        switch (waveNumber) {
            case 1:
                spawnEnemies(10, 1, 10, 1, 10, 1, 0, 1);
                break;
            case 2:
                spawnEnemies(50, 1, 2, 3, 1, 4, 0, 1);
                break;
            case 3:
                spawnEnemies(7, 1, 3, 3, 1, 4, 0, 1);
                break;
            default:
                spawnEnemies(waveNumber * 4, 2, waveNumber * 2, 3, waveNumber * 2, 3, 0, 1);
                break;
        }

        startGame();
    }

    private void startNextWave() {
        nextWaveButton.setVisible(false);
        nextWave();
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        drawUI(g);

        for (Tower tower : towers) {
            tower.draw(g);
            tower.drawBullets(g);
        }
        for (TDEnemy enemy : enemies) {
            enemy.draw(g);
        }

        if (hoveredTower != null) {
            drawTowerInfo(g, hoveredTower);
            drawTowerRange(g, hoveredTower);
        }

        if (selectingTower) {
            drawGridOverlay(g);
        }
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
            nextWaveButton.setVisible(false);
            toggleButton.setVisible(false);
            buttonPanel.setVisible(false);
            g.setFont(new Font("Arial", Font.BOLD, 100));
            g.setColor(Color.WHITE);
            g.drawString("Game Over", getWidth() / 3, getHeight() / 2);
        }
    }


    private void drawGridOverlay(Graphics g) {
        g.setColor(new Color(0, 0, 0, 128));

        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                int xPos = x * BLOCK_SIZE;
                int yPos = y * BLOCK_SIZE;
                g.drawRect(xPos, yPos, BLOCK_SIZE, BLOCK_SIZE);
            }
        }
    }



    private void drawTowerInfo(Graphics g, Tower tower) {
        g.setColor(Color.WHITE);
        g.fillRect(tower.getX() + 20, tower.getY() - 40, 120, 60);
        g.setColor(Color.BLACK);
        g.drawRect(tower.getX() + 20, tower.getY() - 40, 120, 60);
        g.drawString("Damage: " + tower.getDamage(), tower.getX() + 25, tower.getY() - 25);
        g.drawString("Atk Speed: " + tower.getFireRate(), tower.getX() + 25, tower.getY() - 10);
    }

    private void drawTowerRange(Graphics g, Tower tower) {
        g.setColor(Color.RED);
        g.drawOval(tower.getX() - tower.getRange(), tower.getY() - tower.getRange(),
                tower.getRange() * 2, tower.getRange() * 2);
    }
    private void drawBackground(Graphics g) {
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                int blockType = map[y][x];
                Image blockImage = blockImages[blockType];
                g.drawImage(blockImage, x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
            }
        }
    }
    private void drawUI(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Wave: " + waveNumber, 10, 20);
        g.drawString("HP: " + health, 10, 50);
    }
}