package gdd.sprite;

import gdd.Global;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Alien1 extends Enemy {

    private Bomb bomb;
    private int clipNo = 0;
    private int frameCounter = 0;
    private static final int FRAME_THRESHOLD = 10;
    private final Rectangle[] clips = new Rectangle[] {
            new Rectangle(766, 167, 52, 38),
            new Rectangle(851, 167, 52, 38)
    };

    public Alien1(int x, int y) {
        super(x, y);
        initEnemy(x, y);
        int frame = 0;
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        var ii = new ImageIcon(IMG_ENEMY);

        // Scale the image to use the global scaling factor
//        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
//                ii.getIconHeight() * SCALE_FACTOR,
//                java.awt.Image.SCALE_SMOOTH);
        setImage(ii.getImage());
    }

    public void act(int direction) {
        this.y ++;

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

    public Bomb getBomb() {

        return bomb;
    }

    public class Bomb extends Sprite {

        private boolean destroyed;

        public Bomb(int x, int y) {

            initBomb(x, y);
        }

        private void initBomb(int x, int y) {

            setDestroyed(true);
//            int alienWidth = new ImageIcon(IMG_ENEMY).getIconWidth() * SCALE_FACTOR;
//            int bombWidth = new ImageIcon("src/images/bomb.png").getIconWidth();

            this.x = x;
            this.y = y;

            var bombImg = "src/images/bomb.png";
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
        public void act() {
            this.y += 2; // Bomb falls down
        }
    }
}
