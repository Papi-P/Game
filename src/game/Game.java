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

//<editor-fold defaultstate="collapsed" desc="Main">
public class Game extends Thread {
    
    static JFrame ac;
    static FrameContainer fc;
    
    public Game() {
        
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
        //define every gremoad currently alive
        for(int i = 0; i < G1.size(); i++){
            G1.set(i, new gremoad());
        }
        //refresh every 1/framerate seconds
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fc.repaint();
            }
        }, 0, 1000 / 60);
        //add a new flying enemy every 2.5 seconds
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
//</editor-fold>
//<editor-fold desc="Hero Animation Logic">
class HeroImage{
    BufferedImage SpriteSheet;
    HeroImage(){
        try {
            this.SpriteSheet = ImageIO.read(new File("src\\game\\adventurer-v1.5-Sheet.png"));
        } catch (IOException ex) {
            Logger.getLogger(HeroImage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Image frame(int f, String ac){
        if(ac.equalsIgnoreCase("Idle")){
            while(f >= 4)
                f-= 4;
            return SpriteSheet.getSubimage((f)*50, 0, 50, 37);
        }
        if(ac.equalsIgnoreCase("Move")){
            while(f > 6)
                f-=6;
            if(f < 6)
                return SpriteSheet.getSubimage((f)*50, 37, 50, 37);
            
        }
        if(ac.equalsIgnoreCase("Jump")){
            if(f < 7)
                return SpriteSheet.getSubimage((f)*50, 37*2, 50, 37);
            else if(f == 7)
                return SpriteSheet.getSubimage((f)*50, 37*3, 50, 37);
            else if(f > 7){
                while(f > 7)
                    f-=7;
            }
        }
        if(ac.equalsIgnoreCase("Falling")){
            if(f > 2)
                while(f > 2)
                    f-=2;
            if(f <= 2)
                return SpriteSheet.getSubimage((f)*50, 37*2, 50, 37);
            
        }
        if(ac.equalsIgnoreCase("Attack")){
            if(f > 3)
                while(f > 3)
                    f-=3;
            if(f <= 3)
                return SpriteSheet.getSubimage((f-1)*50, 37*6, 50, 37);
        }
        if(ac.equalsIgnoreCase("Hurt")){
            if(f > 3 && defender.getY() >= defender.maxy)
                defender.curAction = "Idle";
            else if (f > 3 && defender.getY() < defender.maxy)
                defender.curAction = "Falling";
            if(f <= 3)
                return SpriteSheet.getSubimage((f+2)*50, 37*8, 50, 37);
        }
        return frame(1, "Idle");
    }
}
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="FrameContainer">
class FrameContainer extends JPanel {
    private BufferedImage buffer;
    BufferedImage gimage;
    BufferedImage gaImage;
    BufferedImage backgroundImage;
    HeroImage himg = new HeroImage();
    public static Hero defender;
    public static int HeroX;
    int tempDirChange = 0;
    
    
    FrameContainer() {
        try {
            this.backgroundImage = ImageIO.read(new File("src\\game\\Background.png"));
        } catch (IOException ex) {
            Logger.getLogger(FrameContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        this.defender = new Hero();
        
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
        
        am.put("left-pressed", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(defender.isJumping == false){
                    defender.movementValue = -(int) defender.speed;
                    defender.curAction = "Move";
                    defender.curDir = -1;
                }else{
                    defender.flaggedDir = -(int) defender.speed;
                    new Timer().scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if(defender.getY() >= defender.maxy){
                                defender.movementValue = defender.flaggedDir;
                                defender.curDir = -1;
                                defender.curAction = "Move";
                                this.cancel();
                            }
                        }
                    }, 0, 1000 / 60);
                }
            }
        });      
        am.put("left-released", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(defender.isJumping == false){
                    defender.movementValue = 0;
                    defender.curAction = "Idle";
                }else{
                    defender.flaggedDir = 0;
                    new Timer().scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if(defender.getY() >= defender.maxy){
                                defender.movementValue = 0;
                                defender.curAction = "Idle";
                                this.cancel();
                            }
                        }
                    }, 0, 1000 / 60);
                }
            }
        });
        am.put("right-pressed", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(defender.isJumping == false){
                    defender.movementValue = (int) defender.speed;
                    defender.curAction = "Move";
                    defender.curDir = 1;
                }else{
                    defender.flaggedDir = (int) defender.speed;
                    new Timer().scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if(defender.getY() >= defender.maxy){
                                defender.movementValue = defender.flaggedDir;
                                defender.curDir = 1;
                                defender.curAction = "Move";
                                this.cancel();
                            }
                        }
                    }, 0, 1000 / 60);
                }
            }
        });
        am.put("right-released", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(defender.isJumping == false){
                    defender.movementValue = 0;
                    defender.curAction = "Idle";
                }else{
                    defender.flaggedDir = 0;
                    new Timer().scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if(defender.getY() >= defender.maxy){
                                defender.movementValue = 0;
                                defender.curAction = "Idle";
                                this.cancel();
                            }
                        }
                    }, 0, 1000 / 60);
                }
            }
        });
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "up-pressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "up-released");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "up-pressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "up-released");
        
        am.put("up-pressed", DJump);
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), "attack-pressed");
        am.put("attack-pressed", DAttack);
    }
    Action DJump = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            defender.jump();
        }
    };
    Action DAttack = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            defender.attack();
        }
    };
    
    public void init() throws IOException {
        
    }
    int curFrameNumber = 0, curFrame = 1, tempFrameNumber = 0, permFrameNumber = 0;
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //<editor-fold desc="Dealing with specific animation frame increments">
        curFrameNumber++;
        if(curFrameNumber%(60/10) == 0){
            curFrame++;
            tempFrameNumber++;
            if(curFrame > 5)
                curFrame = 1;
        }
        //</editor-fold>
        //create an end time for attacking
        if(defender.attacking){
            if(permFrameNumber+5 == tempFrameNumber){
                defender.attacking = false;
                defender.curAction = "Idle";
            }
        }
        
        Graphics2D g2d = (Graphics2D) buffer.getGraphics();
        //<editor-fold desc="Antialiasing">
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //</editor-fold>
        
        g2d.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
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
        //</editor-fold>
        if(defender.attacking)
            //this sets the attack option at the highest playermodel priority. No matter what else is happening, if the user is attacking it will attack.
            defender.curAction = "Attack";
        if(defender.curDir == 1)
            g2d.drawImage(himg.frame(curFrame, defender.curAction), defender.getX(), defender.getY(), defender.size, defender.size, null);
        if(defender.curDir == -1)
            g2d.drawImage(himg.frame(curFrame, defender.curAction), defender.getX()+defender.size, defender.getY(), -defender.size, defender.size, null);
        g.drawImage(buffer, 0, 0, null);
        HeroX = defender.getX();
    }
    
    public void playGame() {
        
    }
}

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Gremoad Class">

