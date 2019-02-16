import java.io.*;
import java.net.*;
import java.util.Date;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

public class Server {
    public static void main(String[] args) {
	ServerLaunch s = new ServerLaunch();
    }
}

class ServerLaunch {
    public Data G = new Data();
    
    public ServerLaunch() {
	getterrain(G);
	Data t = null;
	boolean ok = false;
	int count = -1;
	ObjectOutputStream out;
	ObjectInputStream in = null;
	try {
	    System.out.println("Server IN/OUT Started");
	    while (true) {
		try (ServerSocket listener = new ServerSocket(9090)) {
		    try (Socket socket = listener.accept()) {
			
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			t = (Data) in.readObject();
			if (t == null || t.number == -1){
			    count++;
			    G.number = count;
			}else{
			    G.persos[t.number] = t.perso;
			}
			out.writeObject(G);
			out.flush();
			socket.close();
			if (t != null && swapblock(t.blockbase, G.blockbase) && t.number != -1)
			    G.blockbase = t.blockbase;
			socket.close();
		    } catch (Exception e) {e.printStackTrace();}
	    }
	    }
	} catch (Exception e) {
	    System.out.print(e);
	}
    }
    public boolean swapblock(block[] t, block[] b) {
	for (int i = 0; i < b.length; i++)
	    if ((b[i] != null && t[i] == null) || (b[i] != null && b[i].life() > t[i].life()))
		return true;
	return false;
    }
    

    public static void getterrain(Data G) {
	int i;
	for (i = 0; i < 11; i++) {
	    G.blockbase[i] = new block((int)(Math.random() * 20 + 10) * 20, i*128, false, true, (int)(Math.random() * 2 + 1));

	}
	G.blockbase[i] = new block(0, 500, true, true, 3, "key_blue", true);
	G.blockbase[i + 1] = new block(0, 600, true, true, 4, "red_blue", true);
    }

    public static int count(block[] b) {
	int i = 0;
	int count = 0;
	if (b != null)
	    for (; i < b.length; i++)
		if(b[i] != null)
		    count++;
	return count;
    }
}

