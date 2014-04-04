package test;

/**
 * @author Leona Cilibeanu
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.*;
import servers.Constants;

public class ResponseTimeReduce implements Callable<Integer> {
	private int mapPartialSolution;
	
	public ResponseTimeReduce(int mapPartialSolution)
	{
		this.mapPartialSolution = mapPartialSolution;
	}
	
	/**
	 * Writes to file each result 
	 * 
	 * @throws IOException
	 */
	public Integer call() throws IOException
	{
		File file = new File(Constants.RESPONSE_TIME + ".txt");
		 
		if (!file.exists()) {
			file.createNewFile();
		}
		
		PrintWriter out = new PrintWriter(new FileWriter(file, true));

		out.append(mapPartialSolution + "\n");
		out.close();
		
		return 0;
	}
}