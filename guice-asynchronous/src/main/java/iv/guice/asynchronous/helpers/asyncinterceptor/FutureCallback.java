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
package iv.guice.asynchronous.helpers.asyncinterceptor;

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
            if (isDone) throw new IllegalStateException();
            this.isDone = true;

            this.result = result;
            lock.notifyAll();
        }
    }

    public void onFail(Throwable th) {
        synchronized (lock) {
            if (isDone) throw new IllegalStateException();
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
            while (!this.isDone())
                lock.wait();
        }

        if (this.throwable != null) throw new ExecutionException(throwable);

        return result;
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        final long timeoutTime = System.currentTimeMillis() + unit.toMillis(timeout);
        synchronized (lock) {
            timeout = timeoutTime - System.currentTimeMillis();

            while (!this.isDone && timeout > 0)
                lock.wait(timeout);
        }

        if (!isDone()) throw new TimeoutException();

        if (this.throwable != null) throw new ExecutionException(throwable);

        return result;
    }
}
