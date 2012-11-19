package cs555.crawler.pool;

import java.util.LinkedList;

/**
 * 
 * @author levans
 * Thread Pool Manager is in charge of keeping track of all tasks to be performed and currently running jobs
 * 
 * It also assigns any available threads a task that is waiting to execute
 */
public class ThreadPoolManager
{
	private final int nThreads;
	private final PoolWorker[] threads;
	private final LinkedList<Task> queue;

	public ThreadPoolManager(int t)
	{
		this.nThreads = t;
		queue = new LinkedList<Task>();
		threads = new PoolWorker[nThreads];

	}

	// Start all of the threads and get them ready to do work
	public void start(){
		for (int i=0; i<nThreads; i++) {
			threads[i] = new PoolWorker(i);
			threads[i].start();
		}
	}
	
	// We have received a task. Add it to the queue and wake up a thread to run it
	public void execute(Task t) {
		synchronized(queue) {
			queue.addLast(t);
			queue.notify();
		}
	}

	// Stop all the threads through an interrupt
	public void stop(){
		for (PoolWorker worker : threads){
			worker.interrupt();
		}
	}
	
	/**
	 * 
	 * @author levans
	 * Pool Worker is the thread that will be assigned a task
	 */
	private class PoolWorker extends Thread {
		int id = 0;
		
		public PoolWorker(int i){
			id = i;
		}
		
		public void run() {
			Runnable r;

			while (true) {
				
				// Wait until there is work available
				synchronized(queue) {
					while (queue.isEmpty()) {
						
						// There is no work to be done. Wait
						try
						{
							queue.wait();
						}
						catch (InterruptedException ignored)
						{
							return;
						}
					}

					r = (Runnable) queue.removeFirst();
				}

				// If we don't catch RuntimeException, 
				// the pool could leak threads
				try {
					Task t = (Task) r;
					t.setRunning(id);
					r.run();
				}
				catch (RuntimeException e) {
					// might want to log something here. Just catch for now
				}
			}
		}
	}
}