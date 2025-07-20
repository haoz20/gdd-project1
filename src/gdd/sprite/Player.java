package gdd.sprite;

import org.w3c.dom.css.Rect;

import static gdd.Global.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 540;
    private int width;
    private int currentSpeed = 2;
    private int multiShotLevel = 1;

    private static final String STILL = "still";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private String action = STILL;



    private int clipNo = 0;
    private final Rectangle[] clips = new Rectangle[] {
            new Rectangle(152*SCALE_FACTOR, 48*SCALE_FACTOR, 16*SCALE_FACTOR, 32*SCALE_FACTOR), // 0: still
            new Rectangle(200*SCALE_FACTOR, 48*SCALE_FACTOR, 16*SCALE_FACTOR, 32*SCALE_FACTOR), // 1: left
            new Rectangle(224*SCALE_FACTOR, 48*SCALE_FACTOR, 16*SCALE_FACTOR, 32*SCALE_FACTOR) // 2: right
    };

    private Rectangle bounds = new Rectangle(175,135,16,32);

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        var ii = new ImageIcon("src/images/spites.png");

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);

        setX(START_X);
        setY(START_Y);
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public int setSpeed(int speed) {
        if (speed < 1) {
            speed = 1; // Ensure speed is at least 1
        }
        this.currentSpeed = speed;
        return currentSpeed;
    }

    @Override
    public Image getImage() {
        Rectangle bound = clips[clipNo];
        BufferedImage bImage = toBufferedImage(image);
        return bImage.getSubimage(bound.x, bound.y, bound.width, bound.height);
    }

    public int getMultiShotLevel() {
        return multiShotLevel;
    }

    public void setMultiShotLevel(int multiShotLevel) {
        this.multiShotLevel = multiShotLevel;
    }

    public void act() {
        x += dx;

        if (x <= 2) {
            x = 2;
        }

        if (x >= BOARD_WIDTH - 2 * width) {
            x = BOARD_WIDTH - 2 * width;
        }

        switch (action) {
            case STILL:
                clipNo = 0;
                break;
            case LEFT:
                clipNo = 1;
                break;
            case RIGHT:
                clipNo = 2;
                break;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            action = LEFT;
            dx = -currentSpeed;
        }

        if (key == KeyEvent.VK_RIGHT) {
            action = RIGHT;
            dx = currentSpeed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            action = STILL;
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            action = STILL;
            dx = 0;
        }
    }
}
