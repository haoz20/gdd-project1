package gdd.powerup;

import gdd.sprite.Player;

import javax.swing.*;

import static gdd.Global.*;

public class MultiShot extends PowerUp {


    public MultiShot(int x, int y) {
        super(x, y);
        int width = 50;
        int height = 50;
        ImageIcon ii = new ImageIcon(IMG_POWERUP_MULTISHOT);
        var scaledImage = ii.getImage().getScaledInstance(width,
                height,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    @Override
    public void upgrade(Player player) {
        if (multiShotCount < MAX_ACTIVATIONS) {
            player.setMultiShotLevel(player.getMultiShotLevel() + 1);
            multiShotCount++;
            this.die();
        }
    }


    @Override
    public void act() {
        this.y += 2;
    }
}