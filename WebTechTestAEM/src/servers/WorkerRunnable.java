package servers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;

public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;
    protected String method 	  = null;
    protected String protocol	  = null;
    protected String connection	  = null;

    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
        this.method = new String();
        this.protocol = new String();
        this.connection = new String();
    }

    public void run() {
    	
    	while (true) {
	        try {
	        	
	        	if (clientSocket.isClosed() || clientSocket.isInputShutdown() ||
	        			clientSocket.isOutputShutdown()) {
	        		
	        		clientSocket.close();
	        		break;
	        		
	        	}
	        	
		        InputStream input  = clientSocket.getInputStream();
		        OutputStream output = clientSocket.getOutputStream();
		            
		        InputStreamReader inputStream = new InputStreamReader(input);
		        BufferedReader in = new BufferedReader(inputStream);
		        String line = in.readLine();
		        
		        // find method and protocol
		        if ((line != null) && !line.isEmpty()) {
		        	String[] parameters = line.split(" ");
		        	
		        	if (parameters.length > 2) {
			        	method = parameters[0];
			        	protocol = parameters[2];
		        	} else {
		        		clientSocket.close();
		        		break;
		        	}
		        }
		        	
		        // find connection type
		        while ((line != null) && (!line.isEmpty()) && (connection.isEmpty())) {
		        	
		        	line = in.readLine();
		        	if (line.contains("Connection")) {
		        		this.connection = line.split(":")[1].trim();
		        	}
		        }
		        
		        // keep-alive behaviour
		        if (!protocol.equals(Constants.HTTP1_1) || (!connection.equals(Constants.KEEP_ALIVE))) {
		        	clientSocket.close();
		        	break;
		        }
		        	
		        long time = System.currentTimeMillis();
		            
		        // write message to output
		        output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
						this.serverText + " - " +
						time +
						"").getBytes());
		            
		        output.close();
		        input.close();
	        }
	         catch (IOException e) {
	            //report exception somewhere.
	            e.printStackTrace();
	        }
    	}
    }
}