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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Collection;

import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClass;
import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClassFactory;
import org.ivcode.guice.asynchronous.internal.binder.InterceptorElement;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerData;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerFactory;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerProvider;
import org.ivcode.guice.asynchronous.internal.utils.GuiceAsyncUtils;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.reflect.FastConstructor;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.TypeLiteralFactory;
import com.google.inject.binder.ScopedBindingBuilder;

public class AsynchronousBinding<T> implements Binding  {
	
	private final AsynchronousClassFactory asyncClassFactory;
	private final EnhancerFactory enhancerFactory;
	
	private final Key<T> key;
	private final Constructor<? extends T> constructor;
	private final Collection<InterceptorElement> interceptors;
	private final ScopeBinding scopeBinding;
	private final Object source;
	
	public AsynchronousBinding(Key<T> key, Constructor<? extends T> c, Collection<InterceptorElement> interceptors, ScopeBinding scopeBinding, Object source, AsynchronousClassFactory asyncClassFactory, EnhancerFactory enhancerFactory) {
		if(key==null) { throw new NullPointerException(); }
		
		this.key = key;
		this.constructor = c;
		this.interceptors = interceptors;
		this.scopeBinding = scopeBinding;
		this.source = source;
		
		this.asyncClassFactory = asyncClassFactory;
		this.enhancerFactory = enhancerFactory;
	}
	
	public void applyTo(Binder binder) {
        final AsynchronousClass<?> asyncClass = asyncClassFactory.createAsynchronousClass(key, constructor, interceptors);
		
		setWithSource(binder);
        
        // create the private binder
        PrivateBinder privateBinder = binder.newPrivateBinder();

        // bind the EnhancerData to the private binder
        bindEnhancerData(privateBinder, asyncClass);
        
        bindObjectFactory(privateBinder, asyncClass);
        
        // bind the key to the Enhancer's provider to the provate binder
        TypeLiteral<EnhancerProvider<T>> type = createEnhancerProviderType();
        ScopedBindingBuilder sbb = bindEnhancerProvider(privateBinder, type);
        
        // bind the key's scope to the original scope
        if(scopeBinding!=null) {
        	scopeBinding.applyTo(sbb);
        } else {
        	Annotation a = GuiceAsyncUtils.findScopeAnnotation(key);
        	if(a!=null) { sbb.in(a.annotationType()); }
        }
        
        // Expose the key 
        privateBinder.expose(key);
    }
	
	private void setWithSource(Binder binder) {
        if(source!=null) binder.withSource(source);
    }
    
    private void bindEnhancerData(Binder binder, AsynchronousClass<?> aopClass) {
    	EnhancerData ed = enhancerFactory.createEnhancer(aopClass);
    	
        binder.bind(FastConstructor.class).toInstance(ed.getFastConstructor());
        binder.bind(Callback[].class).toInstance(ed.getCallbacks());
    }
    
    private ScopedBindingBuilder bindEnhancerProvider(Binder binder, TypeLiteral<EnhancerProvider<T>> type) {
        return binder.bind(key).toProvider(type);
    }
    
    public void bindObjectFactory(Binder binder, AsynchronousClass<?> aopClass) {
        Key<?>[] argumentKeys = aopClass.getConstructor().getArgumentKeys();
        
        Provider<?>[] provider = new Provider[argumentKeys.length];
        for(int i=0; i<provider.length; i++) {
            Key<?> key = argumentKeys[i];
            
            Provider<?> p = binder.getProvider(key);
            provider[i] = p;
        }
        
        binder.bind(Provider[].class).toInstance(provider);
    }
    
    private TypeLiteral<EnhancerProvider<T>> createEnhancerProviderType() {
        Type mainType = EnhancerProvider.class;
        Type genaricType = key.getTypeLiteral().getType();
        return TypeLiteralFactory.createParameterizedTypeLiteral(mainType, genaricType);
    }

	@Override
	public String toString() {
		return "AsynchronousBinding [asyncClassFactory=" + asyncClassFactory
				+ ", enhancerFactory=" + enhancerFactory + ", key=" + key
				+ ", constructor=" + constructor + ", interceptors="
				+ interceptors + ", scopeBinding=" + scopeBinding + ", source="
				+ source + "]";
	}
}
