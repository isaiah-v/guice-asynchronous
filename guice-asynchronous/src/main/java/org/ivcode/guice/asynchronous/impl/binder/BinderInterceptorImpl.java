package org.ivcode.guice.asynchronous.impl.binder;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.Binder;
import com.google.inject.matcher.Matcher;

public class BinderInterceptorImpl extends BinderWrapper implements BinderInterceptor {
	
	private InterceptorManager interceptorManager;
	
	public BinderInterceptorImpl(InterceptorManager interceptorManager, Binder binder) {
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
	public BinderInterceptorImpl withSource(Object arg0) {
		return new BinderInterceptorImpl(interceptorManager, super.withSource(arg0));
	}
	
	@Override
	public BinderInterceptorImpl skipSources(@SuppressWarnings("rawtypes") Class... arg0) {
		return new BinderInterceptorImpl(interceptorManager, super.skipSources(arg0));
	}
}
