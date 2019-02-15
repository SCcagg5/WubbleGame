import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

/**
 * A TCP server that runs on port 9090. When a client connects, it sends the client the
 * current date and time, then closes the connection with that client. This is arguably
 * just about the simplest server you can write. While simple, it has the disadvantage
 * that a client has to be completely served its date before the server will be able to
 * handle another client.
 */
public class Server {
    public static void main(String[] args) throws IOException {
	try (ServerSocket listener = new ServerSocket(9090)) {
	    System.out.println("The date server is running");
	    while (true) {
		try (Socket socket = listener.accept()) {
		    block[] obj = getterrain();
		    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		    out.writeObject(obj);
		    out.flush();
		    out.close();
		}
	    }
	}
    }

    public static block[] getterrain() {
	int i;
	block[] blockbase = new block[25];
	for (i = 0; i < 11; i++) {
	    blockbase[i] = new block((int)(Math.random() * 20 + 10) * 20, i*128, false, true, (int)(Math.random() * 2 + 1));
	}
	blockbase[i] = new block(0, 500, true, true, 3, "key_blue", true);
	//blockbase[i + 1] = new block(0, 600, true, true, 4, "key_red", true);
	return blockbase;
    }
}
