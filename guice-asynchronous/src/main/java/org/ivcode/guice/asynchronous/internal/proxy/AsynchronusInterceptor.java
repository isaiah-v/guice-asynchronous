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
import java.util.Arrays;
import java.util.concurrent.Executor;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class AsynchronusInterceptor implements MethodInterceptor {

    private final Executor executor;
    private final MethodInterceptor methodInterceptor;

    AsynchronusInterceptor(Executor executor, MethodInterceptor methodInterceptor) {
        this.executor = executor;
        this.methodInterceptor = methodInterceptor;
    }

    public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
        executor.execute(new TaskExecutor(obj, method, args, proxy));

        // all asynchronous methods return void
        return null;
    }
    
    @Override
	public String toString() {
		return "AsynchronusInterceptor [executor=" + executor
				+ ", methodInterceptor=" + methodInterceptor + "]";
	}
    
	private class TaskExecutor implements Runnable {
        
        final Object obj;
        final Method method;
        final Object[] args;
        final MethodProxy proxy;

        private TaskExecutor(Object obj, Method method, Object[] args, MethodProxy proxy) {
            this.obj = obj;
            this.method = method;
            this.args = args;
            this.proxy = proxy;
        }
        
        public void run() {
            try {
                // the wrapped method intercepter should invoke the method
                methodInterceptor.intercept(obj, method, args, proxy);
            } catch (final Throwable th) {
            	throw new AsyncTaskException(method, th);
            }
        }

		@Override
		public String toString() {
			return "TaskExecutor [obj=" + obj + ", method=" + method
					+ ", args=" + Arrays.toString(args) + ", proxy=" + proxy
					+ "]";
		}
    }
}
