package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.util.Random;

public class BossAlien extends Enemy {

    private Bomb bomb;
    private int health = 50; // Boss has more health than regular enemies
    private int moveDirection = 1;
    private int moveDirectionY = 1;
    private int moveCounter = 0;
    private Random random = new Random();

    public BossAlien(int x, int y) {
        super(x, y);
        initBoss(x, y);
    }

    private void initBoss(int x, int y) {
        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        // Use the dedicated boss sprite
        var ii = new ImageIcon(IMG_BOSS);
        setImage(ii.getImage());
    }

    @Override
    public void act(int direction) {
        // Boss has a more complex movement pattern
        if (moveCounter % 30 == 0) {
            moveDirection = random.nextInt(3) - 1; // -1, 0, or 1 (left, stay, right)
            moveDirectionY = random.nextInt(3) - 1; // -1, 0, or 1 (up, stay, down)
        }

        x += moveDirection * 3; // Move horizontally
        y += moveDirectionY * 3; // Move vertically

        // Keep boss within screen bounds horizontally
        if (x < 0) {
            x = 0;
            moveDirection = 1;
        } else if (x > BOARD_WIDTH - getImage().getWidth(null)) {
            x = BOARD_WIDTH - getImage().getWidth(null);
            moveDirection = -1;
        }

        // Keep boss within screen bounds vertically (limit to upper half of screen)
        int minY = 0;
        int maxY = BOARD_HEIGHT / 2 - getImage().getHeight(null);
        if (y < minY) {
            y = minY;
            moveDirectionY = 1;
        } else if (y > maxY) {
            y = maxY;
            moveDirectionY = -1;
        }

        moveCounter++;
    }

    public Bomb getBomb() {
        return bomb;
    }

    public int getHealth() {
        return health;
    }

    public void decreaseHealth() {
        health--;
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    public class Bomb extends Sprite {

        private boolean destroyed;

        public Bomb(int x, int y) {
            initBomb(x, y);
        }

        private void initBomb(int x, int y) {
            setDestroyed(true);
            this.x = x;
            this.y = y;

            var bombImg = "src/images/bomb.png";
            var ii = new ImageIcon(bombImg);
            setImage(ii.getImage());
        }

        public void setDestroyed(boolean destroyed) {
            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {
            return destroyed;
        }

        @Override
        public void act() {
            this.y += 3; // Boss bombs fall faster
        }
    }
}