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
package org.ivcode.guice.asynchronous.internal.proxy;

import java.lang.reflect.Method;

public class AsyncTaskException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final Method method;
	
	public AsyncTaskException(Method method, Throwable cause) {
		super("Uncaught Exception : Method="+method.getName(), cause);
		this.method = method;
	}
	
	public Method getMethod() {
		return method;
	}
}
