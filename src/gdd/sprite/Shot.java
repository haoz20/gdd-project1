package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Shot extends Sprite {

    private static final int H_SPACE = 20;
    private static final int V_SPACE = 1;

    public Shot() {
    }

    // add velocity for spread shots
    private int velocityX = 0;
    private int velocityY = 0;

    public void setSpreadVelocity(int vx, int vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }

    @Override
    public void act() {
        if (velocityX != 0 || velocityY != 0) {
            // Use custom velocity for spread shots
            this.x += velocityX;
            this.y += velocityY;
        } else {
            this.x += 4;
        }
    }

    public Shot(int x, int y) {

        initShot(x, y);
    }

    private void initShot(int x, int y) {

        var ii = new ImageIcon(IMG_SHOT);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);

        setX(x + H_SPACE);
        setY(y - V_SPACE);
    }
}
