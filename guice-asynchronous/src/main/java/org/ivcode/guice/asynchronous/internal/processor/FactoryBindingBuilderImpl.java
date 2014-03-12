package org.ivcode.guice.asynchronous.internal.processor;

import java.util.Collection;

import org.ivcode.guice.asynchronous.AsynchronousFactoryBuilder;
import org.ivcode.guice.asynchronous.internal.binder.InterceptorElement;
import org.ivcode.guice.asynchronous.internal.binding.Binding;
import org.ivcode.guice.asynchronous.internal.binding.BindingBuilder;
import org.ivcode.guice.asynchronous.internal.binding.BindingFactory;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class FactoryBindingBuilderImpl implements AsynchronousFactoryBuilder, BindingBuilder {

	private final Binder binder;
	private final Collection<InterceptorElement> interceptors;
	
	private final Key<?> factoryKey;
	private final Object source;
	
	private Key<?> asyncKey;
	
	public FactoryBindingBuilderImpl(Binder binder,
			Collection<InterceptorElement> interceptors, Key<?> factoryKey,
			Object source) {
		this.binder = binder;
		this.interceptors = interceptors;
		this.factoryKey = factoryKey;
		this.source = source;
	}

	public void to(Class<?> asyc) {
		this.asyncKey = Key.get(asyc);
	}

	public void to(TypeLiteral<?> asyc) {
		this.asyncKey = Key.get(asyc);
	}

	public Binding build(BindingFactory bindingFactory) {
		return bindingFactory.createFactoryBinding(binder, factoryKey, asyncKey, interceptors, source);
	}
}