class gremoad {
    public double health = 100;
    public int initDir = (int)(Math.floor(Math.random()*2));
    public double speed = Math.random()*6+1;
    public double xspeed = speed;
    public double damage = 20;
    private int screenWidth = 1200;
    public int x = (int) (Math.floor(Math.random() * screenWidth));
    private int maxy = (int) (Math.floor(Math.random() * 100));
    public int y = -150;
    public int dir = 1;
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
        //slowly spawn the gremoad from the top of the screen down
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (y < maxy) {
                    y += speed;
                }else{
                    this.cancel();
                }
            }
        }, 0, 1000 / 60);
        //attack
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(G1.contains(t)){
                    ga temp = new ga(t.x+25, t.y+t.size-50, t.dir*((int)t.xspeed));
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
}
//</editor-fold>
//<editor-fold desc="Gremoad attack">

class ga{
    Image orb;
    public int orbx;
    public int orby;
    public int orbsize = 50;
    private int orbspeed = 2;
    private double speedMulti = 0;
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
                    if(t.orbspeed > 0){
                        if(speedMulti <= orbspeed){
                            speedMulti-=0.1;
                        }
                        else{
                            speedMulti = orbspeed;
                        }
                        orbx += orbspeed-speedMulti;
                    }else if(t.orbspeed < 0){
                        if(speedMulti <= orbspeed){
                            speedMulti+=0.1;
                        }
                        else{
                            speedMulti = orbspeed;
                        }
                        orbx += orbspeed+speedMulti;
                    }
                if(orby > 800+orbsize){
                    GAttacks.remove(t);
                }
                
