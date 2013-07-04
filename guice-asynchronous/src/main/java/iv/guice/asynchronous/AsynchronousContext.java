package iv.guice.asynchronous;

import java.util.concurrent.Executor;

public interface AsynchronousContext {
	
	/**
	 * Returns the number of asynchronous tasks that have started 
	 * @return
	 * 		The number of asynchronous tasks that have started
	 */
	public int getTasksStarted();
	
	/**
	 * Returns the number of asynchronous tasks that have completed
	 * @return
	 * 		The number of asynchronous tasks that have completed
	 */
	public int getTasksCompleted();
	
	/**
	 * Returns the number of exceptions thrown while executing asynchronous tasks  
	 * @return
	 * 		The number of exceptions thrown while executing asynchronous tasks
	 */
	public int getExceptionsThrown();
	
	/**
	 * Returns the executor used to process asynchronous tasks
	 * @return
	 * 		The executor used to process asynchronous tasks
	 */
	public Executor getExecutor();
	
	/**
	 * Returns <code>true</code> if asynchronous service has been shutdown
	 * @return
	 * 		<code>true</code> if asynchronous service has been shutdown
	 */
	public boolean isShutdown();
}
