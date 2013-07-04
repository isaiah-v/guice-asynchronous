package iv.guice.asynchronous.enhancer;

import iv.guice.asynchronous.Asynchronizer;
import iv.guice.asynchronous.AsynchronousContext;
import iv.guice.asynchronous.Shutdownable;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AsynchronousManager extends ExceptionHandler implements AsynchronousContext, Executor, Shutdownable {

	private final ExecutorService executor;
	
	private volatile int tasksStarted;
	private volatile int tasksCompleted;
	private volatile boolean isShutdown;
	
	private volatile int exceptionsThrown;
	
	@Inject
	public AsynchronousManager(@Named(Asynchronizer.NAME_EXECUTOR_SERVICE) ExecutorService executor) {
		this.executor = executor;
	}
	
	private synchronized void startTask() {
		tasksStarted++;
	}

	private synchronized void endTask() {
		if(++tasksCompleted>=tasksStarted && isShutdown)
			this.notifyAll();
	}

	@Override
	public void shutdown() throws InterruptedException {
		synchronized(this) {
			this.isShutdown = true;
			while(tasksCompleted<tasksStarted) this.wait();
			executor.shutdown();
		}
		
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	}

	@Override
	public void execute(Runnable command) {
		startTask();
		executor.execute(new Task(command));
	}

	@Override
	public int getTasksStarted() {
		return tasksStarted;
	}

	@Override
	public int getTasksCompleted() {
		return tasksCompleted;
	}

	@Override
	public int getExceptionsThrown() {
		return exceptionsThrown;
	}

	@Override
	public Executor getExecutor() {
		return this;
	}

	@Override
	public boolean isShutdown() {
		return isShutdown || executor.isShutdown();
	}
	
	@Override
	protected void onException(Method method, Throwable th) {
		exceptionsThrown++;
	}
	
	private final class Task implements Runnable {

		private final Runnable task;
		
		public Task(Runnable task) {
			this.task = task;
		}
		
		@Override
		public void run() {
			try{
				task.run();
			} finally {
				endTask();
			}
		}
	}
}
