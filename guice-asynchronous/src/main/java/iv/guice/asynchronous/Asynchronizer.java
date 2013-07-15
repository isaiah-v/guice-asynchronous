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

import iv.guice.asynchronous.impl.aopclass.AopClass;
import iv.guice.asynchronous.impl.aopclass.AopClassFinder;
import iv.guice.asynchronous.impl.cglib.EnhancerElement;
import iv.guice.asynchronous.impl.cglib.EnhancerFactory;
import iv.guice.asynchronous.impl.elements.ElementsBean;
import iv.guice.asynchronous.impl.elements.ElementsBeanFactory;
import iv.guice.asynchronous.impl.manager.AsynchronousManager;
import iv.guice.asynchronous.impl.utils.MyThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import net.sf.cglib.proxy.Enhancer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.google.inject.name.Named;
import com.google.inject.spi.Elements;

import static iv.guice.asynchronous.impl.utils.GuiceAsyncUtils.*;

/**
 * Guice-Asynchronous<br>
 * <br>
 * Enables asynchronous method calls while supporting guice's existing AOP
 * behavior. This allows for other frameworks that utilize the AOP mechanism to
 * size-by-side with guice-asynchronous.
 * 
 * @author Isaiah van der Elst
 */
public class Asynchronizer {

    /** The prefix for all names */
    private static final String NAME_PREFIX = "iv.guice.asynchronous";

    /**
     * The {@link Named} value used to bind the {@link Shutdownable} for the
     * asynchronous service<br>
     * <br>
     * <b>Example Injection:</b> <code>
     * <br>@Inject
     * <br>@Named(NAME_SHUTDOWNABLE)
     * <br>Shutdownable shutdownable;
     */
    public static final String NAME_SHUTDOWNABLE = NAME_PREFIX + "/shutdownable";

    /**
     * The {@link Named} value used to bind the {@link AsynchronousContext} for
     * the asynchronous servicer.<br>
     * <br>
     * <b>Example Injection:</b> <code>
     * <br>@Inject
     * <br>@Named(NAME_ASYNCHRONOUS_CONTEXT)
     * <br>AsynchronousContext context;
     * </code>
     */
    public static final String NAME_ASYNCHRONOUS_CONTEXT = NAME_PREFIX + "/asynchronousContext";

    /**
     * The {@link Key} used to bing the {@link AsynchronousContext}. Use
     * {@link Injector#getBinding(Key)} to poll the instance variable from the
     * injector.
     */
    public static final Key<AsynchronousContext> KEY_ASYNCHRONOUS_CONTEXT = Key.get(AsynchronousContext.class, Names.named(NAME_ASYNCHRONOUS_CONTEXT));

    /**
     * The {@link Key} used to bind the {@link Shutdownable}. Use
     * {@link Injector#getBinding(Key)} to poll the instance variable from the
     * injector.
     */
    public static final Key<Shutdownable> KEY_SHUTDOWNABLE = Key.get(Shutdownable.class, Names.named(NAME_SHUTDOWNABLE));

    /** Static Class */
    private Asynchronizer() {

    }

    /**
     * Creates a module with the asynchronous service enabled. The service is
     * used to asynchronize methods that are marked with the
     * {@link Asynchronous} annotation.
     * 
     * @param executor
     *            The executor service used to process asynchronous tasks. This
     *            operation assumes ownership over the executor service. The
     *            service will take care of shutting down the executor.
     * @param modules
     *            The set of modules to asynchronize
     * @return The asynchronized module. This module contains the elements from
     *         the given bindings and the binds used by the service.
     */
    public static final Module asynchronize(ExecutorService executor, Module... modules) {
        if (executor == null) executor = createDefaultExecutor();
        AsynchronousManager aManager = new AsynchronousManager(executor);

        ElementsBean elements = ElementsBeanFactory.createElementsBean(modules);

        AopClass<?>[] aopClasses = AopClassFinder.findAopClasses(elements);
        for (AopClass<?> aopClass : aopClasses) {
            Enhancer e = EnhancerFactory.createEnhancer(aManager, aManager, aopClass);
            EnhancerElement<?> element = EnhancerElement.createEnhancerElement(aopClass, e);

            elements.getBindings().remove(aopClass.getKey());
            elements.getOthers().add(element);
        }

        elements.getBindings().put(KEY_ASYNCHRONOUS_CONTEXT, bindInstance(KEY_ASYNCHRONOUS_CONTEXT, aManager));
        elements.getBindings().put(KEY_SHUTDOWNABLE, bindInstance(KEY_SHUTDOWNABLE, aManager));

        return Elements.getModule(elements.createElementCollection());
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
    public static final Module asynchronize(Module... modules) {
        return asynchronize(null, modules);
    }

    /**
     * Polls the {@link AsynchronousContext} from the given {@link Injector}.
     * 
     * @see #KEY_ASYNCHRONOUS_CONTEXT
     * @see #NAME_ASYNCHRONOUS_CONTEXT
     * @param injector
     *            The Injector
     * @return The {@link AsynchronousContext} bound to the given
     *         {@link Injector}.
     */
    public static AsynchronousContext getAsynchronousContext(Injector injector) {
        return injector.getInstance(KEY_ASYNCHRONOUS_CONTEXT);
    }

    /**
     * Polls the {@link Shutdownable} from the given {@link Injector}
     * 
     * @see #KEY_SHUTDOWNABLE
     * @see #NAME_SHUTDOWNABLE
     * @param injector
     *            the injector
     * @return The {@link Shutdownable} bound to the given {@link Injector}
     */
    public static Shutdownable getShutdownable(Injector injector) {
        return injector.getInstance(KEY_SHUTDOWNABLE);
    }

    /**
     * Creates an executor service when one is not passed into the
     * {@link #asynchronize(ExecutorService, Module...)} method
     * 
     * @return The default executor service
     */
    private static ExecutorService createDefaultExecutor() {
        ThreadFactory threadFactory = new MyThreadFactory(NAME_PREFIX + "-", true);
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
     * Equivalent to:<br>
     * <code>getShutdownable(injector).shutdown()</code>
     * 
     * @see #getShutdownable(Injector)
     * @see Shutdownable
     * @param injector
     *            the injector
     * @throws InterruptedException
     *             If waiting thread is interrupted while shutting down the
     *             service
     */
    public static void shutdown(Injector injector) throws InterruptedException {
        getShutdownable(injector).shutdown();
    }
}
