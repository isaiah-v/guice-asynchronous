package org.ivcode.guice.asynchronous.impl.binder;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.matcher.Matcher;

public interface InterceptorListener {
	public void onBindInterceptor(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, MethodInterceptor... interceptors);
}
