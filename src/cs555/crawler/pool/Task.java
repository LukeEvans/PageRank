package cs555.crawler.pool;

/**
 * 
 * @author levans
 * Task is an interface to be implemented by specific tasks. Eg send, receive...
 */
public interface Task extends Runnable{
	
	@Override
	public void run();

	public void setRunning(int i);
}
