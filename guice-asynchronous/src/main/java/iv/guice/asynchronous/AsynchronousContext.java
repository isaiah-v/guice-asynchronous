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
