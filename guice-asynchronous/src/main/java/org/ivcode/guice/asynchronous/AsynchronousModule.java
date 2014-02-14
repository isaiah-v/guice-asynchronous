package org.ivcode.guice.asynchronous;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.ivcode.guice.asynchronous.impl.binder.BinderInterceptor;
import org.ivcode.guice.asynchronous.impl.binder.InterceptorManager;
import org.ivcode.guice.asynchronous.impl.binder.BinderInterceptorImpl;
import org.ivcode.guice.asynchronous.impl.bindingclass.BindingClassFactory;
import org.ivcode.guice.asynchronous.impl.bindingclass.BindingClassFactoryImpl;
import org.ivcode.guice.asynchronous.impl.bindings.AsynchronousBinding;
import org.ivcode.guice.asynchronous.impl.cglib.EnhancerFactory;
import org.ivcode.guice.asynchronous.impl.cglib.EnhancerFactoryImpl;
import org.ivcode.guice.asynchronous.impl.manager.AsynchronousManager;
import org.ivcode.guice.asynchronous.impl.manager.ExceptionListener;
import org.ivcode.guice.asynchronous.impl.recorder.AsynchronousBindingBean;
import org.ivcode.guice.asynchronous.impl.recorder.AsynchronousBindingBuilderImpl;
import org.ivcode.guice.asynchronous.impl.utils.GuiceAsyncUtils;
import org.ivcode.guice.asynchronous.impl.utils.MyThreadFactory;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public abstract class AsynchronousModule implements Module {
	
	/** The thread name prefix for the internal thread factory */
	private static final String THREAD_NAME_PREFIX = "guice-asynchronous";

	private final Annotation annotation;
	
	private final AsynchronousContext context;
	private final ExceptionListener exceptionListener;
	
	private BinderInterceptor binder;
	private List<AsynchronousBindingBean<?>> asyncBindings;
	
	public AsynchronousModule() {
		this(null, null);
	}
	
	public AsynchronousModule(ExecutorService executor) {
		this(executor, null);
	}
	
	public AsynchronousModule(Annotation annotation) {
		this(null, annotation);
	}
	
	public AsynchronousModule(ExecutorService executor, Annotation annotation) {
		ExecutorService myExecutor = executor==null ? createDefaultExecutor(annotation) : executor;
		AsynchronousManager amanager = new AsynchronousManager(myExecutor);
		
		this.context = amanager;
		this.exceptionListener = amanager;
		
		this.annotation = annotation;
	}
	
	public final void configure(Binder binder) {
		configure(createBinder(binder));
	}
	
	private final void configure(BinderInterceptor binder) {
		synchronized (this) {
			if(this.binder !=null || this.asyncBindings!=null) {
				throw new IllegalStateException();
			}
			init(binder);
		}
		
		try {
			configure();
			processAsynchronousBindings();
		} catch (Exception e) {
			binder.addError(e);
		} finally {
			destroy();
		}
	}
	
	private BinderInterceptor createBinder(Binder binder) {
		if(binder instanceof BinderInterceptor) {
			return (BinderInterceptor) binder;
		} else {
			return new BinderInterceptorImpl(new InterceptorManager(), binder);
		}
	}
	
	private synchronized void init(BinderInterceptor binder) {
		this.binder = binder;
		this.asyncBindings = new LinkedList<AsynchronousBindingBean<?>>();
	}
	
	private synchronized void destroy() {
		this.binder = null;
		this.asyncBindings = null;
	}
	
	private void processAsynchronousBindings() {
		BindingClassFactory bindingClassFactory = new BindingClassFactoryImpl(binder.getInterceptorManager().getInterceptors());
		EnhancerFactory enhancerFactory = new EnhancerFactoryImpl(context.getExecutor(), exceptionListener);
		
		for(AsynchronousBindingBean<?> bean : asyncBindings) {
			AsynchronousBinding<?> binding = createAsynchronousBinding(bean, bindingClassFactory, enhancerFactory);
			binding.applyTo(binder);
		}
		
		if(annotation!=null) {
			binder.bind(Key.get(AsynchronousContext.class, annotation)).toInstance(context);
		} else {
			binder.bind(Key.get(AsynchronousContext.class)).toInstance(context);
		}
	}
	
	private <T> AsynchronousBinding<T> createAsynchronousBinding(AsynchronousBindingBean<T> bean, BindingClassFactory bindingClassFactory, EnhancerFactory enhancerFactory) {
		AsynchronousBinding<T> binding = new AsynchronousBinding<T>(bean.getKey(), bean.getConstructor(), bean.getScopeBinding(), bean.getSource(), bindingClassFactory, enhancerFactory);
		return binding;
	}
	
	protected abstract void configure() throws Exception;
	
	protected Binder getBinder() {
		return binder;
	}
	
	protected <T> AsynchronousBindingBuilder<T> bindAsynchronous(Class<T> clazz) {
		return bindAsynchronous(Key.get(clazz));
	}
	protected <T> AsynchronousBindingBuilder<T> bindAsynchronous(TypeLiteral<T> type) {
		return bindAsynchronous(Key.get(type));
	}
	protected <T> AsynchronousBindingBuilder<T> bindAsynchronous(Key<T> key) {
		AsynchronousBindingBuilderImpl<T> builder = new AsynchronousBindingBuilderImpl<T>(key, GuiceAsyncUtils.getSource());
		this.asyncBindings.add(builder);
		
		return builder;
	}
	
	/**
	 * Creates an executor service when one is not passed into the
	 * {@link #asynchronize(ExecutorService, Module...)} method
	 * 
	 * @return The default executor service
	 */
	private static ExecutorService createDefaultExecutor(Annotation annotation) {
		ThreadFactory threadFactory = new MyThreadFactory(THREAD_NAME_PREFIX + " : " + (annotation == null ? "" : (annotation.toString() + " : ")), true);
		return Executors.newCachedThreadPool(threadFactory);
	}
}
