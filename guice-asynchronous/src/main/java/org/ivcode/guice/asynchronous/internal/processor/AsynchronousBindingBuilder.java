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

import org.ivcode.guice.asynchronous.internal.binder.InterceptorElement;
import org.ivcode.guice.asynchronous.internal.binding.Binding;
import org.ivcode.guice.asynchronous.internal.binding.BindingBuilder;
import org.ivcode.guice.asynchronous.internal.binding.BindingFactory;

import com.google.inject.Binder;

public class AsynchronousBindingBuilder implements BindingBuilder {
	
	private final Binder binder;
	private final Collection<InterceptorElement> interceptors;
	private final Object bindingSource;
	
	private MyAsynchronousAnnotatedBindingBuilder<?> myBindingBuilder;
	
	public AsynchronousBindingBuilder(Binder binder, Collection<InterceptorElement> interceptors, Object source) {
		this.binder = binder;
		this.interceptors = interceptors;
		this.bindingSource = source;
	}
	
	void setMyBindingBuilder(MyAsynchronousAnnotatedBindingBuilder<?> myBindingBuilder) {
		this.myBindingBuilder = myBindingBuilder;
	}

	public Binding build(BindingFactory bindingFactory) {
		return myBindingBuilder.createBinding(bindingFactory, binder, bindingSource, interceptors);
	}
}
