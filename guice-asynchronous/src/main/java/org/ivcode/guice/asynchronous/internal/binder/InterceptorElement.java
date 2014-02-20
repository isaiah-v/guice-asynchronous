package org.ivcode.guice.asynchronous.internal.binder;

import java.lang.reflect.Method;

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
}
