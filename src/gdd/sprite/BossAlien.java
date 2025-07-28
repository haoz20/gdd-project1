package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class BossAlien extends Enemy {

    private int health = 400; // Increased HP for longer boss fight
    private int moveDirection = 1;
    private int moveDirectionY = 1;
    private int moveCounter = 0;
    private Random random = new Random();

    public BossAlien(int x, int y) {
        super(x, y);
        initBoss(x, y);
    }

    private void initBoss(int x, int y) {
        this.x = x;
        this.y = y;


        // Use the dedicated boss sprite
        var ii = new ImageIcon(IMG_BOSS);
        setImage(ii.getImage());
    }


    public void act(int direction, int playerY) {
        // Boss moves horizontally as before
        if (moveCounter % 30 == 0) {
            moveDirection = random.nextInt(3) - 1; // -1, 0, or 1 (left, stay, right)
        }

        x += moveDirection * 3; // Move horizontally
        y += moveDirectionY * 3; // Move vertically

        // Keep boss within screen bounds horizontally
        if (x < 0) {
            x = 0;
            moveDirection = 1;
        } else if (x > BOARD_WIDTH - getImage().getWidth(null)) {
            x = BOARD_WIDTH - getImage().getWidth(null);
            moveDirection = -1;
        }

        // Keep boss within screen bounds vertically (limit to upper half of screen)
        int minY = 0;
        int maxY = BOARD_HEIGHT / 2 - getImage().getHeight(null);
        if (y < minY) {
            y = minY;
        } else if (y > maxY) {
            y = maxY;
        }

        moveCounter++;
    }

    public Bomb getBomb() {
        return new Bomb(this.x, this.y);
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
        private Rectangle[] clips = new Rectangle[] {
                new Rectangle(346, 62, 4, 4),
                new Rectangle(337, 60, 6, 8)
        };

        private boolean destroyed;

        public Bomb(int x, int y) {
            initBomb(x, y);
        }

        private void initBomb(int x, int y) {
            setDestroyed(false);
            // Use boss image width for centering
            int bossWidth = BossAlien.this.getImage().getWidth(null);
            int bombWidth = clips[0].width;
            this.x = x + (bossWidth / 2) - (bombWidth / 2); // Adjust bomb position relative to boss
            this.y = y;
            var bombImg = "src/images/sprites.png";
            var ii = new ImageIcon(bombImg);
            setImage(ii.getImage());
        }

        @Override
        public Image getImage() {
            Rectangle bound = clips[clipNo];
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
            frameNo++;
            if (frameNo >= frame) {
                frameNo = 0; // Reset the counter
                clipNo = (clipNo == 0) ? 1 : 0; // Alternate between clip0 and clip1
            }
        }
    }
}