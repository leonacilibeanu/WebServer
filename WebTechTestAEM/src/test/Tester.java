package test;

/**
 * @author Leona Cilibeanu
 */

import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.junit.*;

import servers.*;

public class Tester {
	protected static ThreadPooledServer server;
	
	@BeforeClass
	public static void startThreadPooledServer() {
		server = new ThreadPooledServer(Constants.PORT);
		new Thread(server).start();
	}
	
	@AfterClass
	public static void stopThreadPooledServer() {
		server.stop();
	}
	
	/**
	 * Verify connection and received output.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSimpleRequest() throws Exception {
	    URL url = new URL("http://localhost:9000/");
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    assertEquals(servers.Constants.MESSAGE, in.readLine().split(servers.Constants.SEPARATOR)[0]);
	}
	
	/**
	 * Verify the keep alive behaviour.
	 * Expected to fail because value of Connection is "close".
	 * 
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testConnectionClose() throws Exception {
		URL url = new URL("http://localhost:9000/");
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestProperty("Connection", "close");
	    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    assertEquals(servers.Constants.MESSAGE, in.readLine().split(servers.Constants.SEPARATOR)[0]);
	}
	
	/**
	 * Try to access url using https protocol.
	 * Expected to fail. There are allowed only http connections.
	 * 
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testHttps() throws Exception {
		URL url = new URL("https://localhost:9000/");
	    URLConnection conn = url.openConnection();
	    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    assertEquals(servers.Constants.MESSAGE, in.readLine().split(servers.Constants.SEPARATOR)[0]);
	}
	
	/**
	 * Test load.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoad() throws Exception{
		ResponseTimeMaster master = new ResponseTimeMaster(100);
		
		master.map();
		
		master.shutdown();
	}
}
