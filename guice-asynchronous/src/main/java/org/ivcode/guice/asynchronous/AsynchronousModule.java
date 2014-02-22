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
package org.ivcode.guice.asynchronous;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.ivcode.guice.asynchronous.context.AsynchronousContext;
import org.ivcode.guice.asynchronous.context.AsynchronousContextImpl;
import org.ivcode.guice.asynchronous.internal.binder.AsynchronousBinderManager;
import org.ivcode.guice.asynchronous.internal.processor.AsynchronousBindingProcessorFactory;
import org.ivcode.guice.asynchronous.internal.processor.AsynchronousBindingProcessorFactoryImpl;
import org.ivcode.guice.asynchronous.internal.utils.ClassPreloader;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.Message;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeListener;

public abstract class AsynchronousModule implements Module {

	private final AsynchronousBinderManager bindingManager;
	private final AsynchronousBindingProcessorFactory processorFactory;
	
	private final AsynchronousContext context;
	
	private AsynchronousBinder rootBinder;
	

	public AsynchronousModule() {
		this(new AsynchronousContextImpl());
	}

	/**
	 * Creates a new {@link AsynchronousModule}<br/>
	 * <br/>
	 * This constructor will result in the given context (a parent context)
	 * being used process the asynchronous tasks. The parent context will not be
	 * bound by this module
	 * 
	 * @param context
	 *            parent context
	 */
	public AsynchronousModule(AsynchronousContext context) {
		if(context==null || context.isShutdown() || context.getExecutor()==null) {
			throw new IllegalArgumentException("invalid context");
		}
		
		this.context = context;
		this.bindingManager = new AsynchronousBinderManager();
		this.processorFactory = new AsynchronousBindingProcessorFactoryImpl();
	}
	
	/**
	 * Configures a {@link AsynchronousBinder} via the exposed methods.
	 */
	protected abstract void configure() throws Exception;
	
	public final void configure(Binder binder) {
		configure(bindingManager.createAsynchronousBinder(binder));
	}
	
	private final void configure(AsynchronousBinder binder) {
		synchronized (this) {
			if (this.rootBinder != null) {
				throw new IllegalStateException();
			}
			init(binder);
		}

		try {
			ClassPreloader.loadAsynchronousClasses();
			
			configure();
			buildAsynchronousBindings();
		} catch (Throwable e) {
			binder.addError(e);
		} finally {
			destroy();
		}
	}
	
	private synchronized void init(AsynchronousBinder binder) {
		this.rootBinder = binder;
	}

	private synchronized void destroy() {
		this.rootBinder = null;
	}
	
	private void buildAsynchronousBindings() {
		bindingManager.build(processorFactory.createAsynchronousBindingProcessor(context.getExecutor()));
	}
	
	protected AsynchronousBinder binder() {
		return rootBinder;
	}
	
	public AsynchronousContext getContext() {
		return context;
	}
	
	protected void bindContext() {
		binder().bind(AsynchronousContext.class).toInstance(context);
	}
	
	protected void bindContext(Key<AsynchronousContext> key) {
		binder().bind(key).toInstance(context);
	}

	/**
	 * Creates an asynchronous binding
	 * @param clazz
	 * 		The asynchronous class
	 */
	protected <T> AsynchronousBindingBuilder<T> bindAsynchronous(Class<T> clazz) {
		return binder().bindAsynchronous(clazz);
	}

	/**
	 * Creates an asynchronous binding
	 * @param type
	 * 		The class type to asynchronize and bind 
	 */
	protected <T> AsynchronousBindingBuilder<T> bindAsynchronous(TypeLiteral<T> type) {
		return binder().bindAsynchronous(type);
	}
	
	/**
	 * Creates an asynchronous binding
	 * @param key
	 * 		A key representing the class to asynchronize and bind 
	 */
	protected <T> AsynchronousBindingBuilder<T> bindAsynchronous(Key<T> key) {
		return binder().bindAsynchronous(key);
	}

	/**
	 * @see Binder#bindScope(Class, Scope)
	 */
	protected void bindScope(Class<? extends Annotation> scopeAnnotation, Scope scope) {
		binder().bindScope(scopeAnnotation, scope);
	}

