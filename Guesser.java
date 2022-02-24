// Password Guesser
// Jesse Harris
// 2022-02-23
// CS 3600

/*
Consider the following program that "guesses" to figure out a password based on it's CRC32 hash.
The password could be anything in the ASCII range from 0x30 - 0x7A


1. Add the capability to do this guessing with multiple threads. 15 pts
2. Use shared memory to let a child know it doesn't have to keep guessing. 10 pts
3. Experiment with the number of threads. Is there any speed up with 2, 3, 4? 5 pts
*/


import java.util.zip.CRC32;
import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class Guesser
{
	public static void main(String [] args)
	{
		// Get the number of CPU cores. We will create this many threads to speed things up.
		int cores = Runtime.getRuntime().availableProcessors();
		List<Cracker> crackerTasks = new ArrayList<>(); // list of tasks used to crack the password
		ExecutorService es = Executors.newFixedThreadPool(cores); // thread pool
		byte[] result; // needed to capture returned value

		//long passwd = 2656977832L; //three char password. Very little time needed.
		long passwd = 3281894034L; //four char password. A little time needed. 
		//long passwd = 2636021861L; 	//five char password. More time needed. 
		
		System.out.println("Detected " + cores + " CPU cores.");

		// Create a new Cracker object for each thread.
		for(int i = 0; i < cores; i++)
		{
			crackerTasks.add(new Cracker(passwd, 4));
		}

		try {
			// Add the list of tasks to the thread pool. The invokeAny() method allows
			// ending termination when any of the threads gets the answer.
			result = es.invokeAny(crackerTasks);
			// This kills ALL outstanding threads. We had to make sure our tasks respect it.
			es.shutdownNow();
		// This error trapping is required for Executors to work.
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}

class Cracker implements Callable<byte[]>
{
	private CRC32 cr = new CRC32();
	private SecureRandom r = new SecureRandom();
	private long guess = 0;
	private int passlength;
	private double startTime = 0;
	private double endTime = 0;

	private long passwd;

	// Constructor; requries the password CRC32 and the best guess on length.
	public Cracker(long passwd, int passlength)
	{
		this.passwd = passwd;
		this.passlength = passlength;
	}

	// This is where we create a Callable (rather than Runnable) to use Executors.
	@Override
	public byte[] call()
	{
		byte b[] = new byte[passlength];
		// We'll attempt to do all of our guessing in the try block.
		try {
			startTime = System.currentTimeMillis();
			while (guess != this.passwd)
			{
				// Check to see if the thread has been interrupted by shutdownNow().
				// If it has, we can throw an exception to end it now.
				if(Thread.currentThread().isInterrupted())
				{
					// Throwing the exception to terminate the interrupted thread.
					throw new InterruptedException();
				}
				// This redesigned loop lets us use a variable length password in the same method.
				for(int i = 0; i < passlength; i++)
				{
					b[i] = (byte)(r.nextInt(75) + '0');
				}
				
				cr.reset();
				cr.update(b);
				
				guess = cr.getValue();	
			}
			endTime = System.currentTimeMillis();
			// Print out the correct guess. As before, loops are more efficient.
			for(byte correctguess : b)
			{
				System.out.print((char)correctguess);
			}
			System.out.println();
			// Print the elapsed time.
			System.out.println("Time Elapsed : " + (endTime-startTime)/1000.0);
		// Catch block for interrupted threads.
		} catch (InterruptedException e) {
			// null
		}
		return b;
	}
}