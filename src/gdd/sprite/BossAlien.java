package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class BossAlien extends Enemy {

    private int health = 300; // Increased HP for longer boss fight
    private int moveDirection = 1;
    private int moveDirectionY = 1;
    private int moveCounter = 0;
    private int shootTimer = 0; // Add shooting timer
    private Random random = new Random();
    private Bomb bomb; // Boss-specific bomb

    private Rectangle bossBounds = new Rectangle(4, 4, 120, 111); // Define bounds for boss movement

    public BossAlien(int x, int y) {
        super(x, y);
        initBoss(x, y);
    }

    private void initBoss(int x, int y) {
        this.x = x;
        this.y = y;
        bomb = new Bomb(x, y);

        // Use the dedicated boss sprite
        var ii = new ImageIcon(IMG_BOSS);
        setImage(ii.getImage());
    }

    @Override
    public Image getImage() {
        Rectangle bound = bossBounds;
        BufferedImage bImage = toBufferedImage(image);

        // Check if the bounds are within the image
        int maxX = bound.x + bound.width;
        int maxY = bound.y + bound.height;

        if (maxX > bImage.getWidth() || maxY > bImage.getHeight()) {
            // If bounds exceed image, return the full image or a safe portion
            return bImage;
        }

        return bImage.getSubimage(bound.x, bound.y, bound.width, bound.height);
    }

    public void act(int direction, int playerY) {
        // Boss moves horizontally within right half of screen
        if (moveCounter % 30 == 0) {
            moveDirection = random.nextInt(3) - 1; // -1, 0, or 1 (left, stay, right)
        }

        x += moveDirection * 3; // Move horizontally
        y += moveDirectionY * 3; // Move vertically

        // Keep boss within right half of screen horizontally
        int minX = BOARD_WIDTH / 2; // Start from middle of screen
        int maxX = BOARD_WIDTH - getImage().getWidth(null);

        if (x < minX) {
            x = minX;
            moveDirection = 1; // Force rightward movement
        } else if (x > maxX) {
            x = maxX;
            moveDirection = -1; // Force leftward movement
        }

        // Keep boss within screen bounds vertically (limit to upper 25% of screen)
        int minY = 0;
        int maxY = BOARD_HEIGHT / 4 - getImage().getHeight(null);
        if (y < minY) {
            y = minY;
        } else if (y > maxY) {
            y = maxY;
        }

        moveCounter++;
        shootTimer++; // Increment shoot timer
    }

    // Add shooting methods
    public boolean shouldShoot() {
        if (shootTimer >= 45) { // Shoot every 45 frames (0.75 seconds)
            shootTimer = 0;
            return true;
        }
        return false;
    }

    public Shot getShot(int playerX, int playerY) {
        // Calculate direction toward player
        int shotX = this.x;
        int shotY = this.y + getImage().getHeight(null) / 2;

        Shot shot = new Shot(shotX, shotY);

        // Calculate velocity toward player
        double deltaX = playerX - shotX;
        double deltaY = playerY - shotY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance > 0) {
            double speed = 6.0; // Shot speed
            int velocityX = (int) ((deltaX / distance) * speed);
            int velocityY = (int) ((deltaY / distance) * speed);
            shot.setSpreadVelocity(velocityX, velocityY);
        }

        return shot;
    }

    public Bomb getBomb() {
        return bomb; // Return the boss-specific bomb
    }

    public int getHealth() {
        return health;
    }

    public void decreaseHealth() {
        health--;
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    public class Bomb extends Sprite {
        private int frameNo = 0;
        private int frame = 5; // Adjust frame rate for bomb animation

        private int clipNo = 0;
        private Rectangle bossbomb = new Rectangle(30, 148, 20, 20);

        private boolean destroyed;
        private int velocityX = 0;
        private int velocityY = 0;

        public Bomb(int x, int y) {
            initBomb(x, y);
        }

        private void initBomb(int x, int y) {
            setDestroyed(false);
            // Use boss image width for centering
            int bossWidth = BossAlien.this.getImage().getWidth(null);
            int bombWidth = bossbomb.width;
            this.x = x + (bossWidth / 2) - (bombWidth / 2); // Adjust bomb position relative to boss
            this.y = y;
            var bombImg = "src/images/finalBoss.png";
            var ii = new ImageIcon(bombImg);
            setImage(ii.getImage());
        }

        @Override
        public Image getImage() {
            Rectangle bound = bossbomb;
            BufferedImage bImage = toBufferedImage(image);

            // Check if the bounds are within the image
            int maxX = bound.x + bound.width;
            int maxY = bound.y + bound.height;

            if (maxX > bImage.getWidth() || maxY > bImage.getHeight()) {
                // If bounds exceed image, return the full image or a safe portion
                return bImage;
            }

            return bImage.getSubimage(bound.x, bound.y, bound.width, bound.height);
        }

        public void setDestroyed(boolean destroyed) {
            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {
            return destroyed;
        }

        @Override
        public void act() {
            this.y += 3; // Boss bombs fall faster
            // frameNo++;
            // if (frameNo >= frame) {
            // frameNo = 0; // Reset the counter
            // clipNo = (clipNo == 0) ? 1 : 0; // Alternate between clip0 and clip1
            // }
            this.x += velocityX;
            this.y += velocityY;
        }
    }
}
