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
package org.ivcode.guice.asynchronous.internal.utils;

import org.ivcode.guice.asynchronous.AsynchronousModule;

public class ClassPreloader {
	private ClassPreloader() {
	}
	
	public static void loadAsynchronousClasses() throws ClassNotFoundException {
		// pre-load classes used to run asynchronous tasks
		
		AsynchronousModule.class.getClassLoader().loadClass("org.ivcode.guice.asynchronous.internal.proxy.AsynchronusInterceptor");
		AsynchronousModule.class.getClassLoader().loadClass("org.ivcode.guice.asynchronous.internal.proxy.AsynchronusInterceptor$TaskExecutor");
		AsynchronousModule.class.getClassLoader().loadClass("org.ivcode.guice.asynchronous.internal.proxy.InterceptorStackCallback$InterceptedMethodInvocation");
		AsynchronousModule.class.getClassLoader().loadClass("org.ivcode.guice.asynchronous.context.AsynchronousContextImpl$Task");
		AsynchronousModule.class.getClassLoader().loadClass("org.ivcode.guice.asynchronous.internal.proxy.AsyncTaskException");
	}
}
