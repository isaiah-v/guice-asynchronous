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
package iv.guice.asynchronous.impl.cglib;

import iv.guice.asynchronous.impl.manager.ExceptionListener;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


class AsynchronusInterceptor implements MethodInterceptor {

	private final Executor executor;
	private final MethodInterceptor methodInterceptor;
	private final ExceptionListener exceptionListener;
	
	public AsynchronusInterceptor(Executor executor, ExceptionListener exceptionListener, MethodInterceptor methodInterceptor) {
		this.executor = executor;
		this.methodInterceptor = methodInterceptor;
		this.exceptionListener = exceptionListener;
	}
	
	public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
		executor.execute(new Runnable() {
			public void run() {
				try {
					// the wrapped method intercepter should invoke the method 
					methodInterceptor.intercept(obj, method, args, proxy);
				} catch (Throwable th) {					
					th.printStackTrace();
					exceptionListener.onException(method, th);
				}
			}
		});
		
		// all asynchronous methods return void
		return null;
	}
}