package test;

/**
 * @author Leona Cilibeanu
 */

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.io.*;

import javax.swing.JFrame;

import org.math.plot.*;

import servers.Constants;
import servers.ThreadPooledServer;


public class WebServerLoad {
	
	protected static LinkedList<Integer> mapPartialSolutions	= new LinkedList<>();
	protected static ThreadPooledServer server;
	
	/**
	 * Initialize file.
	 * If it doesn't exist, create it. Otherwise, overwrite the existing
	 * content.
	 * 
	 * @throws IOException
	 */
	public static void initialize() throws IOException {
		
		File file = new File(Constants.RESPONSE_TIME + ".txt");
		if (file.exists()){
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("");
			bw.close();
		} else {
			file.createNewFile();
		}
		
	}
	
	
	/**
	 * Compute average bandwidth for the set of response times and write it to file.
	 * Bytes length of the sent message it is constant.
	 * 
	 * @throws IOException
	 */
	public static void getBandwidth() throws IOException {
		int result;
		float partialResult = 0;
		
		for (int mps: mapPartialSolutions) {
			partialResult += (float) 1000 * Constants.BYTES_LENGTH / mps;
		}
		
		result = (int) partialResult / mapPartialSolutions.size();
		
		writeToFile(Integer.toString(result), Constants.BANDWITH);
	}
	
	
	/**
	 * Get a set of response times using MapReduce.
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void getResponseTimePlot() throws ExecutionException, InterruptedException, IOException {
		ResponseTimeMaster master = new ResponseTimeMaster(Constants.NUM_THREADS);

		mapPartialSolutions = master.map();
		master.reduce(mapPartialSolutions);
		
		master.shutdown();
		
		try {
		    Thread.sleep(2 * 1000);
		 
		} catch (InterruptedException e) {
		    e.printStackTrace();
		} 
			
		showResponseTime();
	}
	
	
	/**
	 * Create plot based on the response times that were previously
	 * written to file.
	 * X - number of threads
	 * Y - response time
	 * 
	 * @throws IOException
	 */
	public static void showResponseTime() throws IOException {
		int index_x = 0, index_y = 0;
		double[] x = new double[Constants.NUM_THREADS];
		double[] y = new double[Constants.NUM_THREADS];
		
		BufferedReader br = new BufferedReader(new FileReader(Constants.RESPONSE_TIME + ".txt"));
	    try {
	        String line = br.readLine();

	        while (line != null) {
	        	
	        	if (!line.isEmpty()) {
	        		x[index_x] = index_x;
	        		y[index_y] = Double.parseDouble(line);
	        		
	        		index_x++;
	        		index_y++;
	        	} 
	        	
	            line = br.readLine();
	        }
	        
	    } finally {
	        br.close();
	    }
	    
	    // create PlotPanel
	    Plot2DPanel plot = new Plot2DPanel();
	   
	    // add a line plot to the PlotPanel
	    plot.addLinePlot("my plot", x, y);
	   
	    // put the PlotPanel in a JFrame, as a JPanel
	    JFrame frame = new JFrame("Response time plot");
	    frame.setContentPane(plot);
//	    frame.setBounds(x.length, y.length, Constants.WIDTH, Constants.HEIGHT);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setVisible(true);
	    frame.setAlwaysOnTop(true);
	}
	
	
	/**
	 * Writes text to file.
	 * 
	 * @param text
	 * @param filename
	 * @throws IOException
	 */
	public static void writeToFile (String text, String filename) throws IOException {
		
		File file = new File(filename + ".txt");
		 
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(text + " kb/s");
		bw.close();
		
	}
	
	public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
	
		initialize();

		getResponseTimePlot();
		
		getBandwidth();	

	}
}