package org.ivcode.guice.asynchronous;

import java.lang.reflect.Constructor;

import com.google.inject.binder.ScopedBindingBuilder;

public interface AsynchronousBindingBuilder<T> extends ScopedBindingBuilder {
	public ScopedBindingBuilder toConstructor(Constructor<? extends T> c);
}
