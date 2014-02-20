package org.ivcode.guice.asynchronous.internal.binder;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.ivcode.guice.asynchronous.AsynchronousBinder;
import org.ivcode.guice.asynchronous.AsynchronousBindingBuilder;
import org.ivcode.guice.asynchronous.AsynchronousPrivateBinder;
import org.ivcode.guice.asynchronous.internal.recorder.AsynchronousBindingBean;
import org.ivcode.guice.asynchronous.internal.recorder.AsynchronousBindingBuilderImpl;
import org.ivcode.guice.asynchronous.internal.recorder.AsynchronousBindingProcessor;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;

public class AsynchronousBinderManager {
	
	private final List<AsynchronousBindingBean<?>> asyncBindings = new LinkedList<AsynchronousBindingBean<?>>();
	private boolean isClosed;
	
	public AsynchronousBinder createAsynchronousBinder(Binder binder) {
		return createAsynchronousBinder(binder, this.<InterceptorElement>createCollection());
	}
	
	private AsynchronousBinder createAsynchronousBinder(Binder binder, Collection<InterceptorElement> interceptors) {
		return new MyAsynchronousBinder(binder, interceptors);
	}
	
	private AsynchronousPrivateBinder createAsynchronousPrivateBinder(PrivateBinder binder, Collection<InterceptorElement> interceptors) {
		return new MyAsynchronousPrivateBinder(binder, interceptors);
	}
	
	private <T> AsynchronousBindingBuilder<T> bindAsynchronous(Binder binder, Collection<InterceptorElement> interceptors, Class<T> clazz) {
		return bindAsynchronous(binder, interceptors, Key.get(clazz));
	}

	private <T> AsynchronousBindingBuilder<T> bindAsynchronous(Binder binder, Collection<InterceptorElement> interceptors, TypeLiteral<T> type) {
		return bindAsynchronous(binder, interceptors, Key.get(type));
	}

	private synchronized <T> AsynchronousBindingBuilder<T> bindAsynchronous(Binder binder, Collection<InterceptorElement> interceptors, Key<T> key) {
		if(this.isClosed) { throw new IllegalStateException(); }
		
		AsynchronousBindingBuilderImpl<T> abb = new AsynchronousBindingBuilderImpl<T>(binder, interceptors, key, null);
		
		asyncBindings.add(abb);
		
		return abb;
	}
	
	public void build(AsynchronousBindingProcessor bindingProcessor) {
		synchronized(this) {
			if(isClosed) { return; }
			isClosed = true;
		}
		
		bindingProcessor.process(asyncBindings);
	}
	
	private <T> Collection<T> createCollection() {
		return new LinkedList<T>();
	}
	
	private <T> Collection<T> createCollection(Collection<? extends T> c) {
		return new LinkedList<T>(c);
	}
	
	private class MyAsynchronousBinder extends BinderWrapper implements AsynchronousBinder {
		
		private final Collection<InterceptorElement> interceptors;

		MyAsynchronousBinder(Binder binder, Collection<InterceptorElement> interceptors) {
			super(binder);
			this.interceptors = interceptors;
		}
		
		@Override
		public AsynchronousPrivateBinder newPrivateBinder() {
			return createAsynchronousPrivateBinder(getBinder().newPrivateBinder(), createCollection(interceptors));
		}
		
		@Override
		public AsynchronousBinder withSource(Object source) {
			return createAsynchronousBinder(getBinder().withSource(source), interceptors);
		}
		
		@Override
		public AsynchronousBinder skipSources(@SuppressWarnings("rawtypes") Class... classesToSkip) {
			return createAsynchronousBinder(getBinder().skipSources(classesToSkip), interceptors);
		}

		public <T> AsynchronousBindingBuilder<T> bindAsynchronous(Class<T> clazz) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), interceptors ,clazz);
		}

		public <T> AsynchronousBindingBuilder<T> bindAsynchronous(TypeLiteral<T> type) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), interceptors, type);
		}

		public <T> AsynchronousBindingBuilder<T> bindAsynchronous(Key<T> key) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), interceptors, key);
		}
		
		@Override
		public void bindInterceptor(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, MethodInterceptor... interceptors) {
			super.bindInterceptor(classMatcher, methodMatcher, interceptors);
			this.interceptors.add(new InterceptorElement(classMatcher, methodMatcher, interceptors));
		}
	}
	
	private class MyAsynchronousPrivateBinder extends PrivateBinderWrapper implements AsynchronousPrivateBinder {
		
		private Collection<InterceptorElement> interceptors;
		
		MyAsynchronousPrivateBinder(PrivateBinder binder, Collection<InterceptorElement> interceptors) {
			super(binder);
			this.interceptors = interceptors;
		}
		
		@Override
		public AsynchronousPrivateBinder newPrivateBinder() {
			return createAsynchronousPrivateBinder(getBinder().newPrivateBinder(), createCollection(interceptors));
		}
		
		@Override
		public AsynchronousPrivateBinder withSource(Object source) {
			return createAsynchronousPrivateBinder(getBinder().withSource(source), interceptors);
		}
		
		@Override
		public AsynchronousPrivateBinder skipSources(@SuppressWarnings("rawtypes") Class... classesToSkip) {
			return createAsynchronousPrivateBinder(getBinder().skipSources(classesToSkip), interceptors);
		}

		public <T> AsynchronousBindingBuilder<T> bindAsynchronous(Class<T> clazz) {
			return AsynchronousBinderManager.this.bindAsynchronous(this, interceptors, clazz);
		}

		public <T> AsynchronousBindingBuilder<T> bindAsynchronous(TypeLiteral<T> type) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), interceptors, type);
		}

		public <T> AsynchronousBindingBuilder<T> bindAsynchronous(Key<T> key) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), interceptors, key);
		}
		
		@Override
		public void bindInterceptor(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, MethodInterceptor... interceptors) {
			super.bindInterceptor(classMatcher, methodMatcher, interceptors);
			this.interceptors.add(new InterceptorElement(classMatcher, methodMatcher, interceptors));
		}
	}
}
