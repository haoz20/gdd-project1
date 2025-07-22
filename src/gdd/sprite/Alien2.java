package gdd.sprite;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import static gdd.Global.*;


public class Alien2 extends Enemy {

    private int moveDirection = 1;
    private int moveCounter = 0;
    private int frameCounter = 0;
    private static final int FRAME_THRESHOLD = 10;
    private int clipNo = 0;
    private final Rectangle[] clips = new Rectangle[] {
            new Rectangle(594, 167, 50, 38),
            new Rectangle(679, 167, 50, 38)
    };

    public Alien2(int x, int y) {
        super(x, y);
        initAlien2();
    }

    private void initAlien2() {
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
}
