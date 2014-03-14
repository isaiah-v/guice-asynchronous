package org.ivcode.guice.asynchronous.context;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hamcrest.core.Is;
import org.junit.Test;

public class AsynchronousContextImplTest {

	@Test
	public void testShutdown() throws InterruptedException {
		testShutdown_BasicTest();
		testShutdown_JoinTest();
		testShutdown_AfterShutdownTest();
	}
	
	private void testShutdown_BasicTest() throws InterruptedException {
		AsynchronousContext context = new AsynchronousContextImpl();
		assertThat(context.isShutdown(), Is.is(false));
		
		context.shutdown();
		assertThat(context.isShutdown(), Is.is(true));
	}
	
	private void testShutdown_JoinTest() {
		AsynchronousContext context = new AsynchronousContextImpl();
		assertThat(context.isShutdown(), Is.is(false));
		
		
		//fail("Not Yet Impl");
	}
	
	private void testShutdown_AfterShutdownTest() throws InterruptedException {
		AsynchronousContext context = new AsynchronousContextImpl();
		context.shutdown();
		
		assertThat(context.isShutdown(), Is.is(true));
		assertThat(context.getTasksStarted(), Is.is(0));
		assertThat(context.getTasksCompleted(), Is.is(0));
		
		try {
			context.getExecutor().execute(new Runnable() {
				public void run() {}
			});
			
			fail("task not rejected");
		} catch (RejectedExecutionException e) {
		}
		
		assertThat(context.getTasksStarted(), Is.is(0));
		assertThat(context.getTasksCompleted(), Is.is(0));
	}

	@Test
	public void testGetTasksStarted() throws InterruptedException {
		testGetTasksStarted_BasicTest();
	}
	
	private void testGetTasksStarted_BasicTest() throws InterruptedException {
		final Runnable task = new Runnable() {
			public void run() {}
		};
		
		AsynchronousContext context = new AsynchronousContextImpl();
		assertThat(context.getTasksStarted(), Is.is(0));
		
		context.getExecutor().execute(task);
		context.getExecutor().execute(task);
		context.getExecutor().execute(task);
		
		assertThat(context.getTasksStarted(), Is.is(3));
		
		context.shutdown();
	}

	@Test
	public void testGetTasksCompleted() throws InterruptedException {
		testGetTasksCompleted_BasicTest();
		testGetTasksCompleted_CascadingExecutionTest();
	}
	
	private void testGetTasksCompleted_BasicTest() throws InterruptedException {
		final Runnable task = new Runnable() {
			public void run() {}
		};
		
		AsynchronousContext context = new AsynchronousContextImpl();
		assertThat(context.getTasksCompleted(), Is.is(0));
		
		context.getExecutor().execute(task);
		context.getExecutor().execute(task);
		context.getExecutor().execute(task);
		
		context.shutdown();
		assertThat(context.getTasksCompleted(), Is.is(3));
	}
	
	private void testGetTasksCompleted_CascadingExecutionTest() throws InterruptedException {
		final class Task implements Runnable {
			final AsynchronousContext context;
			final int iteration;
			
			Task(AsynchronousContext context, int iteration) {
				this.context = context;
				this.iteration = iteration;
			}
			
			public void run() {
				if(iteration<=1) return;
				context.getExecutor().execute(new Task(context, iteration-1));
			}
		}
		
		AsynchronousContext context = new AsynchronousContextImpl();
		assertThat(context.getTasksCompleted(), Is.is(0));
		
		context.getExecutor().execute(new Task(context, 3));
		
		context.shutdown();
		assertThat(context.getTasksCompleted(), Is.is(3));
	}

	@Test
	public void testGetExceptionsThrown() {
		//fail("Not yet implemented");
	}

	@Test
	public void testIsShutdown() throws InterruptedException {
		testIsShutdown_BasicTest();
		testIsShutdown_UnderlyingExecutorShutdown();
	}

	/**
	 * Asserts {@link AsynchronousContext#isShutdown()} is {@code true} after
	 * {@link AsynchronousContext#shutdown()} is called with no tasks submitted
	 */
	private void testIsShutdown_BasicTest() throws InterruptedException {
		AsynchronousContextImpl context = new AsynchronousContextImpl();
		assertThat(context.isShutdown(), Is.is(false));

		context.shutdown();
		assertThat(context.isShutdown(), Is.is(true));
	}

	/**
	 * Asserts {@link AsynchronousContext#isShutdown()} is {@code true} when the
	 * underlying executor is shutdown.
	 */
	private void testIsShutdown_UnderlyingExecutorShutdown() {
		ExecutorService executor = Executors.newCachedThreadPool();
		AsynchronousContextImpl context = new AsynchronousContextImpl(executor);
		assertThat(context.isShutdown(), Is.is(false));

		executor.shutdown();
		assertThat(context.isShutdown(), Is.is(true));
	}

	@Test
	public void testShutdownNow() throws Throwable {
		testShutdownNow_BasicTest(false);
		testShutdownNow_BasicTest(true);
		testShutdownNow_ShutdownBeforeTasksComplete(false);
		testShutdownNow_ShutdownBeforeTasksComplete(true);
		testShutdownNow_Interruption(false);
		testShutdownNow_Interruption(true);
	}

	/**
	 * Asserts that {@link AsynchronousContext#shutdownNow(boolean)} will
	 * shutdown the context
	 */
	private void testShutdownNow_BasicTest(final boolean isInterrupt) {
		final AsynchronousContextImpl context = new AsynchronousContextImpl();
		assertThat(context.isShutdown(), Is.is(false));

		context.shutdownNow(isInterrupt);
		assertThat(context.isShutdown(), Is.is(true));
	}

	/**
	 * Asserts that the context is shutdown before tasks are completed when
	 * calling {@link AsynchronousContext#shutdownNow(boolean)}
	 * @throws Throwable 
	 */
	private void testShutdownNow_ShutdownBeforeTasksComplete(final boolean isInterrupt) throws Throwable {
		final AsynchronousContextImpl context = new AsynchronousContextImpl();
		assertThat(context.isShutdown(), Is.is(false));
		
		inTaskTester(context, new Runnable() {
			public void run() {
				context.shutdownNow(isInterrupt);
				assertThat(context.isShutdown(), Is.is(true));
			}
		});
	}
	
	/**
	 * Asserts that a task is interrupted 
	 */
	private void testShutdownNow_Interruption(final boolean isInterrupt) throws Throwable {
		final AsynchronousContextImpl context = new AsynchronousContextImpl();
		
		inTaskTester(context, new Runnable() {
			public void run() {
				context.shutdownNow(isInterrupt);
				assertThat(Thread.currentThread().isInterrupted(), Is.is(isInterrupt));
			}
		});
	}
	
	private void inTaskTester(AsynchronousContext context, final Runnable task) throws Throwable {
		class ThrowableWrapper {
			Throwable throwable;
		}

		final ThrowableWrapper error = new ThrowableWrapper();
		final AtomicBoolean isComplete = new AtomicBoolean(false);

		context.getExecutor().execute(new Runnable() {

			public void run() {
				try {
					task.run();
				} catch (Throwable th) {
					error.throwable = th;
				}finally {
					synchronized (isComplete) {
						isComplete.set(true);
						isComplete.notifyAll();
					}
				}
			}
			
		});

		synchronized (isComplete) {
			while (!isComplete.get()) {
				isComplete.wait();
			}
		}

		if(error.throwable!=null) { throw error.throwable; }
	}
}
