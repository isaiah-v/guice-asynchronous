package org.ivcode.guice.asynchronous.impl.binder;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.PrivateBinder;
import com.google.inject.matcher.Matcher;

public class PrivateBinderInterceptorImpl extends PrivateBinderWrapper implements PrivateBinderInterceptor {

	private final InterceptorManager interceptorManager;
	
	PrivateBinderInterceptorImpl(InterceptorManager interceptorManager, PrivateBinder binder) {
		super(binder);
		this.interceptorManager = interceptorManager;
	}
	
	public InterceptorManager getInterceptorManager() {
		return interceptorManager;
	}
	
	@Override
	public void bindInterceptor(Matcher<? super Class<?>> arg0, Matcher<? super Method> arg1, MethodInterceptor... arg2) {
		super.bindInterceptor(arg0, arg1, arg2);
		interceptorManager.bindInterceptor(arg0, arg1, arg2);
	}
	
	@Override
	public PrivateBinderInterceptorImpl newPrivateBinder() {
		return new PrivateBinderInterceptorImpl(interceptorManager, super.newPrivateBinder());
	}
	
	@Override
	public PrivateBinderInterceptorImpl withSource(Object arg0) {
		return new PrivateBinderInterceptorImpl(interceptorManager, super.withSource(arg0));
	}
	
	@Override
	public PrivateBinderInterceptorImpl skipSources(@SuppressWarnings("rawtypes") Class... arg0) {
		return new PrivateBinderInterceptorImpl(interceptorManager, super.skipSources(arg0));
	}
}
