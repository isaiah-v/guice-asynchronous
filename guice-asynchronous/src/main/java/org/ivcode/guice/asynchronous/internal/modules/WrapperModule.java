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
package org.ivcode.guice.asynchronous.internal.modules;

import org.ivcode.guice.asynchronous.AsynchronousBinder;
import org.ivcode.guice.asynchronous.AsynchronousModule;
import org.ivcode.guice.asynchronous.GuiceAsynchronous;
import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClassFactory;
import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClassFactoryImpl;
import org.ivcode.guice.asynchronous.internal.binder.AsynchronousBinderManager;
import org.ivcode.guice.asynchronous.internal.binding.BindingFactory;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerFactory;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerFactoryImpl;
import org.ivcode.guice.asynchronous.internal.proxy.factory.IndexMapFactory;
import org.ivcode.guice.asynchronous.internal.utils.InternalClasses;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * The {@link WrapperModule} wraps Guice's {@link Binder} a with an
 * {@link AsynchronousBinder} which performs the magic that enables asynchronous
 * bindings.
 * 
 * @author Isaiah van der Elst
 */
public class WrapperModule implements Module {

	private final AsynchronousBinderManager bindingManager;
	
	private final AsynchronousModule[] asyncModules;

	/**
	 * Creates a new {@link WrapperModule}<br/>
	 * <br/>
	 * This constructor will result in the given context (a parent context)
	 * being used process the asynchronous tasks. The parent context will not be
	 * bound by this module
	 * 
	 * @param context
	 *            parent context
	 */
	public WrapperModule(GuiceAsynchronous context, AsynchronousModule...asyncModules) {
		if(context==null || context.isShutdown() || context.getExecutor()==null) {
			throw new IllegalArgumentException("invalid context");
		}
		this.asyncModules = asyncModules;
		
		EnhancerFactory enhancerFactory = new EnhancerFactoryImpl(context.getExecutor());
		AsynchronousClassFactory asyncClassFactory = new AsynchronousClassFactoryImpl();
		IndexMapFactory indexMapFactory = new IndexMapFactory();
		
		BindingFactory bindingFactory = new BindingFactory(asyncClassFactory, enhancerFactory, indexMapFactory);
		
		this.bindingManager = new AsynchronousBinderManager(bindingFactory);
	}
	
	public synchronized final void configure(Binder binder) {
		final AsynchronousBinder asyncBinder = bindingManager.createAsynchronousBinder(binder);
		
		configure(asyncBinder);
		buildAsynchronousBindings();
	}
	
	private synchronized final void configure(AsynchronousBinder binder) {
		try {
			InternalClasses.loadInternalClasses();
			installAsyncModules(binder);
		} catch (Throwable e) {
			binder.addError(e);
		}
	}
	
	
	protected void installAsyncModules(AsynchronousBinder binder) {
		for(AsynchronousModule asyncModule : asyncModules) {
			binder.install(asyncModule);
		}
	}
	
	private void buildAsynchronousBindings() {
		bindingManager.build();
	}
	
}