                if(orby > defender.getY()-t.orbsize/2 && orby < defender.getY()+defender.size/2 && orbx > defender.getX()-t.orbsize/2 && orbx < defender.getX()+defender.size/2){
                    if(GAttacks.contains(t) && defender.invincibility == false){
                        lives--;
                        defender.invincibility = true;
                        defender.invincibleTimer = 0;
                        //give the player 2 seconds of invincibility after being hit
                        new Timer().scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                defender.invincibleTimer++;
                                if(defender.invincibleTimer >= 2){
                                    defender.invincibility = false;
                                    this.cancel();
                                }
                            }
                        }, 0, 1000);
                        fc.curFrame = 1;
                        defender.curAction = "Hurt";
                    }
                    GAttacks.remove(t);
                }
            }
        }, 0, 1000/60);
    }
}
//</editor-fold>
//<editor-fold desc="Hero class">

class Hero {
    public String curAction = "Idle";
    double gravity = 20;
    double tvelocity = 300;
    int verticalSpeed = 0;
    public double health = 100;
    public double speed = 10;
    public double damage = 20;
    public int x = 500;
    public int y = 555;
    public int maxy = y;
    int size = 150;
    private final Hero t = this;
    private int screenWidth = 1200;
    public BufferedImage gImage;
    private int jumpForce = 0;
    public int curDir = 1;
    public int movementValue;
    public int flaggedDir = 0;
    Boolean invincibility = false;
    public int invincibleTimer = 0;
    public int getValue() {
        return movementValue;
    }
    public Hero() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                x += movementValue;
                if (x < 0) {
                    x = 0;
                } else if (x + size > screenWidth) {
                    x = screenWidth - size;
                }
            }
        }, 0, 1000 / 60);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                gremoad t = null;
                for(int i = 0; i < G1.size(); i++){
                    t = G1.get(i);
                    if (t.x >= screenWidth - 128) {
                        if(t.initDir == 1)
                            t.dir = -1;
                        else
                            t.dir = 1;
                    }
                    if (t.x <= 0) {
                        if(t.initDir == 1)
                            t.dir = 1;
                        else
                            t.dir = -1;
                    }
                    if (t.dir == -1) {
                        t.x -= t.xspeed;
                    }
                    if (t.dir == 1) {
                        t.x += t.xspeed;
                    }
                    if((t.y > defender.getY()-t.size && t.y < defender.getY()+t.size+20)){
                        if(defender.curDir == -1){
                            //on the left
                            if((t.x > defender.getX()-10 && t.x < defender.getX()+defender.size/2+15)){
                                if(defender.attacking && fc.curFrame >= 2 && fc.curFrame <= 3){
                                    if(G1.contains(t)){
                                        score++;
                                        G1.remove(t);
                                    }
                                }
                            }
                        }
                        if(defender.curDir == 1){
                            //on the right
                            if((t.x > defender.getX()+defender.size/2-15 && t.x < defender.getX()+defender.size+10)){
                                if(defender.attacking && fc.curFrame >= 2 && fc.curFrame <= 3){
                                    if(G1.contains(t)){
                                        score++;
                                        G1.remove(t);
                                    }
                                }
                            }
                        }
                    }
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
            this.curAction = "Jump";
            fc.curFrame = 1;
            isJumping = true;
            goingUp = true;
            gravity = 2;
            tvelocity = 300;
            verticalSpeed = 0;
            jumpForce = 48;
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (goingUp == true) {
                        if (jumpForce > 0) {
                            jumpForce = (int) (jumpForce - gravity);
                            y -= jumpForce;
                        } else {
                            goingUp = false;
                            if(!t.curAction.equalsIgnoreCase("Falling")){
                                t.curAction = "Falling";
                            }
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
                        if(defender.movementValue == 0)
                            t.curAction = "Idle";
                        else if(defender.movementValue != 0)
                            t.curAction = "Move";
                        this.cancel();
                    }
                }
            }, 0, 1000 / 60);
        }
    }
    Boolean attacking = false;
    public void attack(){
        if(!this.attacking){
            this.attacking = true;
            this.curAction = "Attack";
            fc.curFrame = 1;
            fc.permFrameNumber = fc.tempFrameNumber;
            fc.permFrameNumber = fc.tempFrameNumber;
            fc.curFrame = 1;
        }
    }
    
}
//</editor-fold>