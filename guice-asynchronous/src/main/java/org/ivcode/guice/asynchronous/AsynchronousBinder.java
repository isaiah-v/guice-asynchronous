package org.ivcode.guice.asynchronous;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public interface AsynchronousBinder extends Binder {
	public <T> AsynchronousBindingBuilder<T> bindAsynchronous(Class<T> clazz);
	public <T> AsynchronousBindingBuilder<T> bindAsynchronous(TypeLiteral<T> type);
	public <T> AsynchronousBindingBuilder<T> bindAsynchronous(Key<T> key);
	
	public AsynchronousPrivateBinder newPrivateBinder();
	public AsynchronousBinder withSource(Object source);
	public AsynchronousBinder skipSources(@SuppressWarnings("rawtypes") Class... classesToSkip);
}
