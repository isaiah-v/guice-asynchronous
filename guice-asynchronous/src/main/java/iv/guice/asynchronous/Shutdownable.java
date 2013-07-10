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
package iv.guice.asynchronous;

/**
 * Used to shutdown the asynchronous service.
 * 
 * @author Isaiah van der Elst
 */
public interface Shutdownable {

    /**
     * Cleanly shuts down the asynchronous service and waits for all tasks to
     * complete. Even after this method is called, asynchronous tasks can be
     * executed. When there are zero asynchronous tasks executing, the
     * underlying executor service is shutdown and any further submissions will
     * result in an exception.<br>
     * <br>
     * This method will unblock after the service has been completely shutdown
     * or the thread is interrupted.
     * 
     * @throws InterruptedException
     *             if the thread is interrupted while waiting
     */
    public void shutdown() throws InterruptedException;

    /**
     * Forcefully shuts down the asynchronous service. The underlying executor
     * service service is immediately shutdown and any further submissions will
     * result in an exception.<br>
     */
    public void shutdownNow();
}
