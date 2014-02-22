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
package org.ivcode.guice.asynchronous.internal.binder;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.matcher.Matcher;

public class InterceptorElement {
	
	private final Matcher<? super Class<?>> classMatcher;
	private final Matcher<? super Method> methodMatcher;
	private final MethodInterceptor[] interceptors;
	
	public InterceptorElement(Matcher<? super Class<?>> classMatcher,
			Matcher<? super Method> methodMatcher,
			MethodInterceptor[] interceptors) {
		this.classMatcher = classMatcher;
		this.methodMatcher = methodMatcher;
		this.interceptors = interceptors;
	}

	public Matcher<? super Class<?>> getClassMatcher() {
		return classMatcher;
	}

	public Matcher<? super Method> getMethodMatcher() {
		return methodMatcher;
	}

	public MethodInterceptor[] getInterceptors() {
		return interceptors;
	}

	@Override
	public String toString() {
		return "InterceptorElement [classMatcher=" + classMatcher
				+ ", methodMatcher=" + methodMatcher + ", interceptors="
				+ Arrays.toString(interceptors) + "]";
	}
}
