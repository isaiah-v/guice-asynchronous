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
package org.ivcode.guice.asynchronous.internal.processor;

import java.util.concurrent.Executor;

import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClassFactory;
import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClassFactoryImpl;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerFactory;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerFactoryImpl;

public class AsynchronousBindingProcessorFactoryImpl implements AsynchronousBindingProcessorFactory {

	public AsynchronousBindingProcessor createAsynchronousBindingProcessor(Executor executor) {
		final AsynchronousClassFactory asyncClassFactory = new AsynchronousClassFactoryImpl();
		final EnhancerFactory enhancerFactory = new EnhancerFactoryImpl(executor);
		final AsynchronousBindingProcessor bindingProcessor = new AsynchronousBindingProcessorImpl(asyncClassFactory, enhancerFactory);
		
		return bindingProcessor;
	}

	@Override
	public String toString() {
		return "AsynchronousBindingProcessorFactoryImpl";
	}
}