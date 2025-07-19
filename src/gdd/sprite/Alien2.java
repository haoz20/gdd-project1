package gdd.sprite;

import java.awt.Image;
import javax.swing.ImageIcon;
import static gdd.Global.*;


public class Alien2 extends Enemy {

    private int moveDirection = 1;
    private int moveCounter = 0;

    public Alien2(int x, int y) {
        super(x, y);
        initAlien2();
    }

    private void initAlien2() {
        ImageIcon ii = new ImageIcon(IMG_ENEMY);
        Image scaledImage = ii.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        setImage(scaledImage);
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
    }
}
