package gdd.sprite;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

import static gdd.Global.*;


public class Alien2 extends Enemy {

    private Bomb2 bomb2;
    private int moveDirection = 1;
    private int moveCounter = 0;
    private int frameCounter = 0;
    private static final int FRAME_THRESHOLD = 10;
    private int clipNo = 0;
    private final Rectangle[] clips = new Rectangle[]{
            new Rectangle(594, 167, 50, 38),
            new Rectangle(679, 167, 50, 38)
    };

    public Alien2(int x, int y) {
        super(x, y);
        initAlien2(x, y);
    }

    private void initAlien2(int x, int y) {
        this.x = x;
        this.y = y;

        bomb2 = new Bomb2(x, y);

        ImageIcon ii = new ImageIcon(IMG_ENEMY);
//        Image scaledImage = ii.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        setImage(ii.getImage());
    }

    @Override
    public void act(int direction) {
        // Unique zigzag movement pattern
        if (moveCounter % 50 == 0) {
            moveDirection *= -1; // Change direction every 50 frames
        }

        x += moveDirection * 2; // Move horizontally
        y += 1; // Move slightly downward

        moveCounter++;

        if (x < 0 || x > 800) { // Assuming 800 is the screen width
            moveDirection *= -1; // Bounce back if hitting screen edges
        }

        frameCounter++;
        if (frameCounter >= FRAME_THRESHOLD) {
            frameCounter = 0; // Reset the counter
            clipNo = (clipNo == 0) ? 1 : 0; // Alternate between clip0 and clip1
        }
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

    public Bomb2 getBomb() {
        return bomb2;
    }

    public class Bomb2 extends Sprite {

        private boolean destroyed;
        private int clipNo = 0;
        private int frameNo = 0;
        private final int frame = 5; // Frame rate for bomb animation
        private Rectangle[] clips = new Rectangle[]{
                new Rectangle(648, 388, 22, 9),
                new Rectangle(648, 404, 22, 9)
        };

        public Bomb2(int x, int y) {

            initBomb(x, y);
        }

        private void initBomb(int x, int y) {

            setDestroyed(true);
//            int alienWidth = new ImageIcon(IMG_ENEMY).getIconWidth() * SCALE_FACTOR;
//            int bombWidth = new ImageIcon("src/images/bomb.png").getIconWidth();

            int alienWidth = Alien2.this.clips[0].width;
            int bombWidth = clips[0].width;
            this.x = x + (alienWidth / 2) - (bombWidth / 2); // Adjust bomb position relative to alien
            this.y = y;
            var bombImg = "src/images/aliens.png";
            var ii = new ImageIcon(bombImg);
            setImage(ii.getImage());
        }

        public void setDestroyed(boolean destroyed) {

            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {

            return destroyed;
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

        @Override
        public void act() {
            this.y += 2;

            frameNo++;
            if (frameNo >= frame) {
                frameNo = 0; // Reset the counter
                clipNo = (clipNo == 0) ? 1 : 0; // Alternate between clip0 and clip1
            }
        }// Bomb falls down
    }
}

