package test;

/**
 * @author Leona Cilibeanu
 */

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

public class ResponseTimeMap implements Callable<Integer> {
	
	protected int			workerID;
	protected boolean		isStopped = false;
	protected boolean		keepRunning	= true;
	protected long 			startTime;
	protected long 			endTime;
	protected long			responseTime;
	
	public ResponseTimeMap(int ID)
	{
    	this.workerID = ID;
	}
	
	/**
	 * Try to connect to the server and wait for response.
	 * After the response is received, compute response time and close connection.
	 * 
	 * @return		response time
	 * @throws IOException
	 */
	public Integer call() throws IOException
	{
		HttpURLConnection httpConn = null;
    	URL url;
    	
    	startTime = System.currentTimeMillis();

		url = new URL("http://localhost:9000/");
		httpConn =  (HttpURLConnection) url.openConnection();
    	
    	while (true) {	
	        if (!keepRunning) {
	        	break;
	        }
	        	
			httpConn.setInstanceFollowRedirects( false );
			httpConn.setRequestMethod("GET"); 
			    
			try{
			    httpConn.connect();
			    httpConn.getResponseCode();
			    
			    keepRunning = false;
			}catch(java.net.ConnectException e){
			    keepRunning = false;
			}
    	}
    	
    	endTime = System.currentTimeMillis();
    	httpConn.disconnect();

    	this.responseTime = endTime - startTime;
    	
    	return (int) responseTime;
	}
}