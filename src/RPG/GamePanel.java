package RPG;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
public class GamePanel extends JPanel implements ActionListener {
    public static final int PANEL_WIDTH = 1530;
    public static final int PANEL_HEIGHT = 800;
    private JButton nextWaveButton;
    private JPanel abilityPanel;
    private boolean abilityPanelVisible;
    public static int[][] map;
    public static int mapWidth, mapHeight;
    public static final int BLOCK_SIZE = 64;
    private Player player;
    private CopyOnWriteArrayList<Enemy> enemies;
    private CopyOnWriteArrayList<Arrow> arrows;
    private Timer timer;
    private boolean gameOver = false;
    private boolean isPaused = false;
    private Image[] blockImages;
    private int waveNumber = 0;
    private RPGGame game;
    private JButton menuButton;
    private MenuPanel menuPanel;
    private SpawningEnemies spawningEnemies;
    private Collisions collisions;
    private GameOverPanel gameOverPanel;
    public static final int SCALE_FACTOR = 4;
    private static final int CAMERA_WIDTH = PANEL_WIDTH / SCALE_FACTOR;
    private static final int CAMERA_HEIGHT = PANEL_HEIGHT / SCALE_FACTOR;

    public static int cameraX, cameraY;
    private static int killCount;
    protected static Soundtrack backgroundMusic;

    public GamePanel(RPGGame game, Player player) {
        this.game = game;
        this.player = player;
        backgroundMusic = new Soundtrack("res/RPG/Music/658572__josefpres__8-bit-game-loop-013-simple-mix-1-short-120-bpm.wav");
        backgroundMusic.playLoop();
        initializeAbilityPanel();
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setFocusable(true);
        setLayout(null);
        initializeMenu();
        loadBlockImages();
        loadMap("map1.txt");
        initGame();
    }

    private void initializeMenu() {
        menuButton = new JButton();
        menuButton.setBounds(750, 10, 100, 40);

        ImageIcon normalMenuIcon = new ImageIcon(new ImageIcon("res/buttons/Menu_button1.png").getImage().getScaledInstance(100, 40, Image.SCALE_SMOOTH));
        ImageIcon rolloverMenuIcon = new ImageIcon(new ImageIcon("res/buttons/Menu_button2.png").getImage().getScaledInstance(100, 40, Image.SCALE_SMOOTH));

        menuButton.setIcon(normalMenuIcon);
        menuButton.setRolloverIcon(rolloverMenuIcon);
        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusPainted(false);
        menuButton.setOpaque(false);

        menuButton.addActionListener(e -> toggleMenu());
        add(menuButton);

        menuPanel = new MenuPanel(game, this);
        add(menuPanel);
    }

    public void toggleMenu() {
        if (menuPanel.isVisible()) {
            menuPanel.setVisible(false);
            menuButton.setVisible(true);
            if(!enemies.isEmpty()) {
                startGame();
            }
        } else {
            menuPanel.setVisible(true);
            menuButton.setVisible(false);
            stopGame();
        }
    }

    public void restartGame() {
        game.dispose();
        new RPGGame();
    }

    private void loadBlockImages() {
        blockImages = new Image[26];
        try {
            for (int i = 0; i < blockImages.length; i++) {
                Image original = ImageIO.read(new File("res/rpg/background/Block" + i + ".png"));
                blockImages[i] = original.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_SMOOTH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeAbilityPanel() {
        abilityPanel = new JPanel();
        abilityPanel.setLayout(new GridLayout(4, 1));

        int panelWidth = 400;
        int panelHeight = 300;
        abilityPanel.setBounds((PANEL_WIDTH - panelWidth) / 2, (PANEL_HEIGHT - panelHeight) / 2, panelWidth, panelHeight);

        abilityPanel.setBackground(Color.DARK_GRAY);
        abilityPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        JLabel coinsLabel = new JLabel("Coins: " + player.getCoins(), JLabel.CENTER);
        coinsLabel.setForeground(Color.WHITE);
        coinsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        abilityPanel.add(coinsLabel);

        JButton damageButton = createUpgradeButton("Damage");
        JButton hpButton = createUpgradeButton("HP");
        JButton defenseButton = createUpgradeButton("Defense");

        damageButton.addActionListener(e -> upgradeStat("damage", 10));
        hpButton.addActionListener(e -> upgradeStat("HP", 10));
        defenseButton.addActionListener(e -> upgradeStat("defense", 10));

        abilityPanel.add(damageButton);
        abilityPanel.add(hpButton);
        abilityPanel.add(defenseButton);

        add(abilityPanel);
        abilityPanel.setVisible(false);
    }

    private JButton createUpgradeButton(String statName) {
        JButton button = new JButton() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                String text = String.format("%s +%nCurrent: %d", statName, getCurrentStatValue(statName));
                drawCenteredString(g, text, getWidth(), getHeight());
            }
        };
        styleAbylityButton(button);
        return button;
    }

    private void drawCenteredString(Graphics g, String text, int width, int height) {
        FontMetrics metrics = g.getFontMetrics();
        String[] lines = text.split("\n");
        int y = (height - metrics.getHeight() * lines.length) / 2 + metrics.getAscent();
        for (String line : lines) {
            int x = (width - metrics.stringWidth(line)) / 2;
            g.drawString(line, x, y);
            y += metrics.getHeight();
        }
    }

    private int getCurrentStatValue(String statName) {
        switch (statName) {
            case "Damage":
                return player.getDamage();
            case "HP":
                return player.getHp();
            case "Defense":
                return player.getDefense();
            default:
                return 0;
        }
    }

    private void styleAbylityButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setFocusPainted(false);
    }

