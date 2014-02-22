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

import java.util.Collection;

import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClassFactory;
import org.ivcode.guice.asynchronous.internal.binding.AsynchronousBinding;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerFactory;

import com.google.inject.Binder;

public class AsynchronousBindingProcessorImpl implements AsynchronousBindingProcessor {
	
	private final AsynchronousClassFactory bindingClassFactory;
	private final EnhancerFactory enhancerFactory;
	
	public AsynchronousBindingProcessorImpl(AsynchronousClassFactory bindingClassFactory, EnhancerFactory enhancerFactory) {
		this.bindingClassFactory = bindingClassFactory;
		this.enhancerFactory = enhancerFactory;
	}

	public void process(Collection<AsynchronousBindingBean<?>> asyncBindings) {
		for (AsynchronousBindingBean<?> bean : asyncBindings) {
			Binder binder = bean.getBinder();
			
			AsynchronousBinding<?> binding = createAsynchronousBinding(bean,bindingClassFactory, enhancerFactory);
			binding.applyTo(binder);
		}
	}
	
	private <T> AsynchronousBinding<T> createAsynchronousBinding(AsynchronousBindingBean<T> bean, AsynchronousClassFactory bindingClassFactory, EnhancerFactory enhancerFactory) {
		AsynchronousBinding<T> binding = new AsynchronousBinding<T>(
				bean.getKey(),
				bean.getConstructor(),
				bean.getInterceptors(),
				bean.getScopeBinding(), bean.getSource(),
				bindingClassFactory,
				enhancerFactory);
		
		return binding;
	}

	@Override
	public String toString() {
		return "AsynchronousBindingProcessorImpl [bindingClassFactory="
				+ bindingClassFactory + ", enhancerFactory=" + enhancerFactory
				+ "]";
	}
}
