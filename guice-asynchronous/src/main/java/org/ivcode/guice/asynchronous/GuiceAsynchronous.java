/**
 * Copyright (C) 2013 Isaiah van der Elst (isaiah.vanderelst@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ivcode.guice.asynchronous;


import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.ivcode.guice.asynchronous.internal.modules.WrapperModule;
import org.ivcode.guice.asynchronous.internal.proxy.AsyncTaskException;
import org.ivcode.guice.asynchronous.internal.utils.GuiceAsyncUtils;

import com.google.inject.Module;

public class GuiceAsynchronous {

	private final MyExecutor myExecutor;
	
    private final AtomicInteger runningTasks = new AtomicInteger(0);
    private volatile boolean isShutdown;

    private volatile int exceptionsThrown;

    public GuiceAsynchronous() {
    	this(GuiceAsyncUtils.createDefaultExecutor());
    }
    
    public GuiceAsynchronous(ExecutorService executor) {
    	if(executor==null || executor.isShutdown()) {
    		throw new IllegalArgumentException("invalid executor");
    	}
    	
        this.myExecutor = new MyExecutor(executor);
    }

    private void startTask() {
        runningTasks.incrementAndGet();
    }

    private void endTask() {
    	if(runningTasks.decrementAndGet()<=0) {
    		synchronized (this) { this.notifyAll(); }
    	}
    }
    
    private synchronized void onException(Method method, Throwable th) {
        exceptionsThrown++;
    }

    public void shutdown() throws InterruptedException {
        synchronized (this) {
            while (runningTasks.get()>0) {
                this.wait();
            }
            
            if (this.isShutdown) return;
            this.isShutdown = true;
            myExecutor.executor.shutdown();
        }

        myExecutor.executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public int getRunningCount() {
        return runningTasks.get();
    }

    public int getExceptionsThrown() {
        return exceptionsThrown;
    }

    public Executor getExecutor() {
        return myExecutor;
    }

    public boolean isShutdown() {
        return isShutdown || myExecutor.executor.isShutdown();
    }
    
    public void shutdownNow(boolean isInterupt) {
        synchronized (this) {
            if (this.isShutdown) return;

            this.isShutdown = true;
            
            if(isInterupt) {
            	this.myExecutor.executor.shutdownNow();
            } else {
            	this.myExecutor.executor.shutdown();
            }
        }
    }
    
    private final class MyExecutor implements Executor {

    	private final ExecutorService executor;
    	
    	public MyExecutor(ExecutorService executor) {
    		this.executor = executor;
		}
    	
		public void execute(Runnable command) {
			try {
		    	startTask();
		    	executor.execute(new Task(command));
	    	} catch (Throwable th) {
	    		endTask();
	    	}
		}
    	
    }

	private final class Task implements Runnable {

        private final Runnable task;

        private Task(Runnable task) {
        	this.task = task;
        }

        public void run() {
            try {
                task.run();
            } catch (AsyncTaskException e) {
            	onException(e.getMethod(), e.getCause());
            } catch (Throwable th) {
                onException(null, th);
            } finally {
                endTask();
            }
        }

		@Override
		public String toString() {
			return "Task [task=" + task + "]";
		}
    }

	public Module createModule(AsynchronousModule... asyncModules) {
		return new WrapperModule(this, asyncModules);
	}
}
