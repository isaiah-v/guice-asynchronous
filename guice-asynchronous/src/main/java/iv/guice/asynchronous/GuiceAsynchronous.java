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
package iv.guice.asynchronous;

import iv.guice.asynchronous.impl.bindingclass.BindingClass;
import iv.guice.asynchronous.impl.bindingclass.BindingClassFactory;
import iv.guice.asynchronous.impl.bindingclass.BindingClassFactoryImpl;
import iv.guice.asynchronous.impl.cglib.EnhancerElement;
import iv.guice.asynchronous.impl.cglib.EnhancerFactory;
import iv.guice.asynchronous.impl.elements.ElementContainer;
import iv.guice.asynchronous.impl.elements.ElementContainerFactory;
import iv.guice.asynchronous.impl.elements.ElementContainerFactoryImpl;
import iv.guice.asynchronous.impl.manager.AsynchronousManager;
import iv.guice.asynchronous.impl.manager.ExceptionListener;
import iv.guice.asynchronous.impl.utils.MyThreadFactory;

import java.lang.annotation.Annotation;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import net.sf.cglib.proxy.Enhancer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

import static iv.guice.asynchronous.impl.utils.GuiceAsyncUtils.*;

/**
 * GuiceAsynchronous<br>
 * <br>
 * Enables asynchronous method calls while supporting guice's existing AOP
 * behavior. This allows for other frameworks that utilize the AOP mechanism to
 * size-by-side with guice-asynchronous.
 * 
 * @author Isaiah van der Elst
 */
public final class GuiceAsynchronous {

	/** The thread name prefix for the internal thread factory */
	private static final String THREAD_NAME_PREFIX = "iv.guice.asynchronous";

	/**
	 * Creates a module with the asynchronous service enabled. The service is
	 * used to asynchronize methods that are marked with the
	 * {@link Asynchronous} annotation.
	 * 
	 * @param executor
	 *            The executor service used to process asynchronous tasks. This
	 *            operation assumes ownership over the executor service. The
	 *            service will take care of shutting down the executor.
	 * @param annotation
	 *            A binding annotation. A binding annotation can be used to
	 *            compartmentalize the guice-asynchronous service from any other
	 *            classes/libaries that may be using the same service.
	 * @param modules
	 *            The set of modules to asynchronize
	 * @return The asynchronized module. This module contains the elements from
	 *         the given bindings and the binds used by the service.
	 */
	public static Module asynchronize(ExecutorService executor, Annotation annotation, Module... modules) {
		if (executor==null) { executor=createDefaultExecutor(annotation); }

		AsynchronousManager aManager = new AsynchronousManager(executor);
		BindingClassFactory bindingClassFinder = new BindingClassFactoryImpl();

		ElementContainerFactory elementContainerFactory = new ElementContainerFactoryImpl();
		ElementContainer elements = elementContainerFactory.createElementContainer(modules);

		return asynchronize(annotation, aManager, aManager, aManager, elements, bindingClassFinder);
	}

	/** @see #asynchronize(ExecutorService, Module...) */
	protected static Module asynchronize(
			Annotation annotation,
			AsynchronousContext context,
			Shutdownable shutdownable,
			ExceptionListener exceptionListener,
			ElementContainer elements,
			BindingClassFactory bindingClassFinder) {

		// validate input
		assert context!=null : "null context";
		assert shutdownable!=null : "null shutdownable";
		assert exceptionListener!=null : "null exception listener";
		assert elements!=null : "null elements";
		assert bindingClassFinder!=null : "null binding class finder";

		BindingClass<?>[] bindingClasses = bindingClassFinder.getBindingClasses(elements);
		if (bindingClasses != null && bindingClasses.length > 0) {
			// classes were found to have asynchronous methods
			for (BindingClass<?> bindingClass : bindingClasses) {
				Enhancer e = EnhancerFactory.createEnhancer(context.getExecutor(), exceptionListener, bindingClass);
				EnhancerElement<?> element = EnhancerElement.createEnhancerElement(bindingClass, e);

				elements.getBindings().remove(bindingClass.getKey());
				elements.getOthers().add(element);
			}
		} else {
			// no classes were found to have asynchronous methods
			shutdownable.shutdownNow();
		}
		
		// bind the context and shutdownable, even if there are no asynchronous methods
		Key<AsynchronousContext> contextKey = annotation == null ? Key.get(AsynchronousContext.class) : Key.get(AsynchronousContext.class, annotation);
		Key<Shutdownable> shutdownableKey = annotation == null ? Key.get(Shutdownable.class) : Key.get(Shutdownable.class,annotation);
		
		elements.getBindings().put(contextKey,bindInstance(contextKey, context));
		elements.getBindings().put(shutdownableKey,bindInstance(shutdownableKey, shutdownable));

		return elements.createModule();
	}

