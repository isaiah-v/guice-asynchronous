package org.ivcode.guice.asynchronous.internal.binding;

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClassFactory;
import org.ivcode.guice.asynchronous.internal.binder.InterceptorElement;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerFactory;
import org.ivcode.guice.asynchronous.internal.proxy.factory.IndexMapFactory;

import com.google.inject.Binder;
import com.google.inject.Key;

public class BindingFactory {
	
	private final AsynchronousClassFactory asyncClassFactory;
	private final EnhancerFactory enhancerFactory;
	private final IndexMapFactory indexMapFactory;
	
	public BindingFactory(AsynchronousClassFactory asyncClassFactory, EnhancerFactory enhancerFactory, IndexMapFactory indexMapFactory) {
		this.asyncClassFactory = asyncClassFactory;
		this.enhancerFactory = enhancerFactory;
		this.indexMapFactory = indexMapFactory;
	}
	
	public <T> Binding createAsynchronousBinding(Binder binder, Key<T> key, Constructor<T> c, Collection<InterceptorElement> interceptors, ScopeBinding scopeBinding, Object source) {
		return new AsynchronousBinding<T>(binder, key, c, interceptors, scopeBinding, source, asyncClassFactory, enhancerFactory);
	}
	
	public Binding createFactoryBinding(Binder binder, Key<?> factoryKey, Key<?> asyncKey, Collection<InterceptorElement> interceptors, Object source) {
		return new FactoryBinding(asyncClassFactory, enhancerFactory, indexMapFactory, binder, factoryKey, asyncKey, interceptors, source);
	}
}
