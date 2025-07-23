package gdd;

import gdd.scene.BossScene;
import gdd.scene.Scene1;
import gdd.scene.TitleScene;

import javax.swing.JFrame;

public class Game extends JFrame {

    TitleScene titleScene;
    Scene1 scene1;
    BossScene bossScene;

    public Game() {
        titleScene = new TitleScene(this);
        scene1 = new Scene1(this);
        bossScene = new BossScene(this);
        initUI();
        loadTitle();
//        loadScene2();
    }

    private void initUI() {

        setTitle("Space Invaders");
        setSize(Global.BOARD_WIDTH, Global.BOARD_HEIGHT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

    }

    public void loadTitle() {
        getContentPane().removeAll();
        // add(new Title(this));
        add(titleScene);
        titleScene.start();
        revalidate();
        repaint();
    }

    public void loadScene1() {
        // ....  
    }

    public void loadScene2() {
        getContentPane().removeAll();
        add(scene1);
        titleScene.stop();
        scene1.start();
        revalidate();
        repaint();
    }
    
    public void loadBossScene() {
        getContentPane().removeAll();
        add(bossScene);
        titleScene.stop();
        bossScene.start();
        revalidate();
        repaint();
    }
}