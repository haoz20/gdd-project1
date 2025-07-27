package gdd.sprite;

import static gdd.Global.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class HorizontalPlayer extends Player {
    private static final int START_X = 270;
    private static final int START_Y = 540;
    private static final int LOCAL_WIDTH = 24 * SCALE_FACTOR;
    private static final int LOCAL_HEIGHT = 32 * SCALE_FACTOR;
    private static final String STILL = "still";
    private static final String UP = "up";
    private static final String DOWN = "down";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private String action = STILL;

    private int clipNo = 0;
    private final Rectangle[] clips = new Rectangle[]{
            new Rectangle(24 * SCALE_FACTOR, 32 * SCALE_FACTOR, 24 * SCALE_FACTOR, 16 * SCALE_FACTOR), // 0:  still
            new Rectangle(56 * SCALE_FACTOR, 32 * SCALE_FACTOR, 24 * SCALE_FACTOR, 16 * SCALE_FACTOR), // 1:  go
            new Rectangle(88 * SCALE_FACTOR, 32 * SCALE_FACTOR, 24 * SCALE_FACTOR, 16 * SCALE_FACTOR),// 2: up
            new Rectangle(120 * SCALE_FACTOR, 32 * SCALE_FACTOR, 24 * SCALE_FACTOR, 16 * SCALE_FACTOR) // 3: down
    };

    private int frameCounter = 0;
    private final int FRAME_THRESHOLD = 10;

    private int maxSpeed = 8; // Default, can be set from Scene1

    public HorizontalPlayer() {
        super();
        initPlayer();
    }

    private void initPlayer() {
        var ii = new ImageIcon(IMG_PLAYER);
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
        setX(START_X);
        setY(START_Y);
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @Override
    public Image getImage() {
        Rectangle bound = clips[clipNo];
        BufferedImage bImage = toBufferedImage(image);
        return bImage.getSubimage(bound.x, bound.y, bound.width, bound.height);
    }

    @Override
    public void act() {
        // Clamp dx and dy to maxSpeed
        if (dx > maxSpeed) dx = maxSpeed;
        if (dx < -maxSpeed) dx = -maxSpeed;
        if (dy > maxSpeed) dy = maxSpeed;
        if (dy < -maxSpeed) dy = -maxSpeed;

        x += dx;
        y += dy;
        // Horizontal bounds
        if (x <= 2) x = 2;
        if (x >= BOARD_WIDTH - LOCAL_WIDTH) x = BOARD_WIDTH - LOCAL_WIDTH;
        // Vertical bounds
        if (y <= 2) y = 2;
        if (y >= BOARD_HEIGHT - LOCAL_HEIGHT) y = BOARD_HEIGHT - LOCAL_HEIGHT;

        switch (action) {
            case STILL:
                frameCounter++;
                if (frameCounter >= FRAME_THRESHOLD) {
                    frameCounter = 0;
                    clipNo = (clipNo == 0) ? 1 : 0;
                }
                break;
            case UP:
                clipNo = 2;
                break;
            case DOWN:
                clipNo = 3;
                break;
            case LEFT:
                // You can add a left animation if you want
                break;
            case RIGHT:
                // You can add a right animation if you want
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) {
            action = UP;
            dy = -getSpeed();
        } else if (key == KeyEvent.VK_DOWN) {
            action = DOWN;
            dy = getSpeed();
        } else if (key == KeyEvent.VK_LEFT) {
            action = LEFT;
            dx = -getSpeed();
        } else if (key == KeyEvent.VK_RIGHT) {
            action = RIGHT;
            dx = getSpeed();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
            dy = 0;
            if (dx == 0) action = STILL;
        }
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            dx = 0;
            if (dy == 0) action = STILL;
        }
    }
}
