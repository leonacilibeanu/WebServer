package servers;

/**
 * 
 * @author Leona Cilibeanu
 * 
 * Run server example
 *
 */

public class Main {

	public static void main(String[] args) {

		ThreadPooledServer server = new ThreadPooledServer(9000);
		new Thread(server).start();

		try {
		    Thread.sleep(1000 * 1000);
		 
		} catch (InterruptedException e) {
		    e.printStackTrace();
		} 
		
		System.out.println("Stopping Server");
		server.stop();
	}

}
