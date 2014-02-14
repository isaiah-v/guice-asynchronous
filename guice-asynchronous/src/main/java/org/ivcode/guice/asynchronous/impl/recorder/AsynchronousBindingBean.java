package org.ivcode.guice.asynchronous.impl.recorder;

import java.lang.reflect.Constructor;

import org.ivcode.guice.asynchronous.impl.bindings.ScopeBinding;

import com.google.inject.Key;

public interface AsynchronousBindingBean<T> {
	public Key<T> getKey();
	public Object getSource();
	public ScopeBinding getScopeBinding();
	public Constructor<? extends T> getConstructor();
}
