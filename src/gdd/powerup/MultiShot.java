package gdd.powerup;

import gdd.sprite.Player;

import javax.swing.*;

import static gdd.Global.*;

public class MultiShot extends PowerUp {

    public MultiShot(int x, int y) {
        super(x, y);
    }

    @Override
    public void upgrade(Player player) {
        ImageIcon ii = new ImageIcon(IMG_POWERUP_MULTISHOT);
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() ,
                ii.getIconHeight() ,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }


    @Override
    public void act() {

    }
}