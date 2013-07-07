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

import java.lang.reflect.Constructor;

import org.aopalliance.intercept.MethodInvocation;

class FailFastHandler {

	void handle(Integer thrownExceptions, MethodInvocation invocation) throws Throwable {
		FailFast failFast = invocation.getStaticPart().getAnnotation(FailFast.class);
		if(failFast==null) return;
		
		if (thrownExceptions > 0)
			throw createException(failFast.exception(), failFast.message());
	}

	private Throwable createException(Class<? extends Throwable> clazz, String msg) throws Throwable {
		Constructor<? extends Throwable> c = clazz.getConstructor(String.class);
		return c.newInstance(msg);
	}
}
