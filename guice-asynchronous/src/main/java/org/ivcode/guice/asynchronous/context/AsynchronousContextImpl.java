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
package org.ivcode.guice.asynchronous.context;


import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.ivcode.guice.asynchronous.internal.proxy.AsyncTaskException;
import org.ivcode.guice.asynchronous.internal.utils.GuiceAsyncUtils;

public class AsynchronousContextImpl implements AsynchronousContext, Executor {

    private final ExecutorService executor;

    private volatile int tasksStarted;
    private volatile int tasksCompleted;
    private volatile boolean isShutdown;

    private volatile int exceptionsThrown;

    public AsynchronousContextImpl() {
    	this(GuiceAsyncUtils.createDefaultExecutor());
    }
    
    public AsynchronousContextImpl(ExecutorService executor) {
    	if(executor==null || executor.isShutdown()) {
    		throw new IllegalArgumentException("invalid executor");
    	}
    	
        this.executor = executor;
    }

    private synchronized void startTask() {
        tasksStarted++;
    }

    private synchronized void endTask() {
        if (++tasksCompleted >= tasksStarted && isShutdown) this.notifyAll();
    }
    
    private synchronized void onException(Method method, Throwable th) {
        exceptionsThrown++;
    }

    public void shutdown() throws InterruptedException {
        synchronized (this) {
            if (this.isShutdown) return;

            this.isShutdown = true;
            while (tasksCompleted < tasksStarted)
                this.wait();

            executor.shutdown();
        }

        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public void execute(Runnable command) {
        executor.execute(new Task(command));
    }

    public int getTasksStarted() {
        return tasksStarted;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

    public int getExceptionsThrown() {
        return exceptionsThrown;
    }

    public Executor getExecutor() {
        return this;
    }

    public boolean isShutdown() {
        return isShutdown || executor.isShutdown();
    }
    
    public void shutdownNow() {
        synchronized (this) {
            if (this.isShutdown) return;

            this.isShutdown = true;
            this.executor.shutdownNow();
        }
    }

    public final class Task implements Runnable {

        private final Runnable task;

        public Task(Runnable task) {
        	startTask();
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
    }
}
