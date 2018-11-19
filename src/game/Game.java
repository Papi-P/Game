package game;

import static game.FrameContainer.HeroX;
import static game.FrameContainer.defender;
import static game.Game.G1;
import static game.Game.GAttacks;
import static game.Game.fc;
import static game.Game.lives;
import static game.Game.score;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;

public class Game extends Thread {

    static JFrame ac;
    static FrameContainer fc;

    public Game() {

    }
    
    @Override
    public void run() {

    }
    static ArrayList<gremoad> G1 = new ArrayList<gremoad>();
    static ArrayList<ga> GAttacks = new ArrayList<ga>();
    public static int lives = 10;
    public static int score = 0;
    public static void main(String argsp[]) {
        fc = new FrameContainer();
        ac = buildFrame();
        ac.add(fc);
        fc.setFocusable(true);
        ac.setVisible(true);
        for(int i = 0; i < G1.size(); i++){
            G1.set(i, new gremoad());
        }
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fc.repaint();
            }
        }, 0, 1000 / 60);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                G1.add(new gremoad());
            }
        }, 0, 2500);
    }

    public static JFrame buildFrame() {
        JFrame ac = new JFrame();
        ac.setSize(1200, 800);
        ac.setResizable(false);
        ac.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ac.setLocationRelativeTo(null);
        return ac;
    }
}

class MovementState {
    public int xDirection;
}

class FrameContainer extends JPanel {

    private BufferedImage buffer;
    BufferedImage gimage;
    Image himage;
    BufferedImage gaImage;
    public static Hero defender;
    public static int HeroX;
    public class XDirectionAction extends AbstractDirectionAction {

        public XDirectionAction(MovementState movementState, int value) {
            super(movementState, value);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!defender.isJumping) {
                getMovementState().xDirection = getValue();
            if(getMovementState().xDirection > 0)
                Hero.curDir = -1;
            if(getMovementState().xDirection < 0)
                Hero.curDir = 1;
            }
        }
    }

    public abstract class AbstractDirectionAction extends AbstractAction {

        private final MovementState movementState;
        private final int value;

        public AbstractDirectionAction(MovementState movementState, int value) {
            this.movementState = movementState;
            this.value = value;
        }

        public MovementState getMovementState() {
            return movementState;
        }

        public int getValue() {
            return value;
        }

    }

    FrameContainer() {
        try {
            this.gaImage = ImageIO.read(new File("src\\game\\Orb.png"));
        } catch (IOException ex) {
            Logger.getLogger(FrameContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            this.gimage = ImageIO.read(new File("src\\game\\Artboard 1@4x.png"));
        } catch (IOException ex) {
            Logger.getLogger(FrameContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.buffer = new BufferedImage(1200, 800, BufferedImage.TYPE_INT_RGB);
        try {
            this.defender = new Hero();
        } catch (IOException ex) {
            Logger.getLogger(FrameContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        himage = defender.characterModel();

        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left-pressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "left-released");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right-pressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "right-released");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "left-pressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "left-released");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "right-pressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "right-released");

        am.put("left-pressed", new XDirectionAction(defender.movementState, -(int) defender.speed));
        am.put("left-released", new XDirectionAction(defender.movementState, 0));
        am.put("right-pressed", new XDirectionAction(defender.movementState, (int) defender.speed));
        am.put("right-released", new XDirectionAction(defender.movementState, 0));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "up-pressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "up-released");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "up-pressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "up-released");

        am.put("up-pressed", JAction);
    }
    Action JAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            defender.jump();
        }
    };

    public void init() throws IOException {

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) buffer.getGraphics();
        //<editor-fold desc="Antialiasing">
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //</editor-fold>
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 1200, 800);
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 600, 1200, 200);
        g2d.setColor(Color.RED);

        
        //<editor-fold desc="Draw gremoads">
        for(int i = 0; i < GAttacks.size(); i++){
            try{
                g2d.drawImage(gaImage, GAttacks.get(i).orbx+GAttacks.get(i).orbsize/2, GAttacks.get(i).orby, GAttacks.get(i).orbsize, GAttacks.get(i).orbsize, null);
            }catch(Exception e){
                
            }
        }
        for(int i = 0; i < G1.size(); i++){
            try{
                if(G1.get(i) != null)
                    g2d.drawImage(G1.get(i).gImage, G1.get(i).getX(), G1.get(i).getY(), G1.get(i).size, G1.get(i).size, null);
            }catch(Exception e){
                
            }
        }
        //</editor-fold>
        //<editor-fold desc="Print game info">
        g2d.setFont(new Font("Electrofied", Font.PLAIN, 24));
        g2d.drawString("Score: "+score, 50, 24);
        g2d.drawString("Lives: "+lives, 50, 48);
        
        if(defender.curDir == 1)
            g2d.drawImage(himage, defender.getX(), defender.getY(), defender.size, defender.size, null);
        if(defender.curDir == -1)
            g2d.drawImage(himage, defender.getX()+defender.size, defender.getY(), -defender.size, defender.size, null);
        g.drawImage(buffer, 0, 0, null);
        
        HeroX = defender.getX();
    }

    public void playGame() {

    }

}
//<editor-fold defaultstate="collapsed" desc="Gremoad Class">

