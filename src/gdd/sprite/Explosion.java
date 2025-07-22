package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Explosion extends Sprite {


    private int clipNo = 0;
    private final Rectangle[] clips = new Rectangle[] {
            new Rectangle(1089,169,49,36)
    };

    public Explosion(int x, int y) {

        initExplosion(x, y);
    }

    private void initExplosion(int x, int y) {

        this.x = x;
        this.y = y;

        var ii = new ImageIcon(IMG_EXPLOSION);

        // Scale the image to use the global scaling factor
//        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
//                ii.getIconHeight() * SCALE_FACTOR,
//                java.awt.Image.SCALE_SMOOTH);
        setImage(ii.getImage());
    }

    @Override
    public Image getImage() {
        Rectangle bound = clips[clipNo];
        BufferedImage bImage = toBufferedImage(image);

        // Check if the bounds are within the image
        return bImage.getSubimage(bound.x, bound.y, bound.width, bound.height);
    }

    public void act() {
        // The explosion does not move, so no action needed here
    }

    public void act(int direction) {

        // this.x += direction;
    }


}
