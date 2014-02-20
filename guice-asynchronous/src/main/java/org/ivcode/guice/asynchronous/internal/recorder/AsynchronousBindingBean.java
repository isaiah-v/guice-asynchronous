package org.ivcode.guice.asynchronous.internal.recorder;

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.ivcode.guice.asynchronous.internal.binder.InterceptorElement;
import org.ivcode.guice.asynchronous.internal.bindings.ScopeBinding;

import com.google.inject.Binder;
import com.google.inject.Key;

public interface AsynchronousBindingBean<T> {
	public Key<T> getKey();
	public Object getSource();
	public ScopeBinding getScopeBinding();
	public Constructor<? extends T> getConstructor();
	public Binder getBinder();
	public Collection<InterceptorElement> getInterceptors();
}
