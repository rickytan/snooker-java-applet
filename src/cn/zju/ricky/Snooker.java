package cn.zju.ricky;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class Snooker extends Applet implements MouseListener,
        MouseMotionListener, KeyListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // 动画线程
    private Thread snooker;
	// 碰撞检测线程
    // private Thread checkcollision;
    // 双缓冲，减少闪烁
    private Image offscreen;
    private final static BufferedImage bimg = new BufferedImage(240, 140,
            BufferedImage.TYPE_4BYTE_ABGR);

    // 台上的球的链表
    private ArrayList<Ball> balls;
    private boolean putWhiteBall;
    private Ball whiteball;
    // 某一杆的进球个数
    private ArrayList<Ball> ballin = new ArrayList<Ball>();
    // 台面上的红球个数
    private int redBalls;
    private int firstColli;
    // 球杆
    private Stick stick;
    // 两个玩家
    private Player[] players;
    // 球桌
    private Table table;
    // 力量条
    private Powerbar powerbar;
    // 当前轮流次序
    private int turns;
    // 游戏结束
    private boolean gameOver;
    private boolean moving;
    private int state;

    public void init() {
        this.setSize(742, 560);
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        // setBackground(Color.black);
        offscreen = createImage(getWidth(), getHeight());

        // java.net.URL baseURL = getCodeBase();
        table = new Table();
        firstColli = -1;
        balls = initBalls();
        redBalls = 15;
        stick = new Stick(balls.get(0));
        stick.hide(true);
        powerbar = new Powerbar();
        players = new Player[2];
        players[0] = new Player(0, "Steven");
        players[1] = new Player(1, "O'Saliven");
        turns = 0;
        gameOver = false;
        putWhiteBall = true;
        state = 0;
        moving = false;
        firstColli = -1;
        String[] s = {"ball/black.png", "ball/blue.png", "ball/brown.png",
            "ball/green.png", "ball/pink.png", "ball/red.png",
            "ball/white.png", "ball/yellow.png", "player/player0.jpg",
            "player/player1.jpg", "stick/stick.png", "talbe.png"};
        Image img;
        MediaTracker mt = new MediaTracker(this);
        for (int i = 0; i < s.length; i++) {
            img = getImage(getCodeBase(), "Image" + s[i]);
            mt.addImage(img, i);
        }
        try {
            mt.waitForAll();
        } catch (InterruptedException e) {
            System.out.println(e);
            return;
        }
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        snooker = new Thread(new Refresh());
        snooker.start();
    }

    public void paint(Graphics g) {
        table.draw(g);
        Graphics2D P;
        for (int i = 0; i < players.length; i++) {
            P = bimg.createGraphics();
            P.setBackground(Color.yellow);
            players[i].draw(P);
            ((Graphics2D) g).drawImage(bimg, i * 480 + 10, 10, null);
            P.dispose();
            g.setColor(Color.green);
        }
        // 绘制表明次序的线框
        g.drawRect(10 + 480 * turns, 10, 240, 140);
        // 开始认为没有球动了
        moving = false;

        for (int i = 0; i < balls.size(); i++) {
            Ball ball1 = balls.get(i);
            if (ball1.isIn()) {
                continue;
            }
            if (table.ballIn(ball1)) {
                if (1 <= i && i <= 15) {
                    redBalls--; // 红球数减一
                }
                ballin.add(ball1);
                ball1.setIn(true);
            }
            if (ball1.getSpeed().getV() != 0) {
                moving = true; // 有一个球速度不为0，就有球要动
            }
            for (int j = i + 1; j < balls.size(); j++) {
                Ball ball2 = balls.get(j);
                if (ball2.isIn()) {
                    continue;
                }
                if (ball1.isCollision(ball2)) {
                    if (firstColli == -1) {
                        firstColli = j; // 如果还没撞到球，这就是第一个撞到的球
                    }
                    ball1.collision2(ball2);
                }
            }
            if (table.isCollision(ball1)) {
                table.collision(ball1);
            }
            ball1.draw(g);
        }
        if (putWhiteBall) {
            stick.hide(true);
            stick.putMainBall(whiteball);
        } else {
            if (!moving && stick.getHideStatus()) {
                check2();
            }
        }
        powerbar.draw(g);
        String cb;
        if (state == 0 || state == 2) {
            cb = "红";
        } else {
            cb = "彩";
        }
        g.setFont(new Font("楷体", Font.CENTER_BASELINE, 24));
        g.setColor(Color.yellow);
        g.drawString("你应击" + cb + "球", 310, 120);
        stick.draw(g);
    }

    public void update(Graphics g) {
        if (offscreen == null || offscreen.getHeight(null) != this.getHeight()
                || offscreen.getWidth(null) != this.getWidth()) {
            offscreen = createImage(getWidth(), getHeight());
        }
        g.drawImage(offscreen, 0, 0, null);
        Graphics bufferg = offscreen.getGraphics();
        bufferg.setColor(Color.black);
        bufferg.fillRect(0, 0, getWidth(), getHeight());
        paint(bufferg);
        bufferg.dispose();
    }

    public ArrayList<Ball> initBalls() {
        ArrayList<Ball> balls = new ArrayList<Ball>();
        Ball ball;
        double t_pos = 550.0;
        whiteball = ball = new Ball(120.0, 353.0, "white");
        balls.add(ball);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j <= i; j++) {
                ball = new Ball(t_pos + 1.732 * (Ball.radius) * i, 353 - i
                        * (Ball.radius) + j * (Ball.radius) * 2, "red");
                balls.add(ball);
            }
        }
        ball = new Ball(528.0, 353.0, "pink");
        balls.add(ball);
        ball = new Ball(173.0, 296.0, "green");
        balls.add(ball);
        ball = new Ball(173.0, 353.0, "brown");
        balls.add(ball);
        ball = new Ball(173.0, 410.0, "yellow");
        balls.add(ball);
        ball = new Ball(371.0, 353.0, "blue");
        balls.add(ball);
        ball = new Ball(660.0, 353.0, "black");
        balls.add(ball);
        return balls;
    }

    public void check() {
        // 没碰到球，换人，对手加分
        if (firstColli == -1) {
            players[turns].setState(0); // 当前玩家下次要击红球
            turns ^= 1; // 换人
            players[turns].add(2); // 对手加分
        } // 碰到球，且击对颜色
        else if (players[turns].getState() == ((firstColli <= 15) ? 0 : 1)) {
            for (int i = 0; i < ballin.size(); i++) {
                if (ballin.get(i).getType() == 1) {
                    ballin.get(i).reset(); // 将彩球恢复
                } else if (players[turns].getState() == 1) {

                }
                players[turns].add(balls.get(i).score());
            }
        }
        boolean colorball = false;
        for (int i = 16; i < balls.size(); i++) {
            if (!balls.get(i).isIn()) {
                colorball = true;
                break;
            }
        }
        // 如果彩球没有击完
        if (colorball) {

        } else {

        }
        stick.hide(false);
    }

    public void check2() {
        switch (state) {
            case 0: // 初始状态，刚换人或游戏刚开始
                if (whiteball.isIn()) {
                    turns ^= 1; // 换人
                    players[turns].add(2); // 对手加分
                    for (int i = 0; i < ballin.size(); i++) {
                        Ball b = ballin.get(i);
                        if (b.score() >= 2) {
                            b.setToLastPos(); // 彩球加到上次位置
                        }
                    }
                    putWhiteBall = true;
                    if (redBalls <= 0) {
                        state = 3;
                    }
                } else {
                    if (1 <= firstColli && firstColli <= 15) { // 击对球
                        if (!ballin.isEmpty()) {
                            for (int i = 0; i < ballin.size(); i++) {
                                Ball b = ballin.get(i);
                                players[turns].add(b.score()); // 进球得分
                                if (b.score() > 2) {
                                    b.reset();
                                }
                            }
                            state = 1;
                        } else {
                            turns ^= 1;
                        }
                    } else { // 击错球
                        turns ^= 1;
                        players[turns].add(2); // 对手加分
                        for (int i = 0; i < ballin.size(); i++) {
                            Ball b = ballin.get(i);
                            if (b.score() >= 2) {
                                b.setToLastPos(); // 彩球加到上次位置
                                b.setIn(false);
                            }
                        }
                        if (redBalls <= 0) {
                            state = 3;
                        }
                    }
                }
                break;
            case 1: // 已进了一个红球
                if (whiteball.isIn()) {
                    turns ^= 1; // 换人
                    players[turns].add(2); // 对手加分
                    for (int i = 0; i < ballin.size(); i++) {
                        Ball b = ballin.get(i);
                        if (b.score() >= 2) {
                            b.setToLastPos(); // 彩球加到上次位置
                        }
                    }
                    putWhiteBall = true;
                    if (redBalls <= 0) {
                        state = 3;
                    } else {
                        state = 0;
                    }
                } else {
                    if (16 <= firstColli) { // 击对球
                        if (!ballin.isEmpty()) {
                            for (int i = 0; i < ballin.size(); i++) {
                                Ball b = ballin.get(i);
                                players[turns].add(b.score()); // 进球得分
                                if (b.score() > 2) {
                                    b.reset();
                                }
                            }
                            if (redBalls <= 0) {
                                state = 3;
                            } else {
                                state = 2;
                            }
                        } else {
                            turns ^= 1;
                            state = 0;
                        }
                    } else { // 击错球
                        turns ^= 1;
                        players[turns].add(2); // 对手加分
                        for (int i = 0; i < ballin.size(); i++) {
                            Ball b = ballin.get(i);
                            if (b.score() >= 2) {
                                b.setToLastPos(); // 彩球加到上次位置
                                b.setIn(false);
                            }
                        }
                        if (redBalls <= 0) {
                            state = 3;
                        } else {
                            state = 0;
                        }
                    }
                }
                break;
            case 2: // 已进了一个彩球
                if (whiteball.isIn()) {
                    turns ^= 1; // 换人
                    players[turns].add(2); // 对手加分
                    for (int i = 0; i < ballin.size(); i++) {
                        Ball b = ballin.get(i);
                        if (b.score() >= 2) {
                            b.setToLastPos(); // 彩球加到上次位置
                        }
                    }
                    putWhiteBall = true;
                    if (redBalls <= 0) {
                        state = 3;
                    } else {
                        state = 0;
                    }
                } else {
                    if (1 <= firstColli && firstColli <= 15) { // 击对球
                        if (!ballin.isEmpty()) {
                            for (int i = 0; i < ballin.size(); i++) {
                                Ball b = ballin.get(i);
                                players[turns].add(b.score()); // 进球得分
                                if (b.score() > 2) {
                                    b.reset();
                                }
                            }
                            state = 1;
                        } else {
                            turns ^= 1;
                            state = 0;
                        }
                    } else { // 击错球
                        turns ^= 1;
                        players[turns].add(2); // 对手加分
                        for (int i = 0; i < ballin.size(); i++) {
                            Ball b = ballin.get(i);
                            if (b.score() >= 2) {
                                b.setToLastPos(); // 彩球加到上次位置
                                b.setIn(false);
                            }
                        }
                        if (redBalls <= 0) {
                            state = 3;
                        } else {
                            state = 0;
                        }
                    }
                }
                break;
            case 3: // 红球没了
                if (whiteball.isIn()) {
                    turns ^= 1; // 换人
                    players[turns].add(2); // 对手加分
                    for (int i = 0; i < ballin.size(); i++) {
                        Ball b = ballin.get(i);
                        if (b.score() >= 2) {
                            b.setToLastPos(); // 彩球加到上次位置
                        }
                    }
                    putWhiteBall = true;
                } else {
                    int ballTo;
                    for (ballTo = 16; ballTo <= 21; ballTo++) {
                        if (!balls.get(ballTo).isIn()) {
                            break;
                        }
                    }
                    if (ballTo != 22) {
                        if (firstColli == ballTo) {
                            if (!ballin.isEmpty()) {
                                for (int i = 0; i < ballin.size(); i++) {
                                    Ball b = ballin.get(i);
                                    players[turns].add(b.score()); // 进球得分
                                    if (b.score() > balls.get(ballTo).score()) {
                                        b.reset();
                                    }
                                }
                                if (ballTo == 21) {
                                    gameOver = true;
                                }
                            } else {
                                turns ^= 1;
                            }
                        } else {
                            turns ^= 1;
                            players[turns].add(2); // 对手加分
                            for (int i = 0; i < ballin.size(); i++) {
                                Ball b = ballin.get(i);
                                if (b.score() >= 2) {
                                    b.setToLastPos(); // 彩球加到上次位置
                                    b.setIn(false);
                                }
                            }
                        }
                    } else {
                        gameOver = true;
                    }
                }
                break;
        }
        ballin.removeAll(ballin);
        if (putWhiteBall) {
            whiteball.reset();
        } else {
            stick.hide(false);
        }
    }

    private void winner() {
        // TODO Auto-generated method stub
        try {
            Thread.sleep(40);
        } catch (InterruptedException e) {
            // nothing
        }
        Graphics bufferg = offscreen.getGraphics();
        bufferg.setColor(Color.black);
        bufferg.fillRect(0, 0, getWidth(), getHeight());
        bufferg.setFont(new Font("Kunstler Script", Font.BOLD, 64));
        bufferg.setColor(Color.red);
        bufferg.drawString("The winner is", 20, 80);
        bufferg.setFont(new Font("Impact", Font.BOLD, 128));
        bufferg.setColor(Color.yellow);
        bufferg
                .drawString(
                        (players[0].getScore() == players[1].getScore()) ? "BOTH"
                        : ((players[0].getScore() > players[1]
                        .getScore()) ? players[0].getName()
                        : players[1].getName()), 80, 200);
        this.getGraphics().drawImage(offscreen, 0, 0, this);
        bufferg.dispose();
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        // TODO Auto-generated method stub
        if (arg0.getModifiers() == MouseEvent.BUTTON1_MASK && !moving) {
            if (putWhiteBall) {
                putWhiteBall = false;
                stick.hide(false);
            } else {
                if (!powerbar.getStatus()) {
                    powerbar.start();
                } else {
                    stick.hitBall(powerbar.getPower());
                    firstColli = -1;
                }
            }
        } else if (arg0.getModifiers() == MouseEvent.BUTTON3_MASK) {
            powerbar.stop();
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
        // checkcollision = null;
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub
        if (!powerbar.getStatus()) {
            stick.setMouse(arg0.getPoint());
        }
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void stop() {
        snooker = null;
    }

    class Refresh implements Runnable {

        public void run() {
            while (!gameOver) {
                try {
                    repaint();
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    // nothing
                }
            }
            winner();
            snooker = null;
        }
    }
}