class gremoad {
    public double health = 100;
    private int initDir = (int)(Math.floor(Math.random()*2));
    public double speed = Math.random()*6+1;
    private double xspeed = speed;
    public double damage = 20;
    private int screenWidth = 1200;
    private int x = (int) (Math.floor(Math.random() * screenWidth));
    private int maxy = (int) (Math.floor(Math.random() * 100));
    private int y = -150;
    private int dir = 1;
    public int size = 150;
    private gremoad t = this;
    public Image gImage;
    public gremoad(){
        if(initDir == 0){
            this.xspeed*=-1;
        }
        try {
            this.gImage = ImageIO.read(new File("src\\game\\Artboard 1@4x.png"));
        } catch (IOException ex) {
            Logger.getLogger(gremoad.class.getName()).log(Level.SEVERE, null, ex);
        }
        initEntity();
    }

    private void initEntity() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (y < maxy) {
                    y += speed;
                }
                if (x >= screenWidth - 128) {
                    if(initDir == 1)
                        dir = -1;
                    else
                        dir = 1;
                }
                if (x <= 0) {
                    if(initDir == 1)
                        dir = 1;
                    else
                        dir = -1;
                }
                if (dir == -1) {
                    x -= xspeed;
                }
                if (dir == 1) {
                    x += xspeed;
                }
                if((y > defender.getY()-t.size && y < defender.getY()+defender.size)){
                    if((x > defender.getX()-t.size && x < defender.getX()+defender.size)){
                        if(G1.contains(t)){
                            score++;
                            G1.remove(t);
                            this.cancel();
                        }
                    }
                }
            }
        }, 0, 1000 / 60);
        //attack
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(G1.contains(t)){
                    ga temp = new ga(t.getX()+25, t.getY()+t.size-50, dir*((int)t.xspeed));
                    GAttacks.add(temp);
                }else{
                    this.cancel();
                }
            }
        }, 0, 2000);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
    private void deleteEntity(){
        t = null;
        G1.remove(this);
    }
}
//</editor-fold>
//<editor-fold desc="Gremoad attack">

class ga{
    Image orb;
    public int orbx;
    public int orby;
    public int orbsize = 50;
    private int orbspeed = 2;
    private ga t = this;
    gremoad p;
    double gravity = .98/2;
    double verticalSpeed = 1;
    ga(int ox, int oy, int sp){
        this.orbx = ox;
        this.orby = oy;
        this.orbspeed = sp;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                    verticalSpeed += gravity;
                    orby += verticalSpeed;
                    orbx += orbspeed;
                if(orby > 800+orbsize){
                    GAttacks.remove(t);
                }
                if(orby > defender.getY()-t.orbsize && orby < defender.getY()+defender.size && orbx > defender.getX()-t.orbsize && orbx < defender.getX()+defender.size){
                    if(GAttacks.contains(t))
                        lives--;
                    GAttacks.remove(t);
                }
            }
        }, 0, 1000/60);
    }
}
//</editor-fold>
//<editor-fold desc="Hero class">

class Hero {

    public MovementState movementState = new MovementState();
    double gravity = 20;
    double tvelocity = 300;
    int verticalSpeed = 0;
    public double health = 100;
    public double speed = 10;
    public double damage = 20;
    private int x = 500;
    private int y = 600;
    private int maxy = y;
    int size = 150;
    private final Hero t = this;
    private int screenWidth = 1200;
    public BufferedImage gImage;
    private int jumpForce = 0;
    public static int curDir = 1;
    public Hero() throws IOException {
        this.gImage = ImageIO.read(new File("src\\game\\Nate.png"));
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                x += movementState.xDirection;
                if (x < 0) {
                    x = 0;
                } else if (x + size > screenWidth) {
                    x = screenWidth - size;
                }
            }
        }, 0, 1000 / 60);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
    Boolean goingUp = true;
    Boolean isJumping = false;

    public void jump() {
        if (!isJumping) {
            isJumping = true;
            goingUp = true;
            gravity = 2;
            tvelocity = 300;
            verticalSpeed = 0;
            jumpForce = 50;
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (goingUp == true) {
                        if (jumpForce > 0) {
                            jumpForce = (int) (jumpForce - gravity);
                            y -= jumpForce;
                        } else {
                            goingUp = false;
                            verticalSpeed = 0;
                        }
                    }
                    if (goingUp == false) {
                        if (y < maxy) {
                            verticalSpeed = (int) (verticalSpeed + gravity);
                            y += verticalSpeed;
                        }
                    }
                    if (y >= maxy) {
                        isJumping = false;
                        y = maxy;
                        this.cancel();
                    }
                }
            }, 0, 1000 / 60);
        }
    }

    private void attack() {
        
    }

    public Image characterModel() {
        return this.gImage;
    }
}
//</editor-fold>
