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
package iv.guice.asynchronous.helpers.callbacks;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A {@link Future} and {@link Callback} implementation. This mechanism allows
 * us to pass a callback to an asynchronous task and poll result using a future
 * object.<br>
 * <br>
 * <b>Note:</b> {@link Future}s can be prone to a type of deadlock where all of
 * the available threads resources block on tasks that are still queued. It's
 * best if you use a cached thread pool if you use {@link Future}s.
 * 
 * @author Isaiah van der Elst
 * 
 * @param <T>
 *            The return type
 */
public class FutureCallback<T> implements Future<T>, Callback<T> {

    protected final Object lock = new Object();;

    private T result;
    private Throwable throwable;

    private boolean isDone = false;;

    public void onSuccess(T result) {
        // set the result and unblock waiting threads

        synchronized (lock) {
            if (isDone) throw new IllegalStateException();
            this.isDone = true;

            this.result = result;
            lock.notifyAll();
        }
    }

    public void onFail(Throwable th) {
        // set the exception and unblock waiting threads

        synchronized (lock) {
            if (isDone) throw new IllegalStateException();
            this.isDone = true;

            this.throwable = th;
            lock.notifyAll();
        }
    }

    /**
     * Attempts to cancel execution of this task. This attempt will fail if the
     * task has already completed, already been cancelled, or could not be
     * cancelled for some other reason. If successful, and this task has not
     * started when <code>cancel</code> is called, this task should never run.
     * If the task has already started, then the
     * <code>mayInterruptIfRunning parameter</code> determines whether the
     * thread executing this task should be interrupted in an attempt to stop
     * the task.
     * 
     * @param mayInterruptIfRunning
     *            <code>true</code> if the thread executing this task should be
     *            interrupted; otherwise, in-progress tasks are allowed to
     *            complete
     * @return <code>false</code> if the task could not be cancelled, typically
     *         because it has already completed normally; <code>true</code>
     *         otherwise
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        /*
         * Cancel not implemented. It is impossible to enforce a cancellation
         * within a task
         */
        return false;
    }

    /**
     * Returns <code>true</code> if this task was cancelled before it completed
     * normally.
     * 
     * @return <code>true</code> if this task was cancelled before it completed
     *         normally.
     */
    public boolean isCancelled() {
        /*
         * Cancel not implemented. It is impossible to enforce a cancellation
         * within a task
         */
        return false;
    }

    /**
     * Returns <code>true</code> if the task was cancelled and the
     * <code>mayInterruptIfRunning</code> value was set to true.
     * 
     * @return <code>true</code> if the task was cancelled and the
     *         <code>mayInterruptIfRunning</code> value was set to true.
     */
    public boolean isMayInterruptIfRunning() {
        /*
         * Cancel not implemented. It is impossible to enforce a cancellation
         * within a task
         */
        return false;
    }

    /**
     * Returns <code>true</code> if this task completed. Completion may be due
     * to normal termination, an exception, or cancellation -- in all of these
     * cases, this method will return true.
     * 
     * @return <code>true</code> if this task completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Waits if necessary for the computation to complete, and then retrieves
     * its result.
     * 
     * @return the computed result
     * @throws CancellationException
     *             if the computation was cancelled
     * @throws ExecutionException
     *             if the computation threw an exception
     * @throws InterruptedException
     *             if the current thread was interrupted while waiting
     */
    public T get() throws InterruptedException, ExecutionException {
        synchronized (lock) {
            while (!this.isDone())
                lock.wait();
        }

        if (isCancelled()) throw new CancellationException();
        if (this.throwable != null) throw new ExecutionException(throwable);

        return result;
    }

    /**
     * Waits if necessary for at most the given time for the computation to
     * complete, and then retrieves its result, if available.
     * 
     * @param timeout
     *            the maximum time to wait
     * @param unit
     *            the time unit of the timeout argument
     * @throws CancellationException
     *             if the computation was cancelled
     * @throws ExecutionException
     *             if the computation threw an exception
     * @throws InterruptedException
     *             if the current thread was interrupted while waiting
     * @throws TimeoutException
     *             if the wait timed out
     */
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        final long timeoutTime = System.currentTimeMillis() + unit.toMillis(timeout);
        synchronized (lock) {
            timeout = timeoutTime - System.currentTimeMillis();

            while (!this.isDone && timeout > 0)
                lock.wait(timeout);
        }

        if (!isDone()) throw new TimeoutException();
        if (isCancelled()) throw new CancellationException();

        if (this.throwable != null) throw new ExecutionException(throwable);

        return result;
    }
}
