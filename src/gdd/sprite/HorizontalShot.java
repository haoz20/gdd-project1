package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;

public class HorizontalShot extends Sprite {

    private static final int H_SPACE = 20;
    private static final int V_SPACE = 1;
    private static final int FRAME_THRESHOLD = 5; // Adjust frame threshold as needed

    private int clipNo = 0;

    private int frame = 0;
    private final Rectangle[] clips = new Rectangle[] {
            new Rectangle(266*SCALE_FACTOR, 125*SCALE_FACTOR, 12*SCALE_FACTOR, 6*SCALE_FACTOR) // 0: shot frame 1
    };
    public HorizontalShot() {
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

        // Move the shot upward
        y -= 2; // Adjust speed as needed

        // Cycle through animation frames
//        frame++;
//        if (frame >= FRAME_THRESHOLD) {
//            frame = 0; // Reset the frame counter
//            clipNo = (clipNo + 1) % clips.length; // Cycle through all clips
//        }

    }

    public HorizontalShot(int x, int y) {

        initShot(x, y);
    }

    public HorizontalShot(int x, int y, int clipNo) {
        this.setClipNo(clipNo);
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

    @Override
    public Image getImage() {
        if (image == null) {
            System.err.println("Image is null in Shot.getImage()");
            return null;
        }

        if (clipNo < 0 || clipNo >= clips.length) {
            System.err.println("Invalid clipNo: " + clipNo);
            return image; // Return the full image as fallback
        }

        Rectangle bound = clips[clipNo];
        BufferedImage bImage = toBufferedImage(image);
        return bImage.getSubimage(bound.x, bound.y, bound.width, bound.height);
    }

    public int getClipNo() {
        return clipNo;
    }

    public void setClipNo(int clipNo) {
        this.clipNo = clipNo;
    }
}
