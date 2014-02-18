package org.ivcode.guice.asynchronous.impl.binder;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.matcher.Matcher;

public class InterceptorManager implements Cloneable {
	
	private final Collection<InterceptorBean> interceptors;
	
	private InterceptorManager(Collection<InterceptorBean> interceptors) {
		this.interceptors = new LinkedList<InterceptorBean>(interceptors);
	}
	
	public InterceptorManager() {
		this.interceptors = new LinkedList<InterceptorBean>();
	}
	
	public Collection<InterceptorBean> getInterceptors() {
		return Collections.unmodifiableCollection(interceptors);
	}
	
	public void bindInterceptor(Matcher<? super Class<?>> arg0, Matcher<? super Method> arg1, MethodInterceptor... arg2) {	
		interceptors.add(new InterceptorBean(arg0, arg1, arg2));
	}
	
	public InterceptorManager clone() {
		return new InterceptorManager(interceptors);
	}
}
