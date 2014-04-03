package org.ivcode.guice.asynchronous;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public interface AsynchronousLinkedBindingBuilder<S> extends AsynchronousBindingBuilder<S> {
	<T extends S> AsynchronousBindingBuilder<T> to(Key<T> targetKey);
	<T extends S> AsynchronousBindingBuilder<T> to(TypeLiteral<T> implementation);
	<T extends S> AsynchronousBindingBuilder<T> to(Class<T> implementation);
}