	/**
	 * @see Binder#bind(Key)
	 */
	protected <T> LinkedBindingBuilder<T> bind(Key<T> key) {
		return binder().bind(key);
	}

	/**
	 * @see Binder#bind(TypeLiteral)
	 */
	protected <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral) {
		return binder().bind(typeLiteral);
	}

	/**
	 * @see Binder#bind(Class)
	 */
	protected <T> AnnotatedBindingBuilder<T> bind(Class<T> clazz) {
		return binder().bind(clazz);
	}

	/**
	 * @see Binder#bindConstant()
	 */
	protected AnnotatedConstantBindingBuilder bindConstant() {
		return binder().bindConstant();
	}

	/**
	 * @see Binder#install(Module)
	 */
	protected void install(Module module) {
		binder().install(module);
	}

	/**
	 * @see Binder#addError(String, Object[])
	 */
	protected void addError(String message, Object... arguments) {
		binder().addError(message, arguments);
	}

	/**
	 * @see Binder#addError(Throwable)
	 */
	protected void addError(Throwable t) {
		binder().addError(t);
	}

	/**
	 * @see Binder#addError(Message)
	 */
	protected void addError(Message message) {
		binder().addError(message);
	}

	/**
	 * @see Binder#requestInjection(Object)
	 */
	protected void requestInjection(Object instance) {
		binder().requestInjection(instance);
	}

	/**
	 * @see Binder#requestStaticInjection(Class[])
	 */
	protected void requestStaticInjection(Class<?>... types) {
		binder().requestStaticInjection(types);
	}

	/**
	 * @see Binder#bindInterceptor(com.google.inject.matcher.Matcher,
	 *      com.google.inject.matcher.Matcher,
	 *      org.aopalliance.intercept.MethodInterceptor[])
	 */
	protected void bindInterceptor(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, org.aopalliance.intercept.MethodInterceptor... interceptors) {
		binder().bindInterceptor(classMatcher, methodMatcher, interceptors);
	}

	/**
	 * Adds a dependency from this module to {@code key}. When the injector is
	 * created, Guice will report an error if {@code key} cannot be injected.
	 * Note that this requirement may be satisfied by implicit binding, such as
	 * a public no-arguments constructor.
	 */
	protected void requireBinding(Key<?> key) {
		binder().getProvider(key);
	}

	/**
	 * Adds a dependency from this module to {@code type}. When the injector is
	 * created, Guice will report an error if {@code type} cannot be injected.
	 * Note that this requirement may be satisfied by implicit binding, such as
	 * a public no-arguments constructor.
	 */
	protected void requireBinding(Class<?> type) {
		binder().getProvider(type);
	}

	/**
	 * @see Binder#getProvider(Key)
	 */
	protected <T> Provider<T> getProvider(Key<T> key) {
		return binder().getProvider(key);
	}

	/**
	 * @see Binder#getProvider(Class)
	 */
	protected <T> Provider<T> getProvider(Class<T> type) {
		return binder().getProvider(type);
	}

	/**
	 * @see Binder#convertToTypes
	 */
	protected void convertToTypes(Matcher<? super TypeLiteral<?>> typeMatcher, TypeConverter converter) {
		binder().convertToTypes(typeMatcher, converter);
	}

	/**
	 * @see Binder#currentStage()
	 */
	protected Stage currentStage() {
		return binder().currentStage();
	}

	/**
	 * @see Binder#getMembersInjector(Class)
	 */
	protected <T> MembersInjector<T> getMembersInjector(Class<T> type) {
		return binder().getMembersInjector(type);
	}

	/**
	 * @see Binder#getMembersInjector(TypeLiteral)
	 */
	protected <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> type) {
		return binder().getMembersInjector(type);
	}

	/**
	 * @see Binder#bindListener(com.google.inject.matcher.Matcher,
	 *      com.google.inject.spi.TypeListener)
	 */
	protected void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener) {
		binder().bindListener(typeMatcher, listener);
	}
}
