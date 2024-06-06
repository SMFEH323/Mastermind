import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Sayf Elhawary
 */
public class MastermindServer {

	public static void main ( String[] args ) {
		try {
			ServerSocket server = new ServerSocket(9050);
			System.out.println("server listening...");
			for ( ; true ; ) {
				Socket connection = server.accept();
				System.out.println("...connection established");
				ClientRunner cr = new ClientRunner(connection);
				Thread thread = new Thread(cr);
				thread.start();
			}
		} catch ( IOException e ) {
			System.out.println("socket error: " + e.getMessage());
		}
	}

}
