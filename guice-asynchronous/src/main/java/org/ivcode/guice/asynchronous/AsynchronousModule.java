package org.ivcode.guice.asynchronous;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import org.ivcode.guice.asynchronous.impl.binder.AsynchronousBinderManager;
import org.ivcode.guice.asynchronous.impl.bindingclass.BindingClassFactory;
import org.ivcode.guice.asynchronous.impl.bindingclass.BindingClassFactoryImpl;
import org.ivcode.guice.asynchronous.impl.cglib.EnhancerFactory;
import org.ivcode.guice.asynchronous.impl.cglib.EnhancerFactoryImpl;
import org.ivcode.guice.asynchronous.impl.context.AsynchronousContextImpl;
import org.ivcode.guice.asynchronous.impl.recorder.AsynchronousBindingProcessor;
import org.ivcode.guice.asynchronous.impl.recorder.AsynchronousBindingProcessorImpl;
import org.ivcode.guice.asynchronous.impl.utils.ClassPreloader;
import org.ivcode.guice.asynchronous.impl.utils.GuiceAsyncUtils;

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

	private final boolean isBindContext;
	private final Annotation annotation;

	private final AsynchronousContext context;

	private final AsynchronousBinderManager bindingManager;
	private AsynchronousBinder rootBinder;
	

	/**
	 * Creates a new {@link AsynchronousModule}<br/>
	 * <br/>
	 * This constructor will result in a new {@link AsynchronousContext} using
	 * an new cached thread-pool and being bound without a binding annotation.
	 */
	public AsynchronousModule() {
		this(null, null);
	}

	/**
	 * Creates a new {@link AsynchronousModule}<br/>
	 * <br/>
	 * This constructor will result in a new {@link AsynchronousContext} using
	 * the given thread-pool and being bound without a binding annotation.
	 * 
	 * @param executor
	 *            The given thread-pool. The guice-asynchronous service will
	 *            assume control over this {@link ExecutorService} and will
	 *            handle shuitting it down.
	 */
	public AsynchronousModule(ExecutorService executor) {
		this(executor, null);
	}

	/**
	 * Creates a new {@link AsynchronousModule}<br/>
	 * <br/>
	 * This constructor will result in a new {@link AsynchronousContext} using
	 * an new cached thread-pool and being bound with the given binding annotation.
	 * 
	 * @param annotation
	 *            The binding annotation
	 */
	public AsynchronousModule(Annotation annotation) {
		this(null, annotation);
	}

	/**
	 * Creates a new {@link AsynchronousModule}<br/>
	 * <br/>
	 * This constructor will result in a new {@link AsynchronousContext} using
	 * the given thread-pool and being bound with the given binding annotation.
	 * 
	 * @param executor
	 *            The given thread-pool. The guice-asynchronous service will
	 *            assume control over this {@link ExecutorService} and will
	 *            handle shuitting it down.
	 * @param annotation
	 *            the binding annotation
	 */
	public AsynchronousModule(ExecutorService executor, Annotation annotation) {
		ExecutorService myExecutor = executor == null ? GuiceAsyncUtils.createDefaultExecutor(annotation) : executor;
		AsynchronousContextImpl context = new AsynchronousContextImpl(myExecutor);
		
		this.annotation = annotation;
		this.context = context;
		this.isBindContext = true;
		this.bindingManager = createAsynchronousBinderManager(context.getExecutor());
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
		this(context, false);
	}
	
	public AsynchronousModule(AsynchronousModule module) {
		this(module.getContext(), false);
	}
	
	private  AsynchronousModule(AsynchronousContext context, boolean isBindContext) {
		this.context = context;
		this.isBindContext = isBindContext;
		this.annotation = null;
		this.bindingManager = createAsynchronousBinderManager(context.getExecutor());
	}
	
	/**
	 * Polls this module's context
	 * @return
	 * 		this module's context
	 */
	public AsynchronousContext getContext() {
		return context;
	}
	
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
			processAsynchronousBindings();
		} catch (Exception e) {
			binder.addError(e);
		} finally {
			destroy();
		}
	}
	
	private AsynchronousBinderManager createAsynchronousBinderManager(Executor executor) {
		final BindingClassFactory bindingClassFactory = new BindingClassFactoryImpl();
		final EnhancerFactory enhancerFactory = new EnhancerFactoryImpl(executor);
		final AsynchronousBindingProcessor bindingProcessor = new AsynchronousBindingProcessorImpl(bindingClassFactory, enhancerFactory);
		final AsynchronousBinderManager bindingManager = new AsynchronousBinderManager(bindingProcessor);
		
		return bindingManager;
	}

	private void processAsynchronousBindings() {
		bindingManager.build();
		bindContext();
	}
	
	private void bindContext() {
		if(!isBindContext) { return; }
		
		if (annotation != null) {
			binder().bind(Key.get(AsynchronousContext.class, annotation)).toInstance(context);
		} else {
			binder().bind(Key.get(AsynchronousContext.class)).toInstance(context);
		}
	}

	private synchronized void init(AsynchronousBinder binder) {
		this.rootBinder = binder;
	}

	private synchronized void destroy() {
		this.rootBinder = null;
	}
	
	/**
	 * Configures a {@link Binder} via the exposed methods.
	 */
	protected abstract void configure() throws Exception;

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
	
	protected AsynchronousBinder binder() {
		return rootBinder;
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
	protected void bindInterceptor(Matcher<? super Class<?>> classMatcher,
			Matcher<? super Method> methodMatcher,
			org.aopalliance.intercept.MethodInterceptor... interceptors) {
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
	protected void convertToTypes(Matcher<? super TypeLiteral<?>> typeMatcher,
			TypeConverter converter) {
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