    private void upgradeStat(String stat, int cost) {
        if (player.getCoins() >= cost) {
            player.setCoins(player.getCoins() - cost);
            switch (stat) {
                case "damage":
                    player.increaseDamage();
                    break;
                case "HP":
                    player.increaseHp();
                    break;
                case "defense":
                    player.increaseDefense();
                    break;
            }
            updateUpgradePanel();
            this.requestFocusInWindow();
        }
    }

    private void updateUpgradePanel() {
        ((JLabel)abilityPanel.getComponent(0)).setText("Coins: " + player.getCoins());
        abilityPanel.repaint();
    }

    private void stopGame() {
        timer.stop();
        isPaused = true;
        backgroundMusic.stop();
    }

    private void startGame() {
        timer.start();
        isPaused = false;
        backgroundMusic.playLoop();
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

    private void initGame() {
        player = new Player(mapWidth * BLOCK_SIZE / 2, mapHeight * BLOCK_SIZE / 2, 100);
        enemies = new CopyOnWriteArrayList<>();
        arrows = new CopyOnWriteArrayList<>();
        collisions = new Collisions(player, enemies, arrows);
        gameOverPanel = new GameOverPanel(game, this);
        gameOverPanel.setVisible(false);
        add(gameOverPanel);


        spawningEnemies = new SpawningEnemies(this, enemies);

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

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                player.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                player.keyReleased(e);
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_1) {
                    player.toggleMeleeMode();
                } else if (key == KeyEvent.VK_2) {
                    player.toggleMeleeMode();
                } else {
                    player.keyPressed(e);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                player.keyReleased(e);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isPaused) {
                    int mouseX = e.getX() + cameraX;
                    int mouseY = e.getY() + cameraY;

                    if (player.isMeleeMode()) {
                        player.performMeleeAttack(mouseX, mouseY);
                    } else {
                        arrows.add(new Arrow(player.getX() + Player.WIDTH / 2, player.getY() + Player.HEIGHT / 2, mouseX, mouseY));
                    }
                }
            }
        });



        timer = new Timer(20, this);
        waveNumber = 0;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        nextWave();
        abilityPanel.setVisible(false);

        timer.start();
    }

    private void nextWave() {
        spawningEnemies.stopCurrentSpawn();
        enemies.clear();
        waveNumber++;
        arrows.clear();
        killCount = 0;

        switch (waveNumber) {
            case 1:
                spawningEnemies.spawnEnemies(1000, 0, 0, 0,0);
                break;
            case 2:
                spawningEnemies.spawnEnemies(5, 2, 1, 2,1);
                break;
            case 3:
                spawningEnemies.spawnEnemies(7, 3, 1, 5,2);
                break;
            default:
                spawningEnemies.spawnEnemies(waveNumber * 4, waveNumber * 2, waveNumber * 3, waveNumber * 3,5);
                break;
        }

        startGame();
    }

    private void startNextWave() {
        nextWaveButton.setVisible(false);
        abilityPanel.setVisible(false);
        nextWave();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !isPaused) {
            if (abilityPanel.isVisible()) {
                updateUpgradePanel();
            }
            player.move();

            collisions.checkCollisions(killCount);
            gameOver = collisions.isGameOver();

            for (Enemy enemy : enemies) {
                if (enemy.getType() == Enemy.Type.SHOOTING) {
                    enemy.updateProjectiles();
                }
            }

            if (killCount > waveNumber * 1000 && !gameOver) {
                stopGame();
                nextWaveButton.setVisible(true);
                abilityPanel.setVisible(true);
            }

            if (player.getHp() <= 0) {
                gameOver = true;
                setPreferredSize(new Dimension(PANEL_WIDTH / 2, PANEL_HEIGHT / 2));
                revalidate();
            }
            repaint();
        }
    }
    public static void killCountPlus(){
        killCount++;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateCamera();
        Graphics2D g2d = (Graphics2D) g;

        g2d.translate(-cameraX, -cameraY);

        drawBackground(g2d);
        drawEnemies(g2d);
        drawArrows(g2d);
        drawUI(g2d);
        drawPlayer(g2d);

        g2d.translate(cameraX, cameraY);

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (enemy.getType() == Enemy.Type.SHOOTING) {
                enemy.drawProjectiles(g);
            }
        }
        if (abilityPanelVisible) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if (gameOver) {
            gameOverPanel.setVisible(true);
            menuButton.setVisible(false);
        }
    }

    private void updateCamera() {
        int targetCameraX = player.getX() - CAMERA_WIDTH * 2;
        int targetCameraY = player.getY() - CAMERA_HEIGHT * 2;

        cameraX = Math.max(0, Math.min(targetCameraX, mapWidth * BLOCK_SIZE - PANEL_WIDTH));
        cameraY = Math.max(0, Math.min(targetCameraY, mapHeight * BLOCK_SIZE - PANEL_HEIGHT));
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
    private void drawPlayer(Graphics g) {
        player.draw(g);
    }
    public void drawEnemies(Graphics g) {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);

            enemy.draw(g);

            if (enemy.isOffScreen()) {
                enemies.remove(i);
                i--;
            }
        }
    }
    private void drawArrows(Graphics g) {
        for (Arrow arrow : arrows) {
            arrow.draw(g);
        }
    }
    private void drawUI(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Wave: " + waveNumber, 10 + cameraX, 20 + cameraY);
        g.drawString("Coins: " + player.getCoins(), 10 + cameraX, 40 + cameraY);
    }

    public int getCameraX() {
        return cameraX;
    }

    public int getCameraY() {
        return cameraY;
    }

    public Player getPlayer() {
        return player;
    }
}
