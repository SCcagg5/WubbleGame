import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

class OPTION {
    public static final int FRAME = 70;
    public static final int MIN_FRAME = 20;
    public static final int MAX_FRAME = 250;

    public static final int MAX_GRAV = 2;
    public static final boolean GRAV = true;
    
    public static final String[][][] OBJECT = new String[][][] {{{"./assets/perso.png","./assets/perso_wink.png"}, {"./assets/perso_down.png"}, {"./assets/perso_jump.png"}, {"./assets/perso_rr1.png", "./assets/perso_rr2.png", "./assets/perso_rr3.png", "./assets/perso_rr4.png"}, {"./assets/perso_rl1.png", "./assets/perso_rl2.png", "./assets/perso_rl3.png", "./assets/perso_rl4.png"}, { "./assets/perso_sr.png"}, { "./assets/perso_sl.png"}}, {{"./assets/block1.png"}}, {{"./assets/block2.png"}}};
    public static final int[][][] COLLIDE = new int[][][] {{{37, 90, 50, 100}, {37, 90, 65, 100}, {45,82,50,100}, {37, 90, 50, 100}, {37, 90, 50, 100}, {37, 90, 50, 100}, {37, 90, 50, 100}}, {{0, 128, 0, 65}}, {{0, 128, 0, 128}}};

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

    public calcul(block b){
	b.gravity();
    }

    public calcul(Graphics g, Game obj)
    {
	Image[][] imgs = obj.perso.getimgs();
	for (int i = 0; i < imgs.length; i++)
	    for (int i2 = 0; i2 < OPTION.LIMG(imgs[i]); i2++)
		g.drawImage(imgs[i][i2], obj.perso.Y(), obj.perso.X(), obj);
    }
}

public class Game extends Canvas implements Runnable, KeyListener {
    private static final long serialVersionUID = 1L;
    private List<String> pressed = new ArrayList<>();
    public static final String NAME = "Wubble";
    static JButton play;
    static Container pane;

    public static Image icon = Toolkit.getDefaultToolkit().getImage("./assets/perso.png");
    
    public personnage perso = new personnage(0);
    private block[] blockbase = new block[OPTION.WIDTH*2*OPTION.SCALE / 128 + 1];
    private int FRAME = OPTION.FRAME;
    private int _FPS;
    private int _minfps = OPTION.MIN_FRAME;
    private int _maxfps = OPTION.MAX_FRAME;

    private int debug = 0;
    private int objnumber = 0;

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

    private synchronized void start() {
	running = true;
	new Thread(this).start();
    }

    public synchronized void stop() {
	running = false;
    }

