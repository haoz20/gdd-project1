package gdd.sprite;

import static gdd.Global.*;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 540;
    private int width;
    private int currentSpeed = 2;

    private int multiShotLevel = 1;
    
    // Add vertical movement variable
    protected int dy;

    private Rectangle bounds = new Rectangle(175,135,17,32);

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        var ii = new ImageIcon(IMG_PLAYER);

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

    public int getMultiShotLevel() {
        return multiShotLevel;
    }

    public void setMultiShotLevel(int multiShotLevel) {
        this.multiShotLevel = multiShotLevel;
    }

    public void act() {
        // Update position based on dx and dy
        x += dx;
        y += dy;

        // Boundary checks
        if (x <= 2) {
            x = 2;
        }

        if (x >= BOARD_WIDTH - 2 * width) {
            x = BOARD_WIDTH - 2 * width;
        }
        
        // Add vertical boundary checks
        if (y <= 2) {
            y = 2;
        }
        
        if (y >= BOARD_HEIGHT - 50) {
            y = BOARD_HEIGHT - 50;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -currentSpeed;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = currentSpeed;
        }
        
        // Add up/down movement
        if (key == KeyEvent.VK_UP) {
            dy = -currentSpeed;
        }
        
        if (key == KeyEvent.VK_DOWN) {
            dy = currentSpeed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }
        
        // Add up/down release handling
        if (key == KeyEvent.VK_UP) {
            dy = 0;
        }
        
        if (key == KeyEvent.VK_DOWN) {
            dy = 0;
        }
    }
}