	/**
	 * Creates a module with the asynchronous service enabled. The service is
	 * used to asynchronize methods that are marked with the
	 * {@link Asynchronous} annotation.
	 * 
	 * @param modules
	 *            The set of modules to asynchronize
	 * @return The asynchronized module. This module contains the elements from
	 *         the given bindings and the binds used by the service.
	 */
	public static Module asynchronize(Module... modules) {
		return asynchronize(null, null, modules);
	}

	/**
	 * Creates a module with the asynchronous service enabled. The service is
	 * used to asynchronize methods that are marked with the
	 * {@link Asynchronous} annotation.
	 * 
	 * @param annotation
	 *            A binding annotation. A binding annotation can be used to
	 *            compartmentalize the guice-asynchronous service from any other
	 *            classes/libaries that may be using the same service.
	 * @param modules
	 *            The set of modules to asynchronize
	 * @return The asynchronized module. This module contains the elements from
	 *         the given bindings and the binds used by the service.
	 */
	public static Module asynchronize(Annotation annotation, Module... modules) {
		return asynchronize(null, annotation, modules);
	}

	/**
	 * Polls the {@link AsynchronousContext} from the given {@link Injector}.
	 * 
	 * @param injector
	 *            The injector
	 * @return The {@link AsynchronousContext} bound to the given {@link Injector}.
	 */
	public static AsynchronousContext getAsynchronousContext(Injector injector) {
		return injector.getInstance(AsynchronousContext.class);
	}

	/**
	 * Polls the {@link AsynchronousContext} from the given {@link Injector} and binding {@link Annotation}
	 * 
	 * @param injector
	 * 		The injector
	 * @param annotation
	 * 		The binding annotation
	 * @return The {@link AsynchronousContext} bound to the given {@link Injector} and binding {@link Annotation}
	 */
	public static AsynchronousContext getAsynchronousContext(Injector injector, Annotation annotation) {
		return injector.getInstance(Key.get(AsynchronousContext.class, annotation));
	}

	/**
	 * Polls the {@link Shutdownable} from the given {@link Injector}
	 * 
	 * @param injector
	 *            the injector
	 * @return The {@link Shutdownable} bound to the given {@link Injector}
	 */
	public static Shutdownable getShutdownable(Injector injector) {
		return injector.getInstance(Shutdownable.class);
	}

	/**
	 * Polls the {@link Shutdownable} from the given {@link Injector} and
	 * binding {@link Annotation}
	 * 
	 * @param injector
	 *            the injector
	 * @param annotation
	 *            the binding annotation
	 * @return The {@link Shutdownable} bound to the given {@link Injector} and
	 *         binding {@link Annotation}
	 */
	public static Shutdownable getShutdownable(Injector injector, Annotation annotation) {
		return injector.getInstance(Key.get(Shutdownable.class, annotation));
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

	/**
	 * A convenience method that creates an {@link Injector}.<br>
	 * <br>
	 * Equivalent to:<br>
	 * <code>Guice.createInjector(asynchronize(modules))</code>
	 * 
	 * @see #asynchronize(Module[])
	 * @param modules
	 *            The set of modules to asynchronize
	 * @return The injector with the given modules asynchronized
	 */
	public static Injector createInjector(Module... modules) {
		return Guice.createInjector(asynchronize(modules));
	}

	/**
	 * A convenience method to shutdown the service.<br>
	 * <br>
	 * Equivalent to: <code>getShutdownable(injector).shutdown()</code>
	 * 
	 * @see #getShutdownable(Injector)
	 * @see Shutdownable
	 * @param injector
	 *            the injector
	 * @throws InterruptedException
	 *             If waiting thread is interrupted while shutting down the
	 *             service
	 */
	public static void shutdownAsync(Injector injector) throws InterruptedException {
		getShutdownable(injector).shutdown();
	}

	/**
	 * A convenience method to shutdown the service.<br>
	 * <br>
	 * Equivalent to:
	 * <code>getShutdownable(injector,annotation).shutdown()</code>
	 * 
	 * @param injector
	 *            the injector
	 * @param annotation
	 *            the binding annotation
	 * @throws InterruptedException
	 *             If waiting thread is interrupted while shutting down the
	 *             service
	 */
	public static void shutdownAsync(Injector injector, Annotation annotation) throws InterruptedException {
		getShutdownable(injector, annotation).shutdown();
	}
}
