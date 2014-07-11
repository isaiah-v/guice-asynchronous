package org.ivcode.guice.asynchronous.internal.binder;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.aopalliance.intercept.MethodInterceptor;
import org.ivcode.guice.asynchronous.AsynchronousAnnotatedBindingBuilder;
import org.ivcode.guice.asynchronous.AsynchronousBinder;
import org.ivcode.guice.asynchronous.AsynchronousFactoryBuilder;
import org.ivcode.guice.asynchronous.AsynchronousLinkedBindingBuilder;
import org.ivcode.guice.asynchronous.AsynchronousModule;
import org.ivcode.guice.asynchronous.AsynchronousPrivateBinder;
import org.ivcode.guice.asynchronous.internal.binding.BindingBuilder;
import org.ivcode.guice.asynchronous.internal.binding.BindingFactory;
import org.ivcode.guice.asynchronous.internal.processor.AsyncBuilderFactory;
import org.ivcode.guice.asynchronous.internal.processor.AsynchronousBindingBuilder;
import org.ivcode.guice.asynchronous.internal.processor.FactoryBindingBuilderImpl;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;

public class AsynchronousBinderManager {
	
	private final BindingFactory bindingFactory;
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final List<BindingBuilder> asyncBindings = Collections.synchronizedList(new LinkedList<BindingBuilder>());
	
	private AtomicBoolean isClosed = new AtomicBoolean(false);
	
	public AsynchronousBinderManager(BindingFactory bindingFactory) {
		this.bindingFactory = bindingFactory;
	}
	
	public AsynchronousBinder createAsynchronousBinder(Binder binder) {
		return createAsynchronousBinder(binder, this.<InterceptorElement>createCollection(), isClosed);
	}
	
	private AsynchronousBinder createAsynchronousBinder(Binder binder, Collection<InterceptorElement> interceptors, AtomicBoolean isClosed) {
		return new MyAsynchronousBinder(binder, interceptors, isClosed);
	}
	
	private AsynchronousPrivateBinder createAsynchronousPrivateBinder(PrivateBinder binder, Collection<InterceptorElement> interceptors, AtomicBoolean isClosed) {
		return new MyAsynchronousPrivateBinder(binder, interceptors, isClosed);
	}
	
	private <T> AsynchronousAnnotatedBindingBuilder<T> bindAsynchronous(Binder binder, AtomicBoolean isClosed, Collection<InterceptorElement> interceptors, Class<T> clazz) {
		lock.readLock().lock();
		try {
			if(isClosed.get()) { throw new IllegalStateException("closed binder"); }
			
			AsynchronousBindingBuilder abb = new AsynchronousBindingBuilder(binder, interceptors, null);
			AsynchronousAnnotatedBindingBuilder<T> value = AsyncBuilderFactory.createAsynchronousAnnotatedBindingBuilder(clazz, abb);
			
			asyncBindings.add(abb);
			
			return value;
		} finally {
			lock.readLock().unlock();
		}
	}

	private <T> AsynchronousAnnotatedBindingBuilder<T> bindAsynchronous(Binder binder, AtomicBoolean isClosed, Collection<InterceptorElement> interceptors, TypeLiteral<T> type) {
		lock.readLock().lock();
		try {
			if(isClosed.get()) { throw new IllegalStateException("closed binder"); }
			
			AsynchronousBindingBuilder abb = new AsynchronousBindingBuilder(binder, interceptors, null);
			AsynchronousAnnotatedBindingBuilder<T> value = AsyncBuilderFactory.createAsynchronousAnnotatedBindingBuilder(type, abb);
			
			asyncBindings.add(abb);
			
			return value;
		} finally {
			lock.readLock().unlock();
		}
	}

