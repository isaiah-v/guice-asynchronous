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

import java.util.ArrayList;
import java.util.Collection;

import net.sf.cglib.proxy.MethodProxy;

import org.ivcode.guice.asynchronous.GuiceAsynchronous;
import org.ivcode.guice.asynchronous.internal.proxy.AsyncTaskException;
import org.ivcode.guice.asynchronous.internal.proxy.AsynchronusInterceptor;
import org.ivcode.guice.asynchronous.internal.proxy.DirectInterceptor;
import org.ivcode.guice.asynchronous.internal.proxy.InterceptorStackCallback;

public class InternalClasses {
	private InternalClasses() {
	}
	
	public static void loadInternalClasses() throws ClassNotFoundException {
		getInternalClasses();
	}
	
	public static Collection<Class<?>> getInternalClasses() {
		
		return loadDeclaredClasses(new ArrayList<Class<?>>(),
				AsynchronusInterceptor.class,
				InterceptorStackCallback.class,
				GuiceAsynchronous.class,
				AsyncTaskException.class,
				InterceptorStackCallback.class,
				MethodProxy.class,
				DirectInterceptor.class);
		
	}
	
	private static <T extends Collection<Class<?>>>  T loadDeclaredClasses(T values, Class<?>... clazzes) {
		for(Class<?> clazz : clazzes) {
			loadDeclaredClasses(values, clazz);
		}
		
		return values;
	}
	
	private static void loadDeclaredClasses(Collection<Class<?>> values, Class<?> clazz) {
		values.add(clazz);
		
		for(Class<?> innerClazz : clazz.getDeclaredClasses()) {
			loadDeclaredClasses(values, innerClazz);
		}
	}
}