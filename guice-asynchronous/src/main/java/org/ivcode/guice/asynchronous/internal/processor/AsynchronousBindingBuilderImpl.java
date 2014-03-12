/**
 * Copyright (C) 2013 Isaiah van der Elst (isaiah.vanderelst@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ivcode.guice.asynchronous.internal.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Collection;

import org.ivcode.guice.asynchronous.AsynchronousBuilder;
import org.ivcode.guice.asynchronous.internal.binder.InterceptorElement;
import org.ivcode.guice.asynchronous.internal.binding.AnnotationScopeBinding;
import org.ivcode.guice.asynchronous.internal.binding.Binding;
import org.ivcode.guice.asynchronous.internal.binding.BindingBuilder;
import org.ivcode.guice.asynchronous.internal.binding.BindingFactory;
import org.ivcode.guice.asynchronous.internal.binding.EagerSingletonScopeBinding;
import org.ivcode.guice.asynchronous.internal.binding.InstanceScopeBinding;
import org.ivcode.guice.asynchronous.internal.binding.ScopeBinding;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Scope;
import com.google.inject.binder.ScopedBindingBuilder;

public class AsynchronousBindingBuilderImpl<T> implements ScopedBindingBuilder, AsynchronousBuilder<T>, BindingBuilder {
	
	private final Binder binder;
	private final Collection<InterceptorElement> interceptors;
	
	private final Key<T> key;
	private final Object source;
	
	private ScopeBinding scopeBinding;
	private Constructor<T> constructor;
	
	private boolean isBuilt = false;
	
	public AsynchronousBindingBuilderImpl(Binder binder, Collection<InterceptorElement> interceptors, Key<T> key, Object source) {
		this.binder = binder;
		this.interceptors = interceptors;
		this.key = key;
		this.source = source;
	}
	
	public synchronized void asEagerSingleton() {
		if(isBuilt) { throw new IllegalStateException("builder already built"); }
		scopeBinding = new EagerSingletonScopeBinding();
	}

	public synchronized void in(Class<? extends Annotation> clazz) {
		if(isBuilt) { throw new IllegalStateException("builder already built"); }
		scopeBinding = new AnnotationScopeBinding(clazz);
	}

	public synchronized void in(Scope scope) {
		scopeBinding = new InstanceScopeBinding(scope);
	}

	public synchronized ScopedBindingBuilder withConstructor(Constructor<T> c) {
		constructor = c;
		return this;
	}
	
	public Binding build(BindingFactory factory) {
		return factory.createAsynchronousBinding(binder, key, constructor, interceptors, scopeBinding, source);
	}

	@Override
	public String toString() {
		return "AsynchronousBindingBuilderImpl [binder=" + binder
				+ ", interceptors=" + interceptors + ", key=" + key
				+ ", source=" + source + ", scopeBinding=" + scopeBinding
				+ ", constructor=" + constructor + "]";
	}
}
