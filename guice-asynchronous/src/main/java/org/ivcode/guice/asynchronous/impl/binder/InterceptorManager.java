package org.ivcode.guice.asynchronous.impl.binder;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.matcher.Matcher;

public class InterceptorManager {
	
	private final Collection<InterceptorBean> interceptors = new LinkedList<InterceptorBean>();
	private final Collection<InterceptorListener> listeners = new LinkedList<InterceptorListener>();
	
	public void addInterceptorListener(InterceptorListener listener) {
		this.listeners.add(listener);
		for(InterceptorBean interceptor : interceptors) {
			listener.onBindInterceptor(
					interceptor.getClassMatcher(),
					interceptor.getMethodMatcher(),
					interceptor.getInterceptors());
		}
	}
	
	public Collection<InterceptorBean> getInterceptors() {
		return Collections.unmodifiableCollection(interceptors);
	}
	
	public void bindInterceptor(Matcher<? super Class<?>> arg0, Matcher<? super Method> arg1, MethodInterceptor... arg2) {	
		interceptors.add(new InterceptorBean(arg0, arg1, arg2));
		for(InterceptorListener listener : listeners) {
			listener.onBindInterceptor(arg0, arg1, arg2);
		}
	}
}
