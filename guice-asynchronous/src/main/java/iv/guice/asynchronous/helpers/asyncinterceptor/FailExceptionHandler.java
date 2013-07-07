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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

class FailExceptionHandler {	
	
	Integer handle(Throwable th, MethodInvocation invocation) throws Throwable {
		if(!invocation.getStaticPart().isAnnotationPresent(FailException.class))
			return 0;
		
		return fail(invocation.getMethod(), invocation.getArguments(), th);
	}

	private int fail(Method method, Object[] args, Throwable th) {
		Callback<?>[] callbacks = findCallbacks(method, args);

		return fail(callbacks, th);
	}

	private Callback<?>[] findCallbacks(Method method, Object[] args) {
		List<Callback<?>> callbacks = new ArrayList<Callback<?>>();

		Class<?>[] clazzes = method.getParameterTypes();
		for (int i = 0; i < clazzes.length; i++) {
			Class<?> clazz = clazzes[i];

			if (!Callback.class.isAssignableFrom(clazz))
				continue;

			Callback<?> callback = (Callback<?>) args[i];
			if (callback == null)
				continue;

			callbacks.add((Callback<?>) args[i]);
		}

		return callbacks.toArray(new Callback[callbacks.size()]);
	}

	private int fail(Callback<?>[] callbacks, Throwable th) {
		int i = 0;
		for (Callback<?> callback : callbacks)
			i += fail(callback, th);

		return i;
	}

	private int fail(Callback<?> callback, Throwable th) {
		if (callback == null)
			return 0;

		try {
			callback.onFail(th);
		} catch (Throwable thro) {
			thro.printStackTrace();
		}

		return 1;
	}

}
