package iv.guice.asynchronous.interceptor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureCallback<T> implements Future<T>, Callback<T> {

	private final Object lock = new Object();;
	
	private T result;
	private Throwable throwable;
	
	private boolean isDone = false;;
	private boolean isCancelled = false;
	private boolean isMayInterruptIfRunning = false;
	
	public void onSuccess(T result) {
		synchronized (lock) {
			if(isDone) throw new IllegalStateException();
			this.isDone = true;
			
			this.result = result;
			lock.notifyAll();
		}
	}
	
	
	public void onFail(Throwable th) {
		synchronized (lock) {
			if(isDone) throw new IllegalStateException();
			this.isDone = true;
			
			this.throwable = th;
			lock.notifyAll();
		}	
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		synchronized (lock) {
			this.isCancelled = true;
			this.isMayInterruptIfRunning = mayInterruptIfRunning;
			
			return !isDone();
		}
	}

	public boolean isCancelled() {
		return isCancelled;
	}
	
	public boolean isMayInterruptIfRunning() {
		return isMayInterruptIfRunning;
	}

	public boolean isDone() {
		return isDone;
	}

	public T get() throws InterruptedException, ExecutionException {
		synchronized (lock) {
			while(!this.isDone()) lock.wait();
		}
		
		if(this.throwable!=null)
			throw new ExecutionException(throwable);

		return result;
	}

	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		final long timeoutTime = System.currentTimeMillis() + unit.toMillis(timeout);
		synchronized (lock) {
			timeout = timeoutTime-System.currentTimeMillis();
			
			while(!this.isDone && timeout>0)
				lock.wait(timeout);
		}
		
		if(!isDone()) throw new TimeoutException();
		
		if(this.throwable!=null)
			throw new ExecutionException(throwable);

		return result;
	}
}
