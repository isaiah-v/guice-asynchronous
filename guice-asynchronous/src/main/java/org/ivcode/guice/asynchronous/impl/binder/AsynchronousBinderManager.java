package org.ivcode.guice.asynchronous.impl.binder;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.ivcode.guice.asynchronous.AsynchronousBinder;
import org.ivcode.guice.asynchronous.AsynchronousBindingBuilder;
import org.ivcode.guice.asynchronous.AsynchronousPrivateBinder;
import org.ivcode.guice.asynchronous.impl.recorder.AsynchronousBindingBean;
import org.ivcode.guice.asynchronous.impl.recorder.AsynchronousBindingBuilderImpl;
import org.ivcode.guice.asynchronous.impl.recorder.AsynchronousBindingProcessor;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;

public class AsynchronousBinderManager {
	
	private final AsynchronousBindingProcessor bindingProcessor;
	
	private final List<AsynchronousBindingBean<?>> asyncBindings = new LinkedList<AsynchronousBindingBean<?>>();
	private boolean isClosed;
	
	public AsynchronousBinderManager(AsynchronousBindingProcessor bindingProcessor) {
		this.bindingProcessor = bindingProcessor;
	}
	
	public AsynchronousBinder createAsynchronousBinder(Binder binder) {
		return createAsynchronousBinder(binder, new InterceptorManager());
	}
	
	private AsynchronousBinder createAsynchronousBinder(Binder binder, InterceptorManager interceptorManager) {
		return new MyAsynchronousBinder(binder, new InterceptorManager());
	}
	
	private AsynchronousPrivateBinder createAsynchronousPrivateBinder(PrivateBinder binder, InterceptorManager interceptorManager) {
		return new MyAsynchronousPrivateBinder(binder, interceptorManager);
	}
	
	private <T> AsynchronousBindingBuilder<T> bindAsynchronous(Binder binder, InterceptorManager interceptorManager, Class<T> clazz) {
		return bindAsynchronous(binder, interceptorManager, Key.get(clazz));
	}

	private <T> AsynchronousBindingBuilder<T> bindAsynchronous(Binder binder, InterceptorManager interceptorManager, TypeLiteral<T> type) {
		return bindAsynchronous(binder, interceptorManager, Key.get(type));
	}

	private synchronized <T> AsynchronousBindingBuilder<T> bindAsynchronous(Binder binder, InterceptorManager interceptorManager, Key<T> key) {
		if(this.isClosed) { throw new IllegalStateException(); }
		
		AsynchronousBindingBuilderImpl<T> abb = new AsynchronousBindingBuilderImpl<T>(binder, interceptorManager, key, null);
		
		asyncBindings.add(abb);
		
		return abb;
	}
	
	public void build() {
		synchronized(this) {
			if(isClosed) { return; }
			isClosed = true;
		}
		
		bindingProcessor.process(asyncBindings);
	}
	
	private class MyAsynchronousBinder extends BinderWrapper implements AsynchronousBinder {
		
		private InterceptorManager interceptorManager;

		MyAsynchronousBinder(Binder binder, InterceptorManager interceptorManager) {
			super(binder);
			this.interceptorManager = interceptorManager;
		}
		
		@Override
		public AsynchronousPrivateBinder newPrivateBinder() {
			return createAsynchronousPrivateBinder(getBinder().newPrivateBinder(), interceptorManager.clone());
		}
		
		@Override
		public AsynchronousBinder withSource(Object source) {
			return createAsynchronousBinder(getBinder().withSource(source), interceptorManager);
		}
		
		@Override
		public AsynchronousBinder skipSources(@SuppressWarnings("rawtypes") Class... classesToSkip) {
			return createAsynchronousBinder(getBinder().skipSources(classesToSkip), interceptorManager);
		}

		public <T> AsynchronousBindingBuilder<T> bindAsynchronous(Class<T> clazz) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), interceptorManager ,clazz);
		}

		public <T> AsynchronousBindingBuilder<T> bindAsynchronous(TypeLiteral<T> type) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), interceptorManager, type);
		}

		public <T> AsynchronousBindingBuilder<T> bindAsynchronous(Key<T> key) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), interceptorManager, key);
		}
		
		@Override
		public void bindInterceptor(Matcher<? super Class<?>> arg0, Matcher<? super Method> arg1, MethodInterceptor... arg2) {
			super.bindInterceptor(arg0, arg1, arg2);
			this.interceptorManager.bindInterceptor(arg0, arg1, arg2);
		}
	}
	
	private class MyAsynchronousPrivateBinder extends PrivateBinderWrapper implements AsynchronousPrivateBinder {
		
		private InterceptorManager interceptorManager;
		
		MyAsynchronousPrivateBinder(PrivateBinder binder, InterceptorManager interceptorManager) {
			super(binder);
			this.interceptorManager = interceptorManager;
		}
		
		@Override
		public AsynchronousPrivateBinder newPrivateBinder() {
			return createAsynchronousPrivateBinder(getBinder().newPrivateBinder(), interceptorManager.clone());
		}
		
		@Override
		public AsynchronousPrivateBinder withSource(Object source) {
			return createAsynchronousPrivateBinder(getBinder().withSource(source), interceptorManager);
		}
		
		@Override
		public AsynchronousPrivateBinder skipSources(@SuppressWarnings("rawtypes") Class... classesToSkip) {
			return createAsynchronousPrivateBinder(getBinder().skipSources(classesToSkip), interceptorManager);
		}

		public <T> AsynchronousBindingBuilder<T> bindAsynchronous(Class<T> clazz) {
			return AsynchronousBinderManager.this.bindAsynchronous(this, interceptorManager, clazz);
		}

		public <T> AsynchronousBindingBuilder<T> bindAsynchronous(TypeLiteral<T> type) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), interceptorManager, type);
		}

		public <T> AsynchronousBindingBuilder<T> bindAsynchronous(Key<T> key) {
			return AsynchronousBinderManager.this.bindAsynchronous(getBinder(), interceptorManager, key);
		}
		
		@Override
		public void bindInterceptor(Matcher<? super Class<?>> arg0, Matcher<? super Method> arg1, MethodInterceptor... arg2) {
			super.bindInterceptor(arg0, arg1, arg2);
			this.interceptorManager.bindInterceptor(arg0, arg1, arg2);
		}
	}
}
