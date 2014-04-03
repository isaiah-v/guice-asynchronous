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
package org.ivcode.guice.asynchronous.internal.binding;

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClass;
import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClassFactory;
import org.ivcode.guice.asynchronous.internal.binder.InterceptorElement;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerData;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerFactory;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;

public class AsynchronousBinding<T> implements Binding  {
	
	private final AsynchronousClassFactory asyncClassFactory;
	private final EnhancerFactory enhancerFactory;
	
	private final Binder binder;
	
	private final Key<? super T> sourceKey;
	private final Key<T> targetKey;
	private final Constructor<T> constructor;
	private final Collection<InterceptorElement> interceptors;
	private final ScopeBinding scopeBinding;
	private final Object source;
	
	public AsynchronousBinding(Binder binder, Key<? super T> sourceKey, Key<T> targetKey, Constructor<T> c, Collection<InterceptorElement> interceptors, ScopeBinding scopeBinding, Object source, AsynchronousClassFactory asyncClassFactory, EnhancerFactory enhancerFactory) {
		if(targetKey==null) { throw new NullPointerException(); }
		
		this.binder = binder;
		
		this.sourceKey = sourceKey;
		this.targetKey = targetKey;
		this.constructor = c;
		this.interceptors = interceptors;
		this.scopeBinding = scopeBinding;
		this.source = source;
		
		this.asyncClassFactory = asyncClassFactory;
		this.enhancerFactory = enhancerFactory;
	}
	
	public void bind() {
        final AsynchronousClass<?> asyncClass = asyncClassFactory.createAsynchronousClass(targetKey, constructor, interceptors);
        final EnhancerData enhancerData = enhancerFactory.createEnhancer(asyncClass);
		
		setSource();
        
        // create the private binder
        PrivateBinder privateBinder = binder.newPrivateBinder();

        privateBinder.install(new AsynchronousBindingModule<T>(sourceKey, targetKey, scopeBinding, asyncClass, enhancerData));
        
        // Expose the key 
        privateBinder.expose(sourceKey);
    }
	
	private void setSource() {
        if(source!=null) binder.withSource(source);
    }

	@Override
	public String toString() {
		return "AsynchronousBinding [asyncClassFactory=" + asyncClassFactory
				+ ", enhancerFactory=" + enhancerFactory + ", binder=" + binder
				+ ", sourceKey=" + sourceKey + ", targetKey=" + targetKey
				+ ", constructor=" + constructor + ", interceptors="
				+ interceptors + ", scopeBinding=" + scopeBinding + ", source="
				+ source + "]";
	}
}
