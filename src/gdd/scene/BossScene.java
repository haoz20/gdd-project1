package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
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
    private List<Bomb> bombs = new ArrayList<>();
    private HorizontalPlayer player;
    private List<gdd.sprite.Alien1> smallAliens = new ArrayList<>();
    private List<Shot> enemyShots = new ArrayList<>();

    // Add these variables for scrolling map
    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;
    final int BLOCKS_TO_DRAW = BOARD_HEIGHT / BLOCKHEIGHT;
    private double scrollSpeed = 2;
    private double scrollOffset = 0; // Horizontal scroll offset

    // Add variables for boss movement
    private int bossMovementCounter = 0;
    private int bossVerticalDirection = 1;
    private int bossHorizontalDirection = 1;
    private Random bossMovementRandom = new Random();

    // Add variables for small alien movement
    private Map<gdd.sprite.Alien1, Integer> alienVerticalDirections = new HashMap<>();

    // Add variables for modern boss shooting patterns
    private int bossAttackPhase = 0; // 0=spread shot, 1=laser burst, 2=homing shots, 3=spiral pattern
    private int attackTimer = 0;
    private int attackCooldown = 0;
    private int phaseCounter = 0;

    // Define a more random and cool star map pattern
    private final int[][] MAP = {
            { 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0 },
            { 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1 },
            { 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0 },
            { 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1 },
            { 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0 },
            { 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1 },
            { 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0 },
            { 0, 1, 0, 0, 0, 1, 0, 1, 1, 0, 0, 1 },
            { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 },
            { 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1 },
            { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 },
            { 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1 }
    };

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
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/scene1.wav"; // Reuse the same audio for now
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
//        initAudio();
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
        // Create the boss on the right side of the screen
        boss = new BossAlien(BOARD_WIDTH - 150, BOARD_HEIGHT / 2 - 50);
        // Create the player on the left side of the screen
        player = new HorizontalPlayer();
        player.setX(50); // Position player on the left side
        player.setY(BOARD_HEIGHT / 2); // Position player in the middle vertically
    }

    private void drawStars(Graphics g) {
        // Draw horizontally scrolling starfield background (right to left)
        int scrollOffset = (int) (frame * scrollSpeed) % BLOCKWIDTH;
        int baseCol = (int) (frame * scrollSpeed) / BLOCKWIDTH;
        int colsNeeded = (BOARD_WIDTH / BLOCKWIDTH) + 2; // +2 for smooth scrolling
        int rowsNeeded = (BOARD_HEIGHT / BLOCKHEIGHT) + 2;

        for (int screenRow = 0; screenRow < rowsNeeded; screenRow++) {
            int mapRow = (screenRow) % MAP.length;
            int y = (screenRow * BLOCKHEIGHT);
            if (y > BOARD_HEIGHT || y < -BLOCKHEIGHT) {
                continue;
            }
            for (int screenCol = 0; screenCol < colsNeeded; screenCol++) {
                int mapCol = (baseCol + screenCol) % MAP[mapRow].length;
                int x = BOARD_WIDTH - ((screenCol * BLOCKWIDTH) - scrollOffset);
                if (MAP[mapRow][mapCol] == 1) {
                    drawStarCluster(g, x, y, BLOCKWIDTH, BLOCKHEIGHT);
                }
            }
        }
    }

    private void drawStarCluster(Graphics g, int x, int y, int width, int height) {
        // Set star color to white
        g.setColor(Color.WHITE);

        // Draw multiple stars in a cluster pattern
        // Main star (larger)
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g.fillOval(centerX - 2, centerY - 2, 4, 4);

        // Smaller surrounding stars
        g.fillOval(centerX - 15, centerY - 10, 2, 2);
        g.fillOval(centerX + 12, centerY - 8, 2, 2);
        g.fillOval(centerX - 8, centerY + 12, 2, 2);
        g.fillOval(centerX + 10, centerY + 15, 2, 2);

        // Tiny stars for more detail
        g.fillOval(centerX - 20, centerY + 5, 1, 1);
        g.fillOval(centerX + 18, centerY - 15, 1, 1);
        g.fillOval(centerX - 5, centerY - 18, 1, 1);
        g.fillOval(centerX + 8, centerY + 20, 1, 1);
    }

    private void drawBoss(Graphics g) {
        if (boss.isVisible()) {
            //
            g.drawImage(boss.getImage(), boss.getX(), boss.getY(), this);

            // Draw boss health bar
            int healthBarWidth = 100;
            int healthBarHeight = 10;
            int healthBarX = boss.getX() + (boss.getImage().getWidth(null) - healthBarWidth) / 2;
            int healthBarY = boss.getY() - 20;

            // Background of health bar
            g.setColor(Color.DARK_GRAY);
            g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);

            // Current health - unique purple color for boss
            g.setColor(new Color(128, 0, 255)); // Purple color for boss health
            int currentHealthWidth = (int) ((boss.getHealth() / 50.0) * healthBarWidth); // Updated for new max health
            g.fillRect(healthBarX, healthBarY, currentHealthWidth, healthBarHeight);

            // Health bar border
            g.setColor(Color.WHITE);
            g.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        }

        if (boss.isDying()) {
            boss.die();
            playerWon = true;
            inGame = false;
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

    private void drawSmallAliens(Graphics g) {
        for (gdd.sprite.Alien1 alien : smallAliens) {
            if (alien.isVisible()) {
                g.drawImage(alien.getImage(), alien.getX(), alien.getY(), this);
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        // Retro-style text display
        Font retroFont = new Font("Courier New", Font.BOLD, 16);
        g.setFont(retroFont);
        g.setColor(new Color(255, 255, 0)); // Bright Yellow

        // Draw pixelated text border for retro effect
        g.drawString("BOSS FIGHT", 12, 22);
        g.drawString("BOSS FIGHT", 8, 22);
        g.drawString("BOSS FIGHT", 10, 20);
        g.drawString("BOSS FIGHT", 10, 24);

        // Main text
        g.setColor(Color.BLUE);
        g.drawString("BOSS FIGHT", 10, 22);

        // Health display with retro styling
        g.setColor(new Color(255, 255, 0)); // Yellow for health text
        g.drawString("BOSS HP: " + boss.getHealth() + "/50", 10, 45);

        if (inGame) {
            drawStars(g);
            drawExplosions(g);
            drawBoss(g);
            drawPlayer(g);
            drawShot(g);
            drawBombing(g);
            drawSmallAliens(g);
            drawEnemyShots(g);
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

        // boss shooting patterns
        attackTimer++;

        if (attackCooldown > 0) {
            attackCooldown--;
        } else {
            switch (bossAttackPhase) {
                case 0: // Spread Shot Pattern
                    if (attackTimer >= 90) { // Every 1.5 seconds
                        // Fire 5 shots in a spread pattern
                        int centerX = boss.getX() + boss.getImage().getWidth(null) / 2;
                        int centerY = boss.getY() + boss.getImage().getHeight(null);

                        for (int i = -2; i <= 2; i++) {
                            Shot spreadShot = new Shot(centerX, centerY);
                            // Reduce velocity from -8 to -4
                            spreadShot.setSpreadVelocity(-4, 1 + i * 1); // leftward with vertical spread
                            enemyShots.add(spreadShot);
                        }
                        attackTimer = 0;
                        phaseCounter++;

                        if (phaseCounter >= 3) { // After 3 spread attacks
                            bossAttackPhase = 1;
                            phaseCounter = 0;
                            attackCooldown = 60; // 1 second cooldown
                        }
                    }
                    break;

                case 1: // Rapid Laser Burst
                    if (attackTimer >= 15) { // Every 0.25 seconds
                        // Fire rapid straight shots
                        Shot laserShot = new Shot(boss.getX() + boss.getImage().getWidth(null) / 2,
                                boss.getY() + boss.getImage().getHeight(null));
                        laserShot.setSpreadVelocity(-8, 0); // Reduce from -15 to -8
                        enemyShots.add(laserShot);
                        attackTimer = 0;
                        phaseCounter++;

                        if (phaseCounter >= 8) { // After 8 rapid shots
                            bossAttackPhase = 2;
                            phaseCounter = 0;
                            attackCooldown = 90; // 1.5 second cooldown
                        }
                    }
                    break;

                case 2: // Homing Shots (aim toward player)
                    if (attackTimer >= 60) { // Every 1 second
                        int bossX = boss.getX() + boss.getImage().getWidth(null) / 2;
                        int bossY = boss.getY() + boss.getImage().getHeight(null);
                        int playerX = player.getX() + 15; // Player center
                        int playerY = player.getY() + 15;

                        // Calculate direction to player
                        double angle = Math.atan2(playerY - bossY, playerX - bossX);
                        int velX = (int) (Math.cos(angle) * 4); // Reduce from 8 to 4
                        int velY = (int) (Math.sin(angle) * 4); // Reduce from 8 to 4

                        Shot homingShot = new Shot(bossX, bossY);
                        homingShot.setSpreadVelocity(velX, velY);
                        enemyShots.add(homingShot);

                        attackTimer = 0;
                        phaseCounter++;

                        if (phaseCounter >= 4) { // After 4 homing shots
                            bossAttackPhase = 3;
                            phaseCounter = 0;
                            attackCooldown = 60;
                        }
                    }
                    break;

                case 3: // Spiral Pattern
                    if (attackTimer >= 20) { // Every 0.33 seconds
                        int centerX = boss.getX() + boss.getImage().getWidth(null) / 2;
                        int centerY = boss.getY() + boss.getImage().getHeight(null);

                        // Create spiral shots
                        double spiralAngle = (phaseCounter * 45) * Math.PI / 180; // 45 degrees per shot
                        int velX = (int) (Math.cos(spiralAngle) * 3) - 2; // Reduce from 6 to 3, bias from -3 to -2
                        int velY = (int) (Math.sin(spiralAngle) * 3); // Reduce from 6 to 3

                        Shot spiralShot = new Shot(centerX, centerY);
                        spiralShot.setSpreadVelocity(velX, velY);
                        enemyShots.add(spiralShot);

                        attackTimer = 0;
                        phaseCounter++;

                        if (phaseCounter >= 8) { // After 8 spiral shots
                            bossAttackPhase = 0; // Return to spread shot
                            phaseCounter = 0;
                            attackCooldown = 120; // 2 second cooldown before next cycle
                        }
                    }
                    break;
            }
        }

        // Boss movement: up/down and forward/backward randomly
        if (boss.isVisible()) {
            // Change direction randomly every 30 frames
            if (bossMovementCounter % 30 == 0) {
                bossVerticalDirection = bossMovementRandom.nextInt(3) - 1; // -1, 0, or 1 (up, stay, down)
                bossHorizontalDirection = bossMovementRandom.nextInt(3) - 1; // -1, 0, or 1 (backward, stay, forward)
            }
            bossMovementCounter++;

            // Apply boss movement
            int newBossY = boss.getY() + (bossVerticalDirection * 3);
            int newBossX = boss.getX() + (bossHorizontalDirection * 2) - (int) scrollSpeed;

            // Keep boss within screen bounds - allow full screen vertical movement
            if (newBossY > 10 && newBossY < BOARD_HEIGHT - boss.getImage().getHeight(null) - 10) {
                boss.setY(newBossY);
            } else {
                // If boss would go off-screen, reverse direction
                bossVerticalDirection *= -1;
                boss.setY(Math.max(10, Math.min(BOARD_HEIGHT - boss.getImage().getHeight(null) - 10, newBossY)));
            }

            // Allow boss to move slightly forward/backward against scroll, but keep on
            // right side
            if (newBossX > BOARD_WIDTH - 200 && newBossX < BOARD_WIDTH - 50) {
                boss.setX(newBossX);
            } else {
                // If boss would go off-screen, push it back in
                if (newBossX <= BOARD_WIDTH - 200)
                    boss.setX(BOARD_WIDTH - 199);
                if (newBossX >= BOARD_WIDTH - 50)
                    boss.setX(BOARD_WIDTH - 51);
            }

            // Boss spawns small aliens occasionally
            if (randomizer.nextInt(120) == 0) { // ~every 2 seconds at 60fps
                gdd.sprite.Alien1 newAlien = new gdd.sprite.Alien1(boss.getX() + boss.getImage().getWidth(null) / 2,
                        boss.getY() + boss.getImage().getHeight(null));
                smallAliens.add(newAlien);
                // Assign a random vertical direction to the new alien
                alienVerticalDirections.put(newAlien, bossMovementRandom.nextInt(3) - 1);
            }
        }

        // Update small aliens - they move up/down
        List<gdd.sprite.Alien1> aliensToRemove = new ArrayList<>();
        for (gdd.sprite.Alien1 alien : smallAliens) {
            if (alien.isVisible()) {
                // Get or create vertical direction for this alien
                Integer verticalDir = alienVerticalDirections.get(alien);
                if (verticalDir == null) {
                    verticalDir = bossMovementRandom.nextInt(3) - 1;
                    alienVerticalDirections.put(alien, verticalDir);
                }

                // Change direction occasionally
                if (randomizer.nextInt(60) == 0) {
                    verticalDir = bossMovementRandom.nextInt(3) - 1;
                    alienVerticalDirections.put(alien, verticalDir);
                }

                // Apply vertical movement
                int newAlienY = alien.getY() + (verticalDir * 2);
                if (newAlienY > 0 && newAlienY < BOARD_HEIGHT - 30) {
                    alien.setY(newAlienY);
                }

                // Apply horizontal movement (affected by scroll)
                alien.setX(alien.getX() - (int) scrollSpeed);

                // Small aliens shoot at random - MODIFIED to shoot directly leftward
                if (randomizer.nextInt(180) == 0) { // once every 3 seconds
                    int alienX = alien.getX();
                    int alienY = alien.getY() + 10;
                    
                    Shot enemyShot = new Shot(alienX, alienY);
                    enemyShot.setSpreadVelocity(-4, 0); // Straight leftward movement
                    enemyShots.add(enemyShot);
                }

                // Remove if off screen
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

        // Update enemy shots with enhanced movement
        List<Shot> enemyShotsToRemove = new ArrayList<>();
        for (Shot shot : enemyShots) {
            if (shot.isVisible()) {
                shot.act(); // Use the shot's own movement logic

                // Remove if off screen
                if (shot.getX() < -30 || shot.getX() > BOARD_WIDTH + 30 ||
                        shot.getY() < -30 || shot.getY() > BOARD_HEIGHT + 30) {
                    shot.die();
                    enemyShotsToRemove.add(shot);
                }

                // Check for collision with player
                if (player.isVisible() && shot.collidesWith(player)) {
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

        // Shot movement
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                int x = shot.getX();
                x += 20; // Move the shot rightward by 20 pixels per frame

                if (x > BOARD_WIDTH) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setX(x); // Update the shot's position
                }

                // Check for collision with boss
                if (boss.isVisible() && shot.collidesWith(boss)) {
                    int bossX = boss.getX();
                    int bossY = boss.getY();

                    // Create explosion at hit location
                    explosions.add(new Explosion(shot.getX(), shot.getY()));

                    // Decrease boss health
                    boss.decreaseHealth();

                    // If boss is destroyed
                    if (boss.getHealth() <= 0) {
                        var ii = new ImageIcon(IMG_EXPLOSION);
                        boss.setImage(ii.getImage());
                        boss.setDying(true);
                        // Add more explosions for a more dramatic effect
                        explosions.add(new Explosion(bossX, bossY));
                        explosions.add(new Explosion(bossX + 30, bossY + 10));
                        explosions.add(new Explosion(bossX + 15, bossY + 20));
                        explosions.add(new Explosion(bossX - 15, bossY + 15));
                        explosions.add(new Explosion(bossX + 40, bossY - 10));
                    }

                    shot.die();
                    shotsToRemove.add(shot);
                }

                // Check for collision with small aliens
                boolean hitAlien = false;
                for (gdd.sprite.Alien1 alien : smallAliens) {
                    if (alien.isVisible() && shot.collidesWith(alien)) {
                        // Create explosion at hit location
                        explosions.add(new Explosion(shot.getX(), shot.getY()));

                        // Destroy the alien
                        alien.die();

                        // Mark shot for removal
                        shot.die();
                        shotsToRemove.add(shot);

                        hitAlien = true;
                        break;
                    }
                }

                // Skip further collision checks if we already hit something
                if (hitAlien) {
                    continue;
                }
            }
        }
        shots.removeAll(shotsToRemove);

        // Bomb movement and collision
        List<Bomb> bombsToRemove = new ArrayList<>();
        for (Bomb bomb : bombs) {
            if (!bomb.isDestroyed()) {
                // Modified bomb movement: move leftward toward player instead of just falling
                bomb.setX(bomb.getX() - 5); // Move bomb leftward toward player
                bomb.setY(bomb.getY() + 2); // Still move slightly downward for realistic trajectory

                // Check for collision with player
                if (bomb.collidesWith(player)) {
                    var ii = new ImageIcon(IMG_EXPLOSION);
                    player.setImage(ii.getImage());
                    player.setDying(true);
                    bomb.setDestroyed(true);
                    bombsToRemove.add(bomb);
                }

                // Remove bombs that go off screen (left side or bottom)
                if (bomb.getX() < -30 || bomb.getY() > BOARD_HEIGHT) {
                    bomb.setDestroyed(true);
                    bombsToRemove.add(bomb);
                }
            }
        }
        bombs.removeAll(bombsToRemove);
    }

    private void doGameCycle() {
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
            int key = e.getKeyCode();

            // Pass movement keys to player object
            player.keyPressed(e);

            // Handle shooting separately
            if (key == KeyEvent.VK_SPACE) {
                if (inGame) {
                    // Fire a shot to the right (from player toward boss)
                    Shot shot = new Shot(player.getX() + PLAYER_WIDTH, player.getY() + PLAYER_HEIGHT / 2);
                    shots.add(shot);
                }
            }
        }
    }
}
