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
import com.google.inject.spi.Elements;

import static iv.guice.asynchronous.impl.utils.GuiceAsyncUtils.*;

public class Asynchronizer {
	private static final String NAME_PACKAGE = "iv.guice-asynchronous";

	public static final String NAME_EXECUTOR_SERVICE = NAME_PACKAGE
			+ "/executorService";
	public static final String NAME_SHUTDOWNABLE = NAME_PACKAGE
			+ "/shutdownable";

	public static final Key<ExecutorService> KEY_EXECUTOR_SERVICE = Key.get(
			ExecutorService.class, Names.named(NAME_EXECUTOR_SERVICE));
	public static final Key<AsynchronousContext> KEY_ASYNCHRONOUS_CONTEXT = Key
			.get(AsynchronousContext.class);
	public static final Key<Shutdownable> KEY_SHUTDOWNABLE = Key.get(
			Shutdownable.class, Names.named(NAME_SHUTDOWNABLE));

	private Asynchronizer() {
	}

	public static final Injector createInjector(Module... modules) {
		return Guice.createInjector(asynchronize(modules));
	}

	public static final Module asynchronize(ExecutorService executor,
			Module... modules) {
		AsynchronousManager aManager = new AsynchronousManager(executor);

		ElementsBean elements = ElementsBeanFactory.createElementsBean(modules);

		AopClass<?>[] aopClasses = AopClassFinder.findAopClasses(elements);
		for (AopClass<?> aopClass : aopClasses) {
			Enhancer e = EnhancerFactory.createEnhancer(aManager, aManager,
					aopClass);
			EnhancerElement<?> element = EnhancerElement.createEnhancerElement(
					aopClass, e);

			elements.getBindings().remove(aopClass.getKey());
			elements.getOthers().add(element);
		}

		elements.getBindings().put(KEY_ASYNCHRONOUS_CONTEXT,
				bindInstance(KEY_ASYNCHRONOUS_CONTEXT, aManager));
		elements.getBindings().put(KEY_SHUTDOWNABLE,
				bindInstance(KEY_SHUTDOWNABLE, aManager));
		elements.getBindings().put(KEY_EXECUTOR_SERVICE,
				bindInstance(KEY_EXECUTOR_SERVICE, executor));

		return Elements.getModule(elements.createElementCollection());
	}

	public static final Module asynchronize(Module... modules) {
		ThreadFactory threadFactory = new MyThreadFactory(NAME_PACKAGE + "-",
				true);
		return asynchronize(Executors.newCachedThreadPool(threadFactory),
				modules);
	}

	public static final void shutdown(Injector injector)
			throws InterruptedException {
		Shutdownable shutdownable = injector.getInstance(KEY_SHUTDOWNABLE);
		shutdownable.shutdown();
	}
}