    public void generateterrain() {
	for (int i = 0; i < blockbase.length; i++) {
	    blockbase[i] = new block((int)(Math.random() * 5 + 4) * 20, i*128, false, true, (int)(Math.random() * 2 + 1));
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
	generateterrain();
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
	for (int i = 0; i < blockbase.length; i++){
	    new Thread(new calcul(blockbase[i]));
	}
	perso.listblock(blockbase);
	for (int i = 0; i < pressed.toArray().length; i++){
	    new Thread(new calcul(perso, Integer.parseInt((String)pressed.toArray()[i])));
	}
	perso.gravity();
	Graphics g = bs.getDrawGraphics();
	g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	if(load){
	    new Thread(new calcul(g, this));
	    load = false;
	}
	g.drawImage(this.perso.getimg(), this.perso.Y(), this.perso.X(), this);
	for (int i = 0; i < blockbase.length; i++){
	    g.drawImage(this.blockbase[i].getimg(), this.blockbase[i].Y(), this.blockbase[i].X(), this);
	}
	g.setColor(Color.YELLOW);
	if (debug > 0) {
	    g.drawString("FPS: " + this._FPS, getWidth() - 165, 20);
	    if(debug > 1)
		if (objnumber == 0)
		    getdata(g, this.perso);
	        else
		    getdata(g, this.blockbase[objnumber - 1]);
	}
	g.dispose();
	bs.show();
    }

    public void getdata(Graphics g, personnage perso){
	g.drawString("next collide left  : " + perso.nextCollide("left"), getWidth() - 165, 35);
	g.drawString("next collide right : " + perso.nextCollide("right"), getWidth() - 165, 50);
	g.drawString("next collide up    : " + perso.nextCollide("up"), getWidth() - 165, 65);
	g.drawString("next collide down  : " + perso.nextCollide("down"), getWidth() - 165, 80);
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
	if (e.getKeyCode() > 36 && e.getKeyCode() < 41 && !pressed.contains("" + (e.getKeyCode() - 37)))
	    pressed.add("" + (e.getKeyCode()-37));
	else if (e.getKeyCode() == 72) {
	    debug = (debug+1) % 4;
	    if (debug < 3)
		objnumber = 0;
	} else if ((e.getKeyCode() == 74 || e.getKeyCode() == 75) && debug >= 3) {
	    objnumber = (objnumber + ((e.getKeyCode() == 74) ? -1 : 1)) % (1 + this.blockbase.length);
	}
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
    private int life;
    protected int state;
    protected Image[][] imgs;
    protected int count;
    public int[][] collide;
    private boolean overlaps;
    private boolean gravity;
    private int vector;
    public String type;
    private block[] blocks;
    
    public object(int x, int y, int life, String[][] imgs, int[][] collide,boolean overlaps, boolean gravity, String name) {
	this.type = name;
	this.X = x;
	this.Y = y;
	this.life = life;
	this.state = 0;
	this.imgs = OPTION.GETIMG(imgs);
	this.count = 0;
	this.overlaps = overlaps;
	this.collide = collide;
	this.gravity = gravity;
	this.vector = 1;
    }

    public void listblock(block[] b) {
	blocks = b;
    }

    public void X(int i) {
	X += i;
    }

    public int X() {
	return X;
    }

    public void Y(int i) {
	Y = Y + i;
    }

    public int Y() {
	return Y;
    }

    public void life(int i) {
	life += i;
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
	if(this.overlaps)
	    return 10000;
	int wall = this.touchWall(dir);
	int obj = this.nextobj(dir);
	return (obj > wall ? wall : obj);
    }

    public int blockcollide(block obj, String dir){
	int ret = 0;
	if (dir == "down"){
	    if (this.Y() + this.collide[this.state][0] > obj.Y() + obj.collide[obj.state()][0] && this.Y() + this.collide[this.state][0] < obj.Y() + obj.collide[obj.state()][1] ||
		this.Y() + this.collide[this.state][1] > obj.Y() + obj.collide[obj.state()][0] && this.Y() + this.collide[this.state][1] < obj.Y() + obj.collide[obj.state()][1]) {

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
		if(ret == 0)
		    this.state = 6;
		return ret;
	    }
	}
	if (dir == "right"){
	    if (this.X() + this.collide[this.state][2] > obj.X() + obj.collide[obj.state()][2] && this.X() + this.collide[this.state][2] < obj.X() + obj.collide[obj.state()][3] ||
		this.X() + this.collide[this.state][3] > obj.X() + obj.collide[obj.state()][2] && this.X() + this.collide[this.state][3] < obj.X() + obj.collide[obj.state()][3]) {
		ret = obj.Y() + obj.collide[obj.state()][2] - this.Y() - this.collide[this.state()][1];
		if(ret == 0)
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
		nouv = blockcollide(this.blocks[i], dir);
		ret = ret > nouv && nouv >= 0 ? nouv : ret;
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

class inventory { }

class block extends object {
    public block(int x, int y, boolean over, boolean grav, int i){
	super(x, y, 10000, OPTION.OBJECT[i], OPTION.COLLIDE[i], over, grav, "block");
    }
}

class personnage extends object{

    private int speed;
    private int wink;

    public personnage(int i){
	super(0, 0, 100, OPTION.OBJECT[i], OPTION.COLLIDE[i], false, true, "perso");
	speed = OPTION.GETMOVE(6);
	wink = 0;
    }
    
    public Image getimg() {
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

    public Image[][] getimgs(){
	return this.imgs;
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
		this.move(OPTION.GETMOVE(3), "down");
	    }else {
		if ((this.state() == 0 || this.state() == 5 && nextCollide("right") != 0 || this.state() == 6 && nextCollide("left") != 0) && nextCollide("down") != 0)
		this.state(2);
		if (this.state() != 6 && this.state() != 5)
		    this.move(OPTION.GETMOVE(20), "up");
	    }

	}
    }
}
