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
package org.ivcode.guice.asynchronous.internal.asynchronousclass;


import static org.ivcode.guice.asynchronous.internal.utils.GuiceAsyncUtils.findBindingAnnotation;
import static org.ivcode.guice.asynchronous.internal.utils.GuiceAsyncUtils.findInjectConstructor;
import static org.ivcode.guice.asynchronous.internal.utils.GuiceAsyncUtils.getRawType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.ivcode.guice.asynchronous.Asynchronous;
import org.ivcode.guice.asynchronous.internal.binder.InterceptorElement;

import com.google.inject.Key;
import com.google.inject.Provider;

public class AsynchronousClassFactoryImpl implements AsynchronousClassFactory {
	
	public <T> AsynchronousClass<T> createAsynchronousClass(Key<T> key, Constructor<? extends T> constructor, Collection<InterceptorElement> interceptors) {
		if(!isAsyncClass(key)) {
			throw new IllegalArgumentException("class contains no asychornous annotations");
		}
		
		AsynchronousConstructor aconstructor = createAsynchronousConstructor(key, constructor);
		AsynchronousMethod[] methods = createAsynchronousMethod(key, interceptors);
		
		return createAsynchronousClass(key, aconstructor, methods);
	}
    
    
    private AsynchronousConstructor createAsynchronousConstructor(Key<?> key, Constructor<?> c) {
        Class<?> clazz = getRawType(key);
        
        AsynchronousConstructor asyncConstructor = null;
        if(c!=null) {
            asyncConstructor = createAsynchronousConstructor(c);
        } else {
            asyncConstructor = createAsynchronousConstructor(clazz);
        }
        
        return asyncConstructor==null ? createAsynchronousConstructor() : asyncConstructor;
    }
    
    private AsynchronousConstructor createAsynchronousConstructor() {
        AsynchronousConstructor asyncConstructor = new AsynchronousConstructor();
        asyncConstructor.setArgumentTypes(new Class[0]);
        asyncConstructor.setArgumentKeys(new Key[0]);
        
        return asyncConstructor;
    }
    
    private AsynchronousConstructor createAsynchronousConstructor(Class<?> clazz) {
        Constructor<?> c = findInjectConstructor(clazz);
        if(c==null) return null;
        
        Class<?>[] argumentTypes = c.getParameterTypes();
        Key<?>[] argumentKeys = new Key[argumentTypes.length];
        
        Provider<?>[] provider = new Provider[argumentTypes.length];
        for(int i=0; i<provider.length; i++) {
            Class<?> argclazz = argumentTypes[i];
            Key<?> key = createKey(argclazz, c.getParameterAnnotations()[i]);
            
            argumentKeys[i] = key;
        }
        
        AsynchronousConstructor asyncConstructor = new AsynchronousConstructor();
        asyncConstructor.setArgumentTypes(argumentTypes);
        asyncConstructor.setArgumentKeys(argumentKeys);
        
        return asyncConstructor;
    }
    
    private AsynchronousConstructor createAsynchronousConstructor(Constructor<?> c) {
    	Class<?>[] argumentTypes = c.getParameterTypes();
        Key<?>[] argumentKeys = new Key[argumentTypes.length];
        
        for(int i=0; i<argumentTypes.length; i++) {
        	Key<?> key = createKey(argumentTypes[i], c.getParameterAnnotations()[i]);
            argumentKeys[i] = key;
        }
        
        AsynchronousConstructor asyncConstructor = new AsynchronousConstructor();
        asyncConstructor.setArgumentTypes(argumentTypes);
        asyncConstructor.setArgumentKeys(argumentKeys);
        
        return asyncConstructor;
    }
    
    private <T> Key<T> createKey(Class<T> clazz, Annotation[] annotations) {
        Annotation a = findBindingAnnotation(annotations);
        if(a!=null) {
            return Key.get(clazz, a);
        } else {
            return Key.get(clazz);
        }
    }
    
    private AsynchronousMethod[] createAsynchronousMethod(Key<?> key, Collection<InterceptorElement> interceptors) {
        if (key == null) return null;
        Class<?> clazz = getRawType(key);

        List<AsynchronousMethod> value = new LinkedList<AsynchronousMethod>();

        for (Method method : clazz.getDeclaredMethods()) {
            boolean isAsynchronous = method.isAnnotationPresent(Asynchronous.class);

            List<MethodInterceptor> list = new LinkedList<MethodInterceptor>();
            
            for(InterceptorElement ibean : interceptors) {
            	if(!ibean.getClassMatcher().matches(clazz)) { continue; }
            	if(!ibean.getMethodMatcher().matches(method)) { continue; }
            	
            	list.addAll(Arrays.asList(ibean.getInterceptors()));
            }

            if ((list != null && !list.isEmpty()) || isAsynchronous) {
                if (isAsynchronous) validateAsynchronousSignature(method);

                AsynchronousMethod aMethod = new AsynchronousMethod(method, isAsynchronous, list);
                value.add(aMethod);
            }
        }

        return value.isEmpty() ? null : value.toArray(new AsynchronousMethod[value.size()]);
    }

    private boolean isAsyncClass(Key<?> key) {
        Class<?> clazz = key.getTypeLiteral().getRawType();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Asynchronous.class)) return true;
        }
        return false;
    }

    private void validateAsynchronousSignature(Method method) {
        if (!void.class.equals(method.getReturnType())) {
            throw new RuntimeException("Asynchronous methods must return void: " + method);
        }
        if(Modifier.isPrivate(method.getModifiers())) {
            throw new RuntimeException("Asynchronous methods must return void: " + method);
        }
    }
    
    private <T> AsynchronousClass<T> createAsynchronousClass(Key<T> key, AsynchronousConstructor constructor, AsynchronousMethod[] methods) {
        AsynchronousClass<T> asyncClass = new AsynchronousClass<T>();
        asyncClass.setKey(key);
        asyncClass.setConstructor(constructor);
        asyncClass.setMethods(methods);

        return asyncClass;
    }


	@Override
	public String toString() {
		return "AsynchronousClassFactoryImpl";
	}
}
