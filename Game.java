import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

class OPTION {
    public static final int FRAME = 60;
    public static final int MIN_FRAME = 20;
    public static final int MAX_FRAME = 250;

    public static final int MAX_GRAV = 2;
    public static final boolean GRAV = true;

    public static final Image  NULL = Toolkit.getDefaultToolkit().getImage("");
    public static final String[][][] OBJECT = new String[][][] {{{"./assets/perso.png","./assets/perso_wink.png"}, {"./assets/perso_down.png"}, {"./assets/perso_jump.png"}, {"./assets/perso_rr1.png", "./assets/perso_rr2.png", "./assets/perso_rr3.png", "./assets/perso_rr4.png"}, {"./assets/perso_rl1.png", "./assets/perso_rl2.png", "./assets/perso_rl3.png", "./assets/perso_rl4.png"}, { "./assets/perso_sr.png"}, { "./assets/perso_sl.png"}}, {{"./assets/block1.png"}, {"./assets/block1_damaged1.png"}, {"./assets/block1_damaged2.png"}}, {{"./assets/block2.png"}, {"./assets/block2_damaged1.png"}, {"./assets/block2_damaged2.png"}}, {{"./assets/key_blue_1.png", "./assets/key_blue_2.png", "./assets/key_blue_3.png", "./assets/key_blue_4.png", "./assets/key_blue_5.png", "./assets/key_blue_6.png", "./assets/key_blue_7.png", "./assets/key_blue_8.png", "./assets/key_blue_9.png"}}, {{"./assets/key_red_5.png", "./assets/key_red_6.png", "./assets/key_red_7.png", "./assets/key_red_8.png", "./assets/key_red_1.png", "./assets/key_red_2.png", "./assets/key_red_3.png", "./assets/key_red_4.png"}}, {{"./assets/slash_1.png", "./assets/slash_2.png", "./assets/slash_3.png", "./assets/slash_4.png"}}};
    public static final int[][][] COLLIDE = new int[][][] {{{37, 90, 50, 100}, {37, 90, 65, 100}, {38,89,50,100}, {37, 90, 50, 100}, {37, 90, 50, 100}, {37, 90, 50, 100}, {37, 90, 50, 100}}, {{0, 128, 0, 65}, {0, 128, 0, 65}, {0, 128, 0, 65}}, {{0, 128, 0, 128}, {0, 128, 0, 128}, {0, 128, 0, 128}}, {{40, 82, 26, 103}}, {{40, 82, 26, 103}}, {{0, 0, 0, 0}}, {{0,0,0,0}}};

    public static final int WIDTH = 160;
    public static final int HEIGHT = WIDTH / 16 * 9;
    public static final int SCALE = 3;

    public static final int SIZE_WIDTH = OPTION.WIDTH*OPTION.SCALE;
    public static final int SIZE_HEIGHT = OPTION.HEIGHT*OPTION.SCALE;
    public static final int PSIZE_WIDTH = OPTION.SIZE_WIDTH*2;
    public static final int PSIZE_HEIGHT = OPTION.SIZE_HEIGHT*2;
    
    public static int GETMOVE(int i) {
	return i * 50 / OPTION.FRAME;
    }

    public static Image[][] GETIMG(String[][] imgs) {
	Image[][] ret = new Image[10][10];
	for (int i = 0; i < imgs.length; i++)
	    for(int i2 = 0; i2 < imgs[i].length; i2++)
		ret[i][i2] = Toolkit.getDefaultToolkit().getImage(imgs[i][i2]);
	return ret;
    }

    public static int LIMG(Image[] img) {
	int i;
	for (i = 0; i < img.length && img[i] != null; i++); 
	return i;
    }
}

class calcul extends Thread implements Runnable{
    public calcul(personnage p, int i) {
	p.move(i);
    }

    public calcul(block[] b, personnage[] e, anime[] a){
	for (int i = 0; i < b.length; i++)
	    if (b[i] != null)
		if (b[i].life() == 0) {
		    b[i] = null;
		} else {
		b[i].listblock(b);
		b[i].gravity();
		}
	for (int i = 0; i < e.length; i++)
	    if (e[i] != null)
		if (e[i].life() == 0) {
		    e[i] = null;
		} else {
		    e[i].listblock(b);
		    e[i].gravity();
		}
	for (int i = 0; i < a.length; i++)
	    if (a[i] != null)
		if (a[i].life() == 0)
		    a[i] = null;
    }

