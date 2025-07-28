package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;

import gdd.sprite.Alien1;
import gdd.sprite.Alien2;
import gdd.sprite.Alien2.Bomb2;
import gdd.sprite.BossAlien;
import gdd.sprite.BossAlien.Bomb;
import gdd.sprite.Explosion;
import gdd.sprite.HorizontalPlayer;
import gdd.sprite.Shot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class BossScene extends JPanel {

    private int frame = 0;
    private BossAlien boss;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private List<BossAlien.Bomb> bombs = new ArrayList<>();
    private List<Alien1.Bomb> enemyBombs = new ArrayList<>();
    private List<Bomb2> enemyBombs2 = new ArrayList<>(); // Add this line
    private HorizontalPlayer player;
    private List<gdd.sprite.Enemy> smallAliens = new ArrayList<>();
    private List<Shot> enemyShots = new ArrayList<>();

    // Map variables for scrolling background
    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;
    final int BLOCKS_TO_DRAW = BOARD_HEIGHT / BLOCKHEIGHT;
    private double scrollSpeed = 2;
    private double scrollOffset = 0;

    // Boss movement variables
    private int bossMovementCounter = 0;
    private int bossVerticalDirection = 1;
    private int bossHorizontalDirection = 1;
    private Random bossMovementRandom = new Random();

    // Small alien movement variables
    private Map<gdd.sprite.Enemy, Integer> alienVerticalDirections = new HashMap<>();

    // Boss attack pattern variables
    private int bossAttackPhase = 0;
    private int attackTimer = 0;
    private int attackCooldown = 0;
    private int phaseCounter = 0;

    // Background star map pattern (loaded from CSV)
    private int[][] MAP;

    private boolean inGame = true;
    private String message = "Game Over";
    private String winMessage = "You Win!";
    private boolean playerWon = false;

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;
    private AudioPlayer audioPlayer;

    public BossScene(Game game) {
        this.game = game;
        loadMapFromCSV("src/boss_scene_starmap.csv");
    }

    // Loads the star map from a CSV file (e.g., src/spawn_enemy.csv)
    private void loadMapFromCSV(String filePath) {
        List<int[]> rows = new ArrayList<>();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Ignore empty lines and comments
                if (line.trim().isEmpty() || line.startsWith("#"))
                    continue;
                String[] tokens = line.split(",");
                int[] row = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    row[i] = Integer.parseInt(tokens[i].trim());
                }
                rows.add(row);
            }
            MAP = rows.toArray(new int[rows.size()][]);
        } catch (Exception e) {
            System.err.println("Error loading star map from CSV: " + e.getMessage());
            // Fallback: use a default map if loading fails
        }
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/scene1.wav";
            audioPlayer = new AudioPlayer(filePath);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        gameInit();
        // initAudio();
    }

    public void stop() {
        timer.stop();
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void gameInit() {
        explosions = new ArrayList<>();
        shots = new ArrayList<>();
        smallAliens = new ArrayList<>();
        enemyShots = new ArrayList<>();
        bombs = new ArrayList<>();
        enemyBombs = new ArrayList<>(); // Add this line
        enemyBombs2 = new ArrayList<>(); // Add this line

        // Create the boss on the right side of the screen
        boss = new BossAlien(BOARD_WIDTH - 150, BOARD_HEIGHT / 2 - 50);

        // Create the horizontal player on the left side of the screen
        player = new HorizontalPlayer();
        player.setX(50);
        player.setY(BOARD_HEIGHT / 2);
    }

    private void drawMap(Graphics g) {
        // Draw horizontally scrolling starfield background
        int scrollOffset = (int) (this.scrollOffset) % BLOCKWIDTH;
        int baseCol = (int) (this.scrollOffset) / BLOCKWIDTH;
        int colsNeeded = (BOARD_WIDTH / BLOCKWIDTH) + 2;

        for (int screenCol = 0; screenCol < colsNeeded; screenCol++) {
            int mapCol = (baseCol + screenCol) % MAP[0].length;
            int x = (screenCol * BLOCKWIDTH) - scrollOffset;

            if (x > BOARD_WIDTH || x < -BLOCKWIDTH) {
                continue;
            }

            for (int row = 0; row < MAP.length; row++) {
                if (MAP[row][mapCol] == 1) {
                    int y = row * BLOCKHEIGHT;
                    drawStarCluster(g, x, y, BLOCKWIDTH, BLOCKHEIGHT);
                }
            }
        }
    }

    private void drawStarCluster(Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.WHITE);

        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g.fillOval(centerX - 2, centerY - 2, 4, 4);

        g.fillOval(centerX - 15, centerY - 10, 2, 2);
        g.fillOval(centerX + 12, centerY - 8, 2, 2);
        g.fillOval(centerX - 8, centerY + 12, 2, 2);
        g.fillOval(centerX + 10, centerY + 15, 2, 2);

        g.fillOval(centerX - 20, centerY + 5, 1, 1);
        g.fillOval(centerX + 18, centerY - 15, 1, 1);
        g.fillOval(centerX - 5, centerY - 18, 1, 1);
        g.fillOval(centerX + 8, centerY + 20, 1, 1);
    }

    private void drawBoss(Graphics g) {
        if (boss.isVisible()) {
            g.drawImage(boss.getImage(), boss.getX(), boss.getY(), this);

            // Draw boss health bar
            int healthBarWidth = 100;
            int healthBarHeight = 10;
            int healthBarX = boss.getX() + (boss.getImage().getWidth(null) - healthBarWidth) / 2;
            int healthBarY = boss.getY() - 20;

            g.setColor(Color.DARK_GRAY);
            g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);

            g.setColor(new Color(128, 0, 255));
            int maxHealth = 300; // Changed from 400 to 300 to match BossAlien's actual HP
            int currentHealthWidth = (int) ((boss.getHealth() / (double) maxHealth) * healthBarWidth);
            g.fillRect(healthBarX, healthBarY, currentHealthWidth, healthBarHeight);

            g.setColor(Color.WHITE);
            g.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        }
    }

    private void drawSmallAliens(Graphics g) {
        for (gdd.sprite.Enemy alien : smallAliens) {
            if (alien.isVisible()) {
                g.drawImage(alien.getImage(), alien.getX(), alien.getY(), this);
            }
        }
    }

    private void drawPlayer(Graphics g) {
        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {
            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g) {
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawBombing(Graphics g) {
        for (Bomb bomb : bombs) {
            if (!bomb.isDestroyed()) {
                g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), this);
            }
        }
    }

    private void drawEnemyShots(Graphics g) {
        for (Shot shot : enemyShots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawExplosions(Graphics g) {
        List<Explosion> toRemove = new ArrayList<>();

        for (Explosion explosion : explosions) {
            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
        }

        explosions.removeAll(toRemove);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        // Display game info similar to Scene1
        g.setColor(Color.white);
        g.drawString("FRAME: " + frame, 10, 10);
        g.drawString("BOSS FIGHT", 10, 25);
        g.drawString("BOSS HP: " + boss.getHealth() + "/300", 10, 40); // Changed from /400 to /300

        g.setColor(Color.green);

        if (inGame) {
            drawMap(g);
            drawExplosions(g);
            drawBoss(g);
            drawSmallAliens(g);
            drawPlayer(g);
            drawShot(g);
            drawBombing(g);
            drawEnemyShots(g);
            drawEnemyBombs(g); // Add this line
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

        Font small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        String displayMessage = playerWon ? winMessage : message;
        g.drawString(displayMessage, (BOARD_WIDTH - fontMetrics.stringWidth(displayMessage)) / 2,
                BOARD_WIDTH / 2);
    }

    private void update() {
        // Increment horizontal scroll offset
        scrollOffset += scrollSpeed;

        // Update player position
        if (player.isVisible()) {
            player.act();
        }

        // Boss AI and movement logic
        updateBossAI();
        updateBossMovement();

        // Update small aliens
        updateSmallAliens();

        // Update shots
        updatePlayerShots();
        updateEnemyShots();

        // Update bombs
        updateBombs();
        updateEnemyBombs();

        // Check win condition
        if (boss.getHealth() <= 0 && !boss.isDying()) {
            var ii = new ImageIcon(IMG_EXPLOSION);
            boss.setImage(ii.getImage());
            boss.setDying(true);
        }
    }

    private void updateBossAI() {
        attackTimer++;

        if (attackCooldown > 0) {
            attackCooldown--;
        } else {
            switch (bossAttackPhase) {
                case 0: // Spread Bomb Pattern
                    if (attackTimer >= 90) {
                        int centerX = boss.getX() + boss.getImage().getWidth(null) / 2;
                        int centerY = boss.getY() + boss.getImage().getHeight(null);

                        // Create multiple bombs for spread pattern
                        for (int i = -2; i <= 2; i++) {
                            // Use the boss's getBomb() method to get a new bomb instance
                            BossAlien.Bomb bomb = boss.getBomb();
                            if (bomb.isDestroyed()) {
                                bomb.setDestroyed(false);
                                bomb.setX(centerX);
                                bomb.setY(centerY);
                                bombs.add(bomb);
                            }
                        }
                        attackTimer = 0;
                        phaseCounter++;

                        if (phaseCounter >= 3) {
                            bossAttackPhase = 1;
                            phaseCounter = 0;
                            attackCooldown = 60;
                        }
                    }
                    break;

                case 1: // Rapid Bomb Burst
                    if (attackTimer >= 30) { // Slower frequency for bombs
                        BossAlien.Bomb rapidBomb = boss.getBomb();
                        if (rapidBomb.isDestroyed()) {
                            rapidBomb.setDestroyed(false);
                            rapidBomb.setX(boss.getX() + boss.getImage().getWidth(null) / 2);
                            rapidBomb.setY(boss.getY() + boss.getImage().getHeight(null));
                            bombs.add(rapidBomb);
                        }
                        attackTimer = 0;
                        phaseCounter++;

                        if (phaseCounter >= 5) {
                            bossAttackPhase = 2;
                            phaseCounter = 0;
                            attackCooldown = 90;
                        }
                    }
                    break;

                case 2: // Homing Bombs
                    if (attackTimer >= 60) {
                        BossAlien.Bomb homingBomb = boss.getBomb();
                        if (homingBomb.isDestroyed()) {
                            homingBomb.setDestroyed(false);
                            int bossX = boss.getX() + boss.getImage().getWidth(null) / 2;
                            int bossY = boss.getY() + boss.getImage().getHeight(null);
                            homingBomb.setX(bossX);
                            homingBomb.setY(bossY);
                            bombs.add(homingBomb);
                        }

                        attackTimer = 0;
                        phaseCounter++;

                        if (phaseCounter >= 4) {
                            bossAttackPhase = 3;
                            phaseCounter = 0;
                            attackCooldown = 60;
                        }
                    }
                    break;

                case 3: // Spiral Bomb Pattern
                    if (attackTimer >= 40) {
                        BossAlien.Bomb spiralBomb = boss.getBomb();
                        if (spiralBomb.isDestroyed()) {
                            spiralBomb.setDestroyed(false);
                            int centerX = boss.getX() + boss.getImage().getWidth(null) / 2;
                            int centerY = boss.getY() + boss.getImage().getHeight(null);
                            spiralBomb.setX(centerX);
                            spiralBomb.setY(centerY);
                            bombs.add(spiralBomb);
                        }

                        attackTimer = 0;
                        phaseCounter++;

                        if (phaseCounter >= 6) {
                            bossAttackPhase = 0;
                            phaseCounter = 0;
                            attackCooldown = 120;
                        }
                    }
                    break;
            }
        }
    }

    private void updateBossMovement() {
        if (boss.isVisible()) {
            boss.act(0, player.getY());

            // Add boss shooting
            if (boss.shouldShoot()) {
                // Shot bossShot = boss.getShot(player.getX() + player.getImage().getWidth(null)
                // / 2,
                // player.getY() + player.getImage().getHeight(null) / 2);
                // enemyShots.add(bossShot);
                BossAlien.Bomb bomb = boss.getBomb();
                bombs.add(bomb);
            }

            // Check collision with player
            int bossX = boss.getX();
            int bossY = boss.getY();
            int bossW = boss.getImage().getWidth(null);
            int bossH = boss.getImage().getHeight(null);
            int playerX = player.getX();
            int playerY = player.getY();
            int playerW = player.getImage().getWidth(null);
            int playerH = player.getImage().getHeight(null);
            if (player.isVisible() &&
                    bossX < playerX + playerW && bossX + bossW > playerX &&
                    bossY < playerY + playerH && bossY + bossH > playerY) {
                var ii = new ImageIcon(IMG_EXPLOSION);
                player.setImage(ii.getImage());
                player.setDying(true);
                inGame = false;
            }
            // Boss spawns small aliens occasionally
            if (randomizer.nextInt(120) == 0) {
                gdd.sprite.Enemy newAlien;

                // Randomly choose between Alien1 and Alien2 (50% chance each)
                if (randomizer.nextBoolean()) {
                    newAlien = new gdd.sprite.Alien1(boss.getX() + boss.getImage().getWidth(null) / 2,
                            boss.getY() + boss.getImage().getHeight(null));
                } else {
                    newAlien = new gdd.sprite.Alien2(boss.getX() + boss.getImage().getWidth(null) / 2,
                            boss.getY() + boss.getImage().getHeight(null));
                }

                smallAliens.add(newAlien);
                alienVerticalDirections.put(newAlien, bossMovementRandom.nextInt(3) - 1);
            }
        }
    }

    private void updateSmallAliens() {
        List<gdd.sprite.Enemy> aliensToRemove = new ArrayList<>();
        for (gdd.sprite.Enemy alien : smallAliens) {
            if (alien.isVisible()) {
                Integer verticalDir = alienVerticalDirections.get(alien);
                if (verticalDir == null) {
                    verticalDir = bossMovementRandom.nextInt(3) - 1;
                    alienVerticalDirections.put(alien, verticalDir);
                }

                if (randomizer.nextInt(60) == 0) {
                    verticalDir = bossMovementRandom.nextInt(3) - 1;
                    alienVerticalDirections.put(alien, verticalDir);
                }

                // Animate alien (advance animation frame)
                alien.act(verticalDir);

                int newAlienY = alien.getY() + (verticalDir * 2);
                if (newAlienY > 0 && newAlienY < BOARD_HEIGHT - 30) {
                    alien.setY(newAlienY);
                }

                alien.setX(alien.getX() - (int) scrollSpeed);

                // Handle bomb dropping for different alien types
                if (randomizer.nextInt(180) == 0) {
                    if (alien instanceof gdd.sprite.Alien1) {
                        gdd.sprite.Alien1 alien1 = (gdd.sprite.Alien1) alien;
                        Alien1.Bomb alienBomb = alien1.getBomb();
                        if (alienBomb.isDestroyed()) {
                            alienBomb.setDestroyed(false);
                            int alienWidth = alien.getImage().getWidth(null);
                            int bombWidth = alienBomb.getImage().getWidth(null);
                            int bombX = alien.getX() + (alienWidth / 2) - (bombWidth / 2);
                            int bombY = alien.getY() + alien.getImage().getHeight(null);
                            alienBomb.setX(bombX);
                            alienBomb.setY(bombY);
                            alienBomb.setHorizontalMovement(true);
                            enemyBombs.add(alienBomb);
                        }
                    } else if (alien instanceof gdd.sprite.Alien2) {
                        gdd.sprite.Alien2 alien2 = (gdd.sprite.Alien2) alien;
                        Bomb2 alienBomb2 = alien2.getBomb();
                        if (alienBomb2.isDestroyed()) {
                            alienBomb2.setDestroyed(false);
                            int alienWidth = alien.getImage().getWidth(null);
                            int bombWidth = alienBomb2.getImage().getWidth(null);
                            int bombX = alien.getX() + (alienWidth / 2) - (bombWidth / 2);
                            int bombY = alien.getY() + alien.getImage().getHeight(null);
                            alienBomb2.setX(bombX);
                            alienBomb2.setY(bombY);
                            alienBomb2.setHorizontalMovement(true); // Add this line
                            enemyBombs2.add(alienBomb2);
                        }
                    }
                }

                // Check collision with player
                if (player.isVisible() && checkAlienCollision(alien, player)) {
                    var ii = new ImageIcon(IMG_EXPLOSION);
                    player.setImage(ii.getImage());
                    player.setDying(true);
                    inGame = false;
                }

                if (alien.getX() < -30 || alien.getY() > BOARD_HEIGHT) {
                    alien.die();
                    aliensToRemove.add(alien);
                }
            } else {
                aliensToRemove.add(alien);
            }
        }
        smallAliens.removeAll(aliensToRemove);
        alienVerticalDirections.keySet().removeAll(aliensToRemove);
    }

    private void updatePlayerShots() {
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                int x = shot.getX();
                x += 20;

                if (x > BOARD_WIDTH) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setX(x);
                }

                // Check collision with boss
                if (boss.isVisible() && shot.collidesWith(boss)) {
                    int bossX = boss.getX();
                    int bossY = boss.getY();

                    explosions.add(new Explosion(shot.getX(), shot.getY()));
                    AudioPlayer.playExplosionSound();
                    boss.decreaseHealth();

                    if (boss.getHealth() <= 0) {
                        explosions.add(new Explosion(bossX, bossY));
                        explosions.add(new Explosion(bossX + 30, bossY + 10));
                        explosions.add(new Explosion(bossX + 15, bossY + 20));
                        explosions.add(new Explosion(bossX - 15, bossY + 15));
                        explosions.add(new Explosion(bossX + 40, bossY - 10));
                    }

                    shot.die();
                    shotsToRemove.add(shot);
                }

                // Check collision with small aliens
                boolean hitAlien = false;
                for (gdd.sprite.Enemy alien : smallAliens) {
                    if (alien.isVisible() && shot.collidesWith(alien)) {
                        explosions.add(new Explosion(shot.getX(), shot.getY()));
                        AudioPlayer.playExplosionSound();
                        alien.die();
                        shot.die();
                        shotsToRemove.add(shot);
                        hitAlien = true;
                        break;
                    }
                }
            }
        }
        shots.removeAll(shotsToRemove);
    }

    private void updateEnemyShots() {
        List<Shot> enemyShotsToRemove = new ArrayList<>();
        for (Shot shot : enemyShots) {
            if (shot.isVisible()) {
                shot.act();

                if (shot.getX() < -30 || shot.getX() > BOARD_WIDTH + 30 ||
                        shot.getY() < -30 || shot.getY() > BOARD_HEIGHT + 30) {
                    shot.die();
                    enemyShotsToRemove.add(shot);
                }

                // Check collision with player using proper bounds
                if (player.isVisible() && checkCollision(shot, player)) {
                    var ii = new ImageIcon(IMG_EXPLOSION);
                    player.setImage(ii.getImage());
                    player.setDying(true);
                    shot.die();
                    enemyShotsToRemove.add(shot);
                }
            } else {
                enemyShotsToRemove.add(shot);
            }
        }
        enemyShots.removeAll(enemyShotsToRemove);
    }

    private void updateBombs() {
        List<Bomb> bombsToRemove = new ArrayList<>();
        for (Bomb bomb : bombs) {
            if (!bomb.isDestroyed()) {
                // Move bomb towards the left (towards player)
                bomb.act();

                // Check collision with player using proper bounds
                if (player.isVisible() && checkBombCollision(bomb, player)) {
                    var ii = new ImageIcon(IMG_EXPLOSION);
                    player.setImage(ii.getImage());
                    player.setDying(true);
                    bomb.setDestroyed(true);
                    bombsToRemove.add(bomb);
                }

                // Remove bomb if it goes off screen
                if (bomb.getX() < -30 || bomb.getY() > BOARD_HEIGHT || bomb.getY() < -30) {
                    bomb.setDestroyed(true);
                    bombsToRemove.add(bomb);
                }
            } else {
                bombsToRemove.add(bomb);
            }
        }
        bombs.removeAll(bombsToRemove);
    }

    // Helper method for proper collision detection
    private boolean checkCollision(Shot shot, HorizontalPlayer player) {
        int shotX = shot.getX();
        int shotY = shot.getY();
        int shotWidth = shot.getImage().getWidth(null);
        int shotHeight = shot.getImage().getHeight(null);

        int playerX = player.getX();
        int playerY = player.getY();
        int playerWidth = player.getImage().getWidth(null);
        int playerHeight = player.getImage().getHeight(null);

        return shotX < playerX + playerWidth &&
                shotX + shotWidth > playerX &&
                shotY < playerY + playerHeight &&
                shotY + shotHeight > playerY;
    }

    // Helper method for proper bomb collision detection
    private boolean checkBombCollision(Bomb bomb, HorizontalPlayer player) {
        int bombX = bomb.getX();
        int bombY = bomb.getY();
        int bombWidth = bomb.getImage().getWidth(null);
        int bombHeight = bomb.getImage().getHeight(null);

        int playerX = player.getX();
        int playerY = player.getY();
        int playerWidth = player.getImage().getWidth(null);
        int playerHeight = player.getImage().getHeight(null);

        return bombX < playerX + playerWidth &&
                bombX + bombWidth > playerX &&
                bombY < playerY + playerHeight &&
                bombY + bombHeight > playerY;
    }

    // Helper method for proper alien collision detection
    private boolean checkAlienCollision(gdd.sprite.Enemy alien, HorizontalPlayer player) {
        int alienX = alien.getX();
        int alienY = alien.getY();
        int alienWidth = alien.getImage().getWidth(null);
        int alienHeight = alien.getImage().getHeight(null);

        int playerX = player.getX();
        int playerY = player.getY();
        int playerWidth = player.getImage().getWidth(null);
        int playerHeight = player.getImage().getHeight(null);

        return alienX < playerX + playerWidth &&
                alienX + alienWidth > playerX &&
                alienY < playerY + playerHeight &&
                alienY + alienHeight > playerY;
    }

    private void doGameCycle() {
        frame++;
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("BossScene.keyPressed: " + e.getKeyCode());

            player.keyPressed(e);

            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE && inGame) {
                System.out.println("Shots: " + shots.size());
                if (shots.size() < 4) { // Limit shots for boss scene
                    int x = player.getX() + 24; // Adjust for HorizontalPlayer width
                    int y = player.getY() + 8; // Center vertically
                    shots.add(new Shot(x, y - 30, 0)); // Use clipNo = 2 for larger bullets
                    shots.add(new Shot(x, y - 10, 0)); // Use clipNo = 2 for larger bullets
                    shots.add(new Shot(x, y + 10, 0)); // Use clipNo = 2 for larger bullets
                    shots.add(new Shot(x, y + 30, 0)); // Use clipNo = 2 for larger bullets
                    AudioPlayer.playShootSound(); // Play shot sound
                }
            }
        }
    }

    // Move all these methods inside the BossScene class
    private void updateEnemyBombs() {
        // Handle Alien1 bombs
        List<Alien1.Bomb> enemyBombsToRemove = new ArrayList<>();
        for (Alien1.Bomb bomb : enemyBombs) {
            if (!bomb.isDestroyed()) {
                bomb.act(); // Move the bomb

                // Check collision with player
                if (player.isVisible() && checkEnemyBombCollision(bomb, player)) {
                    var ii = new ImageIcon(IMG_EXPLOSION);
                    player.setImage(ii.getImage());
                    player.setDying(true);
                    bomb.setDestroyed(true);
                    enemyBombsToRemove.add(bomb);
                    inGame = false;
                }

                // Remove bomb if it goes off screen
                if (bomb.getX() < -30 || bomb.getY() > BOARD_HEIGHT || bomb.getY() < -30) {
                    bomb.setDestroyed(true);
                    enemyBombsToRemove.add(bomb);
                }
            } else {
                enemyBombsToRemove.add(bomb);
            }
        }
        enemyBombs.removeAll(enemyBombsToRemove);

        // Handle Alien2 bombs
        List<Bomb2> enemyBombs2ToRemove = new ArrayList<>();
        for (Bomb2 bomb2 : enemyBombs2) {
            if (!bomb2.isDestroyed()) {
                bomb2.act(); // Move the bomb

                // Check collision with player
                if (player.isVisible() && checkEnemyBomb2Collision(bomb2, player)) {
                    var ii = new ImageIcon(IMG_EXPLOSION);
                    player.setImage(ii.getImage());
                    player.setDying(true);
                    bomb2.setDestroyed(true);
                    enemyBombs2ToRemove.add(bomb2);
                    inGame = false;
                }

                // Remove bomb if it goes off screen
                if (bomb2.getX() < -30 || bomb2.getY() > BOARD_HEIGHT || bomb2.getY() < -30) {
                    bomb2.setDestroyed(true);
                    enemyBombs2ToRemove.add(bomb2);
                }
            } else {
                enemyBombs2ToRemove.add(bomb2);
            }
        }
        enemyBombs2.removeAll(enemyBombs2ToRemove);
    }

    private boolean checkEnemyBombCollision(Alien1.Bomb bomb, HorizontalPlayer player) {
        int bombX = bomb.getX();
        int bombY = bomb.getY();
        int bombWidth = bomb.getImage().getWidth(null);
        int bombHeight = bomb.getImage().getHeight(null);

        int playerX = player.getX();
        int playerY = player.getY();
        int playerWidth = player.getImage().getWidth(null);
        int playerHeight = player.getImage().getHeight(null);

        return bombX < playerX + playerWidth &&
                bombX + bombWidth > playerX &&
                bombY < playerY + playerHeight &&
                bombY + bombHeight > playerY;
    }

    private boolean checkEnemyBomb2Collision(Bomb2 bomb2, HorizontalPlayer player) {
        int bombX = bomb2.getX();
        int bombY = bomb2.getY();
        int bombWidth = bomb2.getImage().getWidth(null);
        int bombHeight = bomb2.getImage().getHeight(null);

        int playerX = player.getX();
        int playerY = player.getY();
        int playerWidth = player.getImage().getWidth(null);
        int playerHeight = player.getImage().getHeight(null);

        return bombX < playerX + playerWidth &&
                bombX + bombWidth > playerX &&
                bombY < playerY + playerHeight &&
                bombY + bombHeight > playerY;
    }

    private void drawEnemyBombs(Graphics g) {
        // Draw Alien1 bombs
        for (Alien1.Bomb bomb : enemyBombs) {
            if (!bomb.isDestroyed()) {
                g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), this);
            }
        }

        // Draw Alien2 bombs
        for (Bomb2 bomb2 : enemyBombs2) {
            if (!bomb2.isDestroyed()) {
                g.drawImage(bomb2.getImage(), bomb2.getX(), bomb2.getY(), this);
            }
        }
    }
} // This should be the final closing brace for the BossScene class