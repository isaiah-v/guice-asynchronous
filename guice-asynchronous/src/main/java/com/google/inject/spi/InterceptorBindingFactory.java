package com.google.inject.spi;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.matcher.Matcher;

public class InterceptorBindingFactory {
	private InterceptorBindingFactory() {
	}
	
	public static InterceptorBinding createInterceptorBinding(Object source, Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, MethodInterceptor... interceptors) {
		return new InterceptorBinding(source, classMatcher, methodMatcher, interceptors);
	}
}