    public static boolean checkdestroy(block[] b){
	for (int i = 0; i < b.length; i++)
	    if (b[i] != null)
		if (b[i].life() == 0)
		    return true;
	return false;
    }

    public calcul(Graphics g, Game obj, personnage perso, block[] block)
    {
	Image[][] imgs = perso.getimgs();
	for (int i = 0; i < imgs.length; i++)
	    for (int i2 = 0; i2 < OPTION.LIMG(imgs[i]); i2++)
		g.drawImage(imgs[i][i2], 0, 0, obj);
	for (int i3 = 0; i3 < block.length; i3++) {
	    if(block[i3] != null) {
		imgs = block[i3].getimgs();
		for (int i = 0; i < imgs.length; i++)
		    for (int i2 = 0; i2 < OPTION.LIMG(imgs[i]); i2++)
			g.drawImage(imgs[i][i2], 0, 0, obj);
	    }
	}
    }

    public calcul(Game G) {
	new Thread(new calcul(G.blockbase, G.enemies, G.anime));
	G.perso.pressed = G.pressed;
	if(G.pressed.contains("1") && (G.perso.nextCollide("right") == 0 || G.perso.nextCollide("left") == 0))
	    G.pressed.remove("1");
	G.perso.listblock(G.blockbase);
	for (int i = 0; i < G.pressed.toArray().length; i++){
	    if(G.pressed.toArray()[i] != null)
		new Thread(new calcul(G.perso, Integer.parseInt((String)G.pressed.toArray()[i])));
	}
	G.perso.gravity();
	if(!G.perso.canjump && G.perso.nextCollide("down") == 0)
	    G.perso.canjump = true;
    }

    public calcul(Game G, int k)
    {
        if (k == 72) {
	    G.debug = (G.debug+1) % 4;
	    if (G.debug < 3)
		G.objnumber = 0;
	} else if ((k == 74) && G.debug >= 3) {
	    G.objnumber = (G.objnumber + 1) % (1 + G.blockbase.length);
	} else if (k == 73) {
	    G.inv = (G.inv+1) % 2;
	} else if (k == 69) {
	    if(G.perso.pick(G.blockbase)) {
		G.inv = 1;
	    }else{
		G.generateterrain(G.perso.doorAndKey());
	    }
	} else if (k == 82) {
	    if(G.addblock(G.perso.drop()))
		G.inv = 1;
	} else if (k == 83 || k == 87 || k ==65 || k == 68) {
	    anime a = G.perso.attack(G.blockbase, G.enemies, (k != 87 ? k != 83 ? k != 65 ? "right" : "left" : "down" : "up"));
	    if (a == null)
		return;
	    for (int i = 0; i < G.anime.length; i++)
		if (G.anime[i] == null)
		    G.anime[i] = a;
	    if (!calcul.checkdestroy(G.blockbase))
		return;
	    new calcul(G.perso, 0);
	    new calcul(G.perso, 2);
	    G.perso.state(0);
	}
    }
}

public class Game extends Canvas implements Runnable, KeyListener {
    private static final long serialVersionUID = 1L;
    public List<String> pressed = new ArrayList<>();
    public static final String NAME = "Wubble";
    static JButton play;
    static Container pane;

    public static Image icon = Toolkit.getDefaultToolkit().getImage("./assets/perso.png");
    
    public personnage perso = new personnage(0);
    public block[] blockbase = new block[25];
    public personnage[] enemies = new personnage[5];
    public anime[] anime = new anime[5]; 


    private int FRAME = OPTION.FRAME;
    private int _FPS;
    private int _minfps = OPTION.MIN_FRAME;
    private int _maxfps = OPTION.MAX_FRAME;

    public int inv = 0;
    
    public int debug = 0;
    public int objnumber = 0;

    public boolean load = true;
    public boolean running = false;

    private JFrame frame;

    private Thread t; 
    