	private synchronized <T> AsynchronousLinkedBindingBuilder<T> bindAsynchronous(Binder binder, AtomicBoolean isClosed, Collection<InterceptorElement> interceptors, Key<T> key) {
		lock.readLock().lock();
		try {
			if(isClosed.get()) { throw new IllegalStateException("closed binder"); }
			
			AsynchronousBindingBuilder abb = new AsynchronousBindingBuilder(binder, interceptors, null);
			AsynchronousLinkedBindingBuilder<T> value = AsyncBuilderFactory.createAsynchronousAnnotatedBindingBuilder(key, abb);
			
			asyncBindings.add(abb);
			
			return value;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	private synchronized AsynchronousFactoryBuilder bindAsynchronousFactory(Binder binder, AtomicBoolean isClosed, Collection<InterceptorElement> interceptors, Key<?> key) {
		lock.readLock().lock();
		try {
			if(isClosed.get()) { throw new IllegalStateException("closed binder"); }
			
			FactoryBindingBuilderImpl abb = new FactoryBindingBuilderImpl(binder, interceptors, key, null);
			asyncBindings.add(abb);
			
			return abb;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public void build() {
		lock.writeLock().lock();
		try {
			isClosed.set(true);
			this.isClosed = new AtomicBoolean(false);
			
			for(BindingBuilder builder : asyncBindings) {
				builder.build(bindingFactory).bind();
			}
			
			asyncBindings.clear();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	private <T> Collection<T> createCollection() {
		return new LinkedList<T>();
	}
	
	private <T> Collection<T> createCollection(Collection<? extends T> c) {
		return new LinkedList<T>(c);
	}
	
	@Override
	public String toString() {
		return "AsynchronousBinderManager [asyncBindings=" + asyncBindings
				+ ", isClosed=" + isClosed + "]";
	}



	private class MyAsynchronousBinder extends BinderWrapper implements AsynchronousBinder {
		
		private final Collection<InterceptorElement> interceptors;
		private final AtomicBoolean isClosed;

		MyAsynchronousBinder(Binder binder, Collection<InterceptorElement> interceptors, AtomicBoolean isClosed) {
			super(binder);
			this.interceptors = interceptors;
			this.isClosed = isClosed;
		}
		
		@Override
		public AsynchronousPrivateBinder newPrivateBinder() {
			return createAsynchronousPrivateBinder(getBinder().newPrivateBinder(), createCollection(interceptors), isClosed);
		}
		
		@Override
		public AsynchronousBinder withSource(Object source) {
			return createAsynchronousBinder(getBinder().withSource(source), interceptors, isClosed);
		}
		
		@Override
		public AsynchronousBinder skipSources(@SuppressWarnings("rawtypes") Class... classesToSkip) {
			return createAsynchronousBinder(getBinder().skipSources(classesToSkip), interceptors, isClosed);
		}

		public <T> AsynchronousAnnotatedBindingBuilder<T> bindAsynchronous(Class<T> clazz) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), isClosed, interceptors ,clazz);
		}

		public <T> AsynchronousAnnotatedBindingBuilder<T> bindAsynchronous(TypeLiteral<T> type) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), isClosed, interceptors, type);
		}

		public <T> AsynchronousLinkedBindingBuilder<T> bindAsynchronous(Key<T> key) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), isClosed, interceptors, key);
		}
		
		@Override
		public void bindInterceptor(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, MethodInterceptor... interceptors) {
			super.bindInterceptor(classMatcher, methodMatcher, interceptors);
			this.interceptors.add(new InterceptorElement(classMatcher, methodMatcher, interceptors));
		}

		@Override
		public String toString() {
			return "MyAsynchronousBinder [interceptors=" + interceptors + "]";
		}

		public AsynchronousFactoryBuilder bindAsynchronousFactory(Class<?> factory) {
			return this.bindAsynchronousFactory(Key.get(factory));
		}

		public AsynchronousFactoryBuilder bindAsynchronousFactory(TypeLiteral<?> factory) {
			return this.bindAsynchronousFactory(Key.get(factory));
		}

		public AsynchronousFactoryBuilder bindAsynchronousFactory(Key<?> factory) {
			return AsynchronousBinderManager.this.bindAsynchronousFactory(super.getBinder(), isClosed, interceptors, factory);
		}

		public void install(AsynchronousModule module) {
			module.configure(this);
		}
		
		@Override
		public void install(Module module) {
			module.configure(this);
		}
	}
	
	private class MyAsynchronousPrivateBinder extends PrivateBinderWrapper implements AsynchronousPrivateBinder {
		
		private final Collection<InterceptorElement> interceptors;
		private final AtomicBoolean isClosed;
		
		MyAsynchronousPrivateBinder(PrivateBinder binder, Collection<InterceptorElement> interceptors, AtomicBoolean isClosed) {
			super(binder);
			this.interceptors = interceptors;
			this.isClosed = isClosed;
		}
		
		@Override
		public AsynchronousPrivateBinder newPrivateBinder() {
			return createAsynchronousPrivateBinder(getBinder().newPrivateBinder(), createCollection(interceptors), isClosed);
		}
		
		@Override
		public AsynchronousPrivateBinder withSource(Object source) {
			return createAsynchronousPrivateBinder(getBinder().withSource(source), interceptors, isClosed);
		}
		
		@Override
		public AsynchronousPrivateBinder skipSources(@SuppressWarnings("rawtypes") Class... classesToSkip) {
			return createAsynchronousPrivateBinder(getBinder().skipSources(classesToSkip), interceptors, isClosed);
		}

		public <T> AsynchronousAnnotatedBindingBuilder<T> bindAsynchronous(Class<T> clazz) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), isClosed, interceptors, clazz);
		}

		public <T> AsynchronousAnnotatedBindingBuilder<T> bindAsynchronous(TypeLiteral<T> type) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), isClosed, interceptors, type);
		}

		public <T> AsynchronousLinkedBindingBuilder<T> bindAsynchronous(Key<T> key) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), isClosed , interceptors, key);
		}
		
		@Override
		public void bindInterceptor(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, MethodInterceptor... interceptors) {
			super.bindInterceptor(classMatcher, methodMatcher, interceptors);
			this.interceptors.add(new InterceptorElement(classMatcher, methodMatcher, interceptors));
		}

		@Override
		public String toString() {
			return "MyAsynchronousPrivateBinder [interceptors=" + interceptors
					+ "]";
		}

		public AsynchronousFactoryBuilder bindAsynchronousFactory(Class<?> factory) {
			return this.bindAsynchronousFactory(Key.get(factory));
		}

		public AsynchronousFactoryBuilder bindAsynchronousFactory(TypeLiteral<?> factory) {
			return this.bindAsynchronousFactory(Key.get(factory));
		}

		public AsynchronousFactoryBuilder bindAsynchronousFactory(Key<?> factory) {
			return AsynchronousBinderManager.this.bindAsynchronousFactory(super.getBinder(), isClosed, interceptors, factory);
		}

		public void install(AsynchronousModule module) {
			module.configure(this);
		}
		
		@Override
		public void install(Module module) {
			module.configure(this);
		}
	}
}
