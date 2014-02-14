package org.ivcode.guice.asynchronous.impl.binder;

import com.google.inject.Binder;

public interface BinderInterceptor extends Binder {
	public InterceptorManager getInterceptorManager();
	public PrivateBinderInterceptor newPrivateBinder();
	public BinderInterceptor withSource(Object arg0);
	public BinderInterceptor skipSources(@SuppressWarnings("rawtypes") Class... arg0);
}
