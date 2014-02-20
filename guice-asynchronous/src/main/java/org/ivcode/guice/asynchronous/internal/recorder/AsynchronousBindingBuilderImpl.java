package org.ivcode.guice.asynchronous.internal.recorder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Collection;

import org.ivcode.guice.asynchronous.AsynchronousBindingBuilder;
import org.ivcode.guice.asynchronous.internal.binder.InterceptorElement;
import org.ivcode.guice.asynchronous.internal.bindings.AnnotationScopeBinding;
import org.ivcode.guice.asynchronous.internal.bindings.EagerSingletonScopeBinding;
import org.ivcode.guice.asynchronous.internal.bindings.InstanceScopeBinding;
import org.ivcode.guice.asynchronous.internal.bindings.ScopeBinding;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Scope;
import com.google.inject.binder.ScopedBindingBuilder;

public class AsynchronousBindingBuilderImpl<T> implements AsynchronousBindingBean<T>, ScopedBindingBuilder, AsynchronousBindingBuilder<T> {

	private final Binder binder;
	private final Collection<InterceptorElement> interceptors;
	
	private final Key<T> key;
	private final Object source;
	
	private ScopeBinding scopeBinding;
	private Constructor<? extends T> constructor;
	
	public AsynchronousBindingBuilderImpl(Binder binder, Collection<InterceptorElement> interceptors, Key<T> key, Object source) {
		this.binder = binder;
		this.interceptors = interceptors;
		this.key = key;
		this.source = source;
	}
	
	public void asEagerSingleton() {
		scopeBinding = new EagerSingletonScopeBinding();
	}

	public void in(Class<? extends Annotation> clazz) {
		scopeBinding = new AnnotationScopeBinding(clazz);
	}

	public void in(Scope scope) {
		scopeBinding = new InstanceScopeBinding(scope);
	}

	public ScopedBindingBuilder toConstructor(Constructor<? extends T> c) {
		constructor = c;
		return this;
	}

	public Key<T> getKey() {
		return key;
	}

	public Object getSource() {
		return source;
	}

	public ScopeBinding getScopeBinding() {
		return scopeBinding;
	}

	public Constructor<? extends T> getConstructor() {
		return constructor;
	}

	public Binder getBinder() {
		return binder;
	}

	public Collection<InterceptorElement> getInterceptors() {
		return interceptors;
	}
}
