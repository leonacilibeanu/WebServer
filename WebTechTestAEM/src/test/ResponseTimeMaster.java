package test;

/**
 * @author Leona Cilibeanu
 */

import java.util.*;
import java.util.concurrent.*;

public class ResponseTimeMaster {
	
	private ExecutorService executorService;
	private int numThreads;
	
	public ResponseTimeMaster(int numThreads)
	{	
		this.numThreads = numThreads;
		executorService = Executors.newFixedThreadPool(numThreads);
	}
	
	/**
	 * Runs the specified number of threads in order to get the response time.
	 * Creates a LinkedList with the result.
	 * 
	 * @return a list with the response time that each thread returned
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public LinkedList<Integer> map() throws ExecutionException, InterruptedException
	{
		ExecutorCompletionService<Integer> map = new ExecutorCompletionService<>(executorService);
	
		long taskCount = 0;
		
		// run threads
		for(int i = 0; i < numThreads; i++)
		{
			ResponseTimeMap mapTask = new ResponseTimeMap(i);

			map.submit(mapTask);	
			taskCount++;
		}
	
		LinkedList<Integer> mapPartialSolutions = new LinkedList<Integer>();
		
		// create solution
		for (int i = 0; i < taskCount; i++)
		{
			// it will wait for the thread to stop if it's still running
			Integer mps = map.take().get();

			mapPartialSolutions.add(mps);
		}
		
		return mapPartialSolutions;
	}
	
	/**
	 * Runs the reduce task for the given partial solution.
	 * 
	 * @param mapPartialSolutions
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	void reduce(LinkedList<Integer> mapPartialSolutions) throws ExecutionException, InterruptedException
	{
		ExecutorCompletionService<Integer> reduce = new ExecutorCompletionService<>(executorService);
		
		for (int mps: mapPartialSolutions) {
			ResponseTimeReduce reduceTask = new ResponseTimeReduce(mps);
			
			reduce.submit(reduceTask);
		}
	}
	
	void shutdown()
	{
		executorService.shutdown();
	}
}