    private BufferedImage image = new BufferedImage(OPTION.WIDTH, OPTION.HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    public Game() {
	setMinimumSize(new Dimension(OPTION.WIDTH*OPTION.SCALE, OPTION.HEIGHT*OPTION.SCALE));
	setMaximumSize(new Dimension(OPTION.WIDTH*OPTION.SCALE, OPTION.HEIGHT*OPTION.SCALE));
	setPreferredSize(new Dimension(OPTION.WIDTH*2*OPTION.SCALE, OPTION.HEIGHT*2*OPTION.SCALE));

	frame = new JFrame(NAME);

	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setLayout(new BorderLayout());

	frame.add(this, BorderLayout.CENTER);
	frame.pack();
	frame.setIconImage(icon);

	frame.setResizable(false);
	frame.setLocationRelativeTo(null);
	frame.setVisible(true);
	frame.setFocusable(true);
	frame.setFocusTraversalKeysEnabled(true);
	frame.setAlwaysOnTop(true);
	addKeyListener(this);
	frame.requestFocusInWindow();
	this.start();
    }

    public boolean addblock(block b) {
	if (b.type == "none")
	    return false;
	for (int i = 0; i < blockbase.length; i++)
	    if (blockbase[i] == null){
		blockbase[i] = b.clone();
		blockbase[i].picked = false;
		blockbase[i].pickable = true;
		blockbase[i].gravity = true;
		return true;
	    }
	return false;
    }

    private synchronized void start() {
	running = true;
	new Thread(this).start();
    }

    public synchronized void stop() {
	running = false;
    }

    public void generateterrain(int n) {
	if (n == -1)
	    return;
	for (int i = 0; i < blockbase.length; i++)
	    blockbase[i] = null;
	this.perso.rX(OPTION.GRAV ? -300 : 10);
	this.perso.rY(OPTION.WIDTH * OPTION.SCALE - 50);
	if (n == 0) {
	    int i;
     	     for (i = 0; i < OPTION.WIDTH*2*OPTION.SCALE / 128 + 1; i++) {
		blockbase[i] = new block((int)(Math.random() * 20 + 10) * 20, i*128, false, true, (int)(Math.random() * 2 + 1));
	    }
	    blockbase[i] = new block(0, 500, true, true, 3, "key_blue", true);
	    blockbase[i + 1] = new block(0, 600, true, true, 4, "key_red", true);
	}
    }	

    public void run() {
	long lastTime = System.nanoTime();
	double nsPerTick = 1000000000D / 60D;

	int frames = 0;
	boolean shouldRender = false;

	long lastTimer = System.currentTimeMillis();
	double delta = 0;
	int sleepFrame = -1 + 1000 / (FRAME >= _maxfps ? _maxfps : FRAME <= _minfps ? _minfps : FRAME);
	generateterrain(0);
	while(running) {
	    long now = System.nanoTime();
	    delta += (now - lastTime) / nsPerTick;
	    lastTime = now;

	    while (delta >= 1) {
		delta -= 1;
		shouldRender = true;
	    }

	    try {
		Thread.sleep(sleepFrame);
	    }catch (InterruptedException e) {
		e.printStackTrace();
	    }

	    if (shouldRender) {
		frames++;
		render(frames);
	    }

	    if (System.currentTimeMillis() - lastTimer >= 1000) {
		lastTimer += 1000;
		this._FPS = frames;
		frames = 0;
	    }else{
		continue;
	    }
	}
    }

    public void render(int frames) {
	BufferStrategy bs = getBufferStrategy();
	if(bs ==null) {
	    createBufferStrategy(3);
	    return;
	}
	new Thread(new calcul(this));
	Graphics g = bs.getDrawGraphics();
	g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	if(load){
	    new Thread(new calcul(g, this, this.perso, this.blockbase));
	    load = false;
	}
	if (inv > 0) {
	    g.setColor(Color.LIGHT_GRAY);
	    g.fillRect(11,4,240,97);
	    block b;
	    g.setColor(Color.BLACK);
	    for (int i = 0; i < perso.inv.getinv().length; i++) {
		b = perso.inv.getinv()[i];
		g.fillRect(i*60 + 12, 5 + i/4 * 96, 58, 95);
		if (b != null)
		    g.drawImage(b.getimg(), b.Y(), b.X(), this);
	    }
	}
	for (int i = 0; i < blockbase.length; i++){
	    if (this.blockbase[i] != null)
		g.drawImage(this.blockbase[i].getimg(), this.blockbase[i].Y(), this.blockbase[i].X(), this);
	}
	g.drawImage(this.perso.getimg(), this.perso.Y(), this.perso.X(), this);
	for (int i = 0; i < anime.length; i++){
	    if (this.anime[i] != null)
		g.drawImage(this.anime[i].getimg(), this.anime[i].Y(), this.anime[i].X(), this);
	}
	g.setColor(Color.YELLOW);
	if (debug > 0) {
	    g.drawString("FPS: " + this._FPS, getWidth() - 165, 20);
	    if(debug > 1)
		if (objnumber == 0)
		    getdata(g, this.perso);
	        else {
		    while (this.blockbase[objnumber - 1] == null) {
			objnumber = (objnumber + 1) % (1 + this.blockbase.length);
			if (objnumber == 0){
			    getdata(g, this.perso);
			    break;
			}
		    }
		    if (objnumber != 0)
			getdata(g, this.blockbase[objnumber - 1]);
		}
	}
	g.dispose();
	bs.show();
    }

    public void getdata(Graphics g, personnage perso){
	g.drawString("next collide left  : " + perso.nextCollide("left"), getWidth() - 165, 35);
	g.drawString("next collide right : " + perso.nextCollide("right"), getWidth() - 165, 50);
	g.drawString("next collide up    : " + perso.nextCollide("up"), getWidth() - 165, 65);
	g.drawString("next collide down  : " + perso.nextCollide("down"), getWidth() - 165, 80);
	g.drawString("life               : " + perso.life(), getWidth() - 165, 95);
	if(debug < 3)
	    return;
	g.drawLine(0,perso.X() + perso.collide[perso.state][2],getWidth(),perso.X() + perso.collide[perso.state][2]);
	g.drawLine(0,perso.X() + perso.collide[perso.state][3],getWidth(),perso.X() + perso.collide[perso.state][3]);
	g.drawLine(perso.Y() + perso.collide[perso.state][0],0,perso.Y() + perso.collide[perso.state][0], getHeight());
	g.drawLine(perso.Y() + perso.collide[perso.state][1],0,perso.Y() + perso.collide[perso.state][1], getHeight());
	g.drawString("" + (getHeight() - perso.X() - perso.collide[perso.state][2]), 10, perso.X() + perso.collide[perso.state][2] - 5);
	g.drawString("" + (getHeight() - perso.X() - perso.collide[perso.state][3]), 10, perso.X() + perso.collide[perso.state][3] - 5);
	g.drawString("" + (perso.Y() + perso.collide[perso.state][0]), perso.Y() + perso.collide[perso.state][0] - 30 > 0 ? perso.Y() + perso.collide[perso.state][0] - 30 : 5 , 20);
	g.drawString("" + (perso.Y() + perso.collide[perso.state][1]), perso.Y() + perso.collide[perso.state][1] + 30 < getWidth() ? perso.Y() + perso.collide[perso.state][1] + 5 : getWidth() - 30, 20);
	g.drawString("" + (getHeight() - perso.X() - perso.collide[perso.state][2]), getWidth() - 35, perso.X() + perso.collide[perso.state][2] - 5);
	g.drawString("" + (getHeight() - perso.X() - perso.collide[perso.state][3]), getWidth() - 35, perso.X() + perso.collide[perso.state][3] - 5);
	g.drawString("" + (perso.Y() + perso.collide[perso.state][0]), perso.Y() + perso.collide[perso.state][0] - 30 > 0 ? perso.Y() + perso.collide[perso.state][0] - 30 : 5 , getHeight() - 5);
	g.drawString("" + (perso.Y() + perso.collide[perso.state][1]), perso.Y() + perso.collide[perso.state][1] + 30 < getWidth() ? perso.Y() + perso.collide[perso.state][1] + 5 : getWidth() - 30, getHeight() - 5);
	
    }

    public void getdata(Graphics g, block block){
	g.drawString("next collide left  : " + block.nextCollide("left"), getWidth() - 165, 35);
	g.drawString("next collide right : " + block.nextCollide("right"), getWidth() - 165, 50);
	g.drawString("next collide up    : " + block.nextCollide("up"), getWidth() - 165, 65);
	g.drawString("next collide down  : " + block.nextCollide("down"), getWidth() - 165, 80);
	g.drawString("life               : " + block.life(), getWidth() - 165, 95);
	if(debug < 3)
	    return;
	g.drawLine(0,block.X() + block.collide[block.state][2],getWidth(),block.X() + block.collide[block.state][2]);
	g.drawLine(0,block.X() + block.collide[block.state][3],getWidth(),block.X() + block.collide[block.state][3]);
	g.drawLine(block.Y() + block.collide[block.state][0],0,block.Y() + block.collide[block.state][0], getHeight());
	g.drawLine(block.Y() + block.collide[block.state][1],0,block.Y() + block.collide[block.state][1], getHeight());
	g.drawString("" + (getHeight() - block.X() - block.collide[block.state][2]), 10, block.X() + block.collide[block.state][2] - 5);
	g.drawString("" + (getHeight() - block.X() - block.collide[block.state][3]), 10, block.X() + block.collide[block.state][3] - 5);
	g.drawString("" + (block.Y() + block.collide[block.state][0]), block.Y() + block.collide[block.state][0] - 30 > 0 ? block.Y() + block.collide[block.state][0] - 30 : 5 , 20);
	g.drawString("" + (block.Y() + block.collide[block.state][1]), block.Y() + block.collide[block.state][1] + 30 < getWidth() ? block.Y() + block.collide[block.state][1] + 5 : getWidth() - 30, 20);
	g.drawString("" + (getHeight() - block.X() - block.collide[block.state][2]), getWidth() - 35, block.X() + block.collide[block.state][2] - 5);
	g.drawString("" + (getHeight() - block.X() - block.collide[block.state][3]), getWidth() - 35, block.X() + block.collide[block.state][3] - 5);
	g.drawString("" + (block.Y() + block.collide[block.state][0]), block.Y() + block.collide[block.state][0] - 30 > 0 ? block.Y() + block.collide[block.state][0] - 30 : 5 , getHeight() - 5);
	g.drawString("" + (block.Y() + block.collide[block.state][1]), block.Y() + block.collide[block.state][1] + 30 < getWidth() ? block.Y() + block.collide[block.state][1] + 5 : getWidth() - 30, getHeight() - 5);
    }

    public static void main(String[] a) {
	new Game();
    }

    public void keyPressed(KeyEvent e) {
	int k = e.getKeyCode();
	if (k > 36 && k < 41 && !pressed.contains("" + (k - 37))){
	    if ((k != 38 || perso.canjump) || !OPTION.GRAV)
		pressed.add("" + (k-37));
	} else
	new Thread( new calcul(this, k));
    }

    public void keyReleased(KeyEvent e) {
	pressed.remove("" + (e.getKeyCode() - 37));
	if(!pressed.contains("1") && perso.state() == 2)
	    if(pressed.contains("3"))
		perso.state(1);
	    else
		perso.state(0);
	if(!pressed.contains("3") && perso.state() == 1)
	    perso.state(0);
	if(!pressed.contains("2") && perso.state() == 3)
	    perso.state(0);
	if(!pressed.contains("0") && perso.state() == 4)
	    perso.state(0);
    }

    public void keyTyped(KeyEvent e) {
    }
}

class object {
    private int X;
    private int Y;
    protected int life;
    protected int state;
    protected Image[][] imgs;
    protected int count;
    public int[][] collide;
    public boolean overlaps;
    public boolean gravity;
    private int vector;
    public String type;
    private block[] blocks;
    public List<String> pressed;
    public boolean godown;
    public boolean pickable;
    public boolean picked;
    public int attack;
    
    public object(int x, int y, int life, String[][] imgs, int[][] collide,boolean overlaps, boolean gravity, String name) {
       	this.type = name;
	this.X = x;
	this.godown = true;
	this.Y = y;
	this.life = life;
	this.state = 0;
	this.count = 0;
	this.overlaps = overlaps;
	this.collide = collide;
	this.gravity = gravity;
	this.vector = 1;
	this.attack = 34;
	this.pressed = new ArrayList<String>();
	this.pickable = this.pickable ? true : false;
	this.picked = false;
	if (this.imgs == null)
	    this.imgs = OPTION.GETIMG(imgs);
    }

    public object(int x, int y, int life, String[][] imgs, int[][] collide,boolean overlaps, boolean gravity, String name, boolean pickable) {
	this(x, y, life, imgs, collide, overlaps, gravity, name);
	this.pickable = true;
    }

    public object(int x, int y, int life, Image[][] imgs, int[][] collide,boolean overlaps, boolean gravity, String name, boolean pickable) {
	this(x, y, life, new String[1][1] , collide, overlaps, gravity, name);
	this.pickable = true;
	this.imgs = imgs;
    }

    public void listblock(block[] b) {
	blocks = b;
    }

    public void X(int i) {
	X += i;
    }

    public void rX(int i) {
	X = i;
    }

    public int X() {
	return X;
    }

    public void Y(int i) {
	Y = Y + i;
    }

    public void rY(int i) {
	Y = i;
    }

    public int Y() {
	return Y;
    }

    public void life(int i) {
	life += i;
	life = life < 0 ? 0 : life > 1000 ? 1000 : life;
    }

    public int life() {
	return life;
    }

    public void state(int i) {
	state = i;
    }

    public int state() {
	return state;
    }
    
    public void gravity(){
	if (this.gravity && OPTION.GRAV){
	    if (this.canfall()) {
		this.vector += this.vector * 2 > OPTION.MAX_GRAV ? OPTION.MAX_GRAV : this.vector >= 2 ? this.vector / 2 : 1;
		this.move(OPTION.GETMOVE(this.vector)/3, "down");
	    } else
		this.vector = 1;
	}
    }

    public void move(int i, String dir) {
	int i2 = this.nextCollide(dir);
	i = i > i2 ? i2 : i;
	if (dir == "right")
	    this.Y(i);
	else if (dir == "left")
	    this.Y(-i);
	else if (dir == "up")
	    this.X(-i);
	else if (dir == "down")
	    this.X(i);
    }

    public boolean canfall() {
	return (this.nextCollide("down") != 0);
    }
    
    public int nextCollide(String dir){
	int wall = this.touchWall(dir);
	int obj = this.nextobj(dir);
	return (obj > wall ? wall : obj);
    }

    public int blockcollide(block obj, String dir){
	int ret = 0;
	if (dir == "down"){
	    if (this.Y() + this.collide[this.state][0] >  obj.Y() + obj.collide[obj.state()][0] && this.Y() + this.collide[this.state][0] <  obj.Y() + obj.collide[obj.state()][1] ||
		this.Y() + this.collide[this.state][1] >  obj.Y() + obj.collide[obj.state()][0] && this.Y() + this.collide[this.state][1] <  obj.Y() + obj.collide[obj.state()][1]) {
		ret = obj.X() - this.X() - this.collide[this.state()][3];
		if (ret > -(obj.collide[obj.state()][3] - obj.collide[obj.state()][2]) && ret < 0)
		    ret = 0;
		return ret;
	    }
	}
	if (dir == "left"){
	    if (this.X() + this.collide[this.state][2] > obj.X() + obj.collide[obj.state()][2] && this.X() + this.collide[this.state][2] < obj.X() + obj.collide[obj.state()][3] ||
		this.X() + this.collide[this.state][3] > obj.X() + obj.collide[obj.state()][2] && this.X() + this.collide[this.state][3] < obj.X() + obj.collide[obj.state()][3]) {
		ret = this.Y() - this.collide[this.state()][1] - obj.Y() + obj.collide[obj.state()][2];
		if(ret == 0 && type == "perso"){
		    if (this.pressed.contains("1") && this.godown && state != 6){
			this.godown = false;
			this.X(14);
		    }
		    this.state = 6;
		}
		return ret;
	    }
	}
	if (dir == "right"){
	    if (this.X() + this.collide[this.state][2] > obj.X() + obj.collide[obj.state()][2] && this.X() + this.collide[this.state][2] < obj.X() + obj.collide[obj.state()][3] ||
		this.X() + this.collide[this.state][3] > obj.X() + obj.collide[obj.state()][2] && this.X() + this.collide[this.state][3] < obj.X() + obj.collide[obj.state()][3]) {
		ret = obj.Y() + obj.collide[obj.state()][2] - this.Y() - this.collide[this.state()][1];
		if(ret == 0 && type == "perso")
		    this.state = 5;
		return ret;
	    }
	}
	if (dir == "up"){
	    if (this.Y() + this.collide[this.state][0] > obj.Y() + obj.collide[obj.state()][0] && this.Y() + this.collide[this.state][0] < obj.Y() + obj.collide[obj.state()][1] ||
		this.Y() + this.collide[this.state][1] > obj.Y() + obj.collide[obj.state()][0] && this.Y() + this.collide[this.state][1] < obj.Y() + obj.collide[obj.state()][1]) {
		return  (this.X() + this.collide[this.state][2]) - (obj.X() + obj.collide[obj.state()][3]);
	    }
	}
	return 10000;
    }

    public int nextobj(String dir){
	int ret = 10000;
	int nouv = 0;
	if (this.blocks != null)
	    for (int i = 0; i < this.blocks.length; i++) {
		if(this.blocks[i] != null && this != this.blocks[i] && !this.blocks[i].overlaps) {
		    nouv = blockcollide(this.blocks[i], dir);
		    ret = ret > nouv && nouv >= 0 ? nouv : ret;
		}
	    }
	return ret;
    }

    public int touchWall(String dir){
	if (state >= this.collide.length)
	    state = 0;
	if (dir == "left")
	    return this.Y() + this.collide[state][0];
	if (dir == "right")
	    return OPTION.PSIZE_WIDTH - this.Y() - this.collide[state][1];
	if (dir == "up" )
	    return this.Y() + this.collide[state][02];
	if (dir == "down"){
	    return OPTION.PSIZE_HEIGHT - this.X() - this.collide[state][3];
	}
	return 0;
    }

    public Image getimg() {
	if (life == 0)
	   return OPTION.NULL;
	if (state >= imgs.length)
	    state = 0;
	count++;
	if(count >= OPTION.FRAME)
	    count = 0;
	int n = OPTION.LIMG(imgs[state]);
	int f = OPTION.FRAME;
	int ret = ((int) Math.ceil(count * n / f));
	return this.imgs[state][ret];
    }

    public Image[][] getimgs(){
	return this.imgs;
    }
}

class inventory {
    private block[] obj;

    public inventory() {
	obj = new block[4];
    }

    public boolean add(block o) {
	for (int i = 0; i < obj.length; i++) {
	    if (obj[i] == null) {
		obj[i] = o.clone();
		o.life(-10000000);
		obj[i].rY(i * 60 - 20);
		obj[i].rX(- 10);
		obj[i].overlaps = true;
		obj[i].gravity = false;
		obj[i].picked = true;
		return true;
	    }
	}
	return false;
    }

    public boolean remove(String type) {
	for (int i = 0; i < obj.length; i++) {
	    if (obj[i] != null && obj[i].type == type) {
		obj[i] = null;
		return true;
	    }
	}
	return false;
    }

    public block[] getinv() {
	return this.obj;
    }
}

class block extends object {
    private int maxlife;
    
    public block(int x, int y, boolean over, boolean grav, int i){
	super(x, y, 100, OPTION.OBJECT[i], OPTION.COLLIDE[i], over, grav, "block");
	maxlife = 100;
    }

    public block(int x, int y, boolean over, boolean grav, int i, String type){
	super(x, y, 100, OPTION.OBJECT[i], OPTION.COLLIDE[i], over, grav, type);
	maxlife = 100;
    }

    public block(int x, int y, boolean over, boolean grav, int i, String type, boolean pick){
	super(x, y, 100, OPTION.OBJECT[i], OPTION.COLLIDE[i], over, grav, type, pick);
	maxlife = 100;
    }

    public block(int x, int y, int life, boolean over, boolean grav, int[][] col, Image[][] Img, String type, boolean pick){
	super(x, y, life, Img, col, over, grav, type, pick);
	maxlife = 100;
    }

    public block clone() {
	return new block(this.X(), this.Y(), this.life(),overlaps, gravity, collide, imgs, type, pickable);
    }

    public void life(int i) {
	this.life += i;
	if (this.life() < 0)
	    this.life = 0;
	else if (this.life() < 33)
	    this.state(2);
	else if (this.life < 67)
	    this.state(1);
    }
}

class personnage extends object{

    private int speed;
    private int wink;
    public boolean canjump;
    public inventory inv;
    public int cooldown;

    public personnage(int i){
	super(0, 0, 100, OPTION.OBJECT[i], OPTION.COLLIDE[i], false, true, "perso");
	speed = OPTION.GETMOVE(6);
	wink = 0;
	cooldown = 20;
	canjump = false;
	inv = new inventory();
    }
    
    public Image getimg() {
	cooldown--;
	if (life() == 0)
	    return OPTION.NULL;
	if (state >= imgs.length)
	    state = 0;
	count++;
	if(count >= OPTION.FRAME)
	    count = 0;
	int n = OPTION.LIMG(imgs[state]);
	int f = OPTION.FRAME;
	int ret = ((int) Math.ceil(count * n / f));
	
	if (this.type == "perso" && this.state() == 0) {
	    if(wink > 0){
		wink--;
	    }else{
		if ((int)(Math.random() * 200) == 5)
		    wink = (int)(Math.random() * 50) + 10;
	    }
	    wink = wink > 30 ? 30 : wink;
	    ret = (wink > 0 ? 1 : 0);
	}
	return this.imgs[state][ret];
    }

    public boolean pick(block[] b) {
	int X = this.X() + (this.collide[state][1] - this.collide[state][0]);
	int Y = this.Y() + (this.collide[state][3] - this.collide[state][2]);
	int o[] = new int[4];
	for (int i = 0; i < b.length; i++) {
	    if (b[i] != null && b[i].pickable && !b[i].picked && b[i].life() > 0) {
		o[0] = b[i].X() + b[i].collide[b[i].state()][0];
		o[1] = b[i].X() + b[i].collide[b[i].state()][1];
		o[2] = b[i].Y() + b[i].collide[b[i].state()][2];
		o[3] = b[i].Y() + b[i].collide[b[i].state()][3];
		if(o[0] <= X && o[1] >= X && o[2] <= Y && o[3] >= Y) {
		    this.inv.add(b[i]);
		    b[i] = null;
		    return true;
		}
	    }
	}
	return false;
    }

    public anime attack(block[] b, personnage[] e, String dir) {
	if (cooldown > 0)
	    return null;
	int[] XY = new int[2];
	XY[0] = (dir == "up" || dir == "down" ? dir == "up" ? -1 : 1 : 0);
	XY[1] = (dir == "left" || dir == "right" ? dir == "left" ? -1 : 1 : 0);
	int[] XYb = ATTACK.slash(this, b, e, XY);
	cooldown = OPTION.FRAME/2;
	return new anime(XYb[0] - 64, XYb[1] - 64, 5, "attack");
    }	     

    public block drop() {
	block[] b = this.inv.getinv();
	for(int i = 1; i < b.length; i++)
	    if(b[i] == null && b[i - 1] != null) {
		block ret = b[i - 1].clone();
		this.inv.remove(b[i - 1].type);
		return ret;
	    }
	return (new block(-100, -100, true, true, 3, "none", true));
    }

    public int doorAndKey() {
	return -1;
    }
    

    public void move(int  i) {
	if (i % 2 == 0) {
	    if(i == 2)
		this.state(3);
	    if(i == 0)
		this.state(4);
	    this.move(this.speed, (i == 0 ? "left" : "right"));
	} else {
	    if (i == 3) {
		if (this.state() == 0 || this.state == 2)
		    this.state(1);
		this.move(OPTION.GRAV ? OPTION.GETMOVE(3) : this.speed, "down");
	    }else {
		if ((this.state() == 0 || this.state() == 5 || this.state() == 6) && nextCollide("down") != 0)
		    this.state(2);
		this.move(OPTION.GRAV ? OPTION.GETMOVE(20) : this.speed, "up");
		canjump = false;
		this.godown = true;
	    }

	}
    }
}

class anime extends object {
    private int time;
    
    public anime(int x, int y, int i, String type){
	super(x, y, 1, OPTION.OBJECT[i], OPTION.COLLIDE[i], true, false, type);
	this.time = OPTION.FRAME * 2;
    }

    public Image getimg() {
	time--;
	if (time < 0)
	    this.life(-10);
	if (this.life() == 0)
	    return OPTION.NULL;
	if (state >= imgs.length)
	    state = 0;
	count++;
	if(count >= OPTION.FRAME)
	    count = 0;
	int n = OPTION.LIMG(imgs[state]);
	int f = OPTION.FRAME;
	int ret = ((int) Math.ceil(count * n / f));
	return this.imgs[state][ret];
    }
}    

class ATTACK {
    public static int[] slash(personnage P, block[] b, personnage[] e, int[] dir) {
	int X = P.X() + dir[0] * 60 + P.collide[P.state()][0] + (P.collide[P.state()][1] - P.collide[P.state()][0]) / 2 + 7;
	int Y = P.Y() + dir[1] * 60 + P.collide[P.state()][2] + (P.collide[P.state()][3] - P.collide[P.state()][2]) /4;
	int[] XY = new int[] {X, Y};
	int o[] = new int[4];
	for (int i = 0; i < b.length; i++) {
	    if (b[i] != null && b[i].overlaps == false && b[i].life() > 0) {
		o[0] = b[i].X() + b[i].collide[b[i].state()][2];
		o[1] = b[i].X() + b[i].collide[b[i].state()][3];
		o[2] = b[i].Y() + b[i].collide[b[i].state()][0];
		o[3] = b[i].Y() + b[i].collide[b[i].state()][1];
		if(o[0] <= X && o[1] >= X && o[2] <= Y && o[3] >= Y) {
		    b[i].life(-P.attack);
		    return XY;
		}
	    }
	}
	for (int i = 0; i < e.length; i++) {
	    if (e[i] != null && e[i].overlaps == false && e[i].life() > 0) {
		o[0] = b[i].X() + b[i].collide[b[i].state()][0];
		o[1] = b[i].X() + b[i].collide[b[i].state()][3];
		o[2] = b[i].Y() + b[i].collide[b[i].state()][0];
		o[3] = b[i].Y() + b[i].collide[b[i].state()][1];
		if(o[0] <= X && o[1] >= X && o[2] <= Y && o[3] >= Y) {
		    e[i].life(-P.attack);
		    return XY;
		}
	    }
	}
	return XY;
    }
}
