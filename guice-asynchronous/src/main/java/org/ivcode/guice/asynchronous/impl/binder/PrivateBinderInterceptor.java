package org.ivcode.guice.asynchronous.impl.binder;

import com.google.inject.PrivateBinder;

public interface PrivateBinderInterceptor extends PrivateBinder, BinderInterceptor {
	public InterceptorManager getInterceptorManager();
	public PrivateBinderInterceptor withSource(Object arg0);
	public PrivateBinderInterceptor skipSources(@SuppressWarnings("rawtypes") Class... arg0);
}
