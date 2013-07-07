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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * The {@link AsyncInterceptor} works in connection with the {@link Callback}
 * interface.
 * 
 * @author isaiah
 * 
 */
public class AsyncInterceptor implements MethodInterceptor {

	private int thrownExceptions = 0;
	
	private FailFastHandler failFast = new FailFastHandler();
	private FailExceptionHandler failExceptions = new FailExceptionHandler();

	public Object invoke(MethodInvocation invocation) throws Throwable {
		try {
			failFast.handle(thrownExceptions, invocation);
			return invocation.proceed();
		} catch (final Throwable th) {
			thrownExceptions++;
			
			if(failExceptions.handle(th, invocation)>0)
				return null;
			else
				throw th;
		}
	}
}
