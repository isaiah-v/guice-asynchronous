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
package org.ivcode.guice.asynchronous.impl.bindingclass;


import static org.ivcode.guice.asynchronous.impl.utils.GuiceAsyncUtils.findBindingAnnotation;
import static org.ivcode.guice.asynchronous.impl.utils.GuiceAsyncUtils.findInjectConstructor;
import static org.ivcode.guice.asynchronous.impl.utils.GuiceAsyncUtils.getRawType;

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
import org.ivcode.guice.asynchronous.impl.binder.InterceptorBean;

import com.google.inject.Key;
import com.google.inject.Provider;

public class BindingClassFactoryImpl implements BindingClassFactory {

	private final Collection<InterceptorBean> interceptors;
	
	public BindingClassFactoryImpl(Collection<InterceptorBean> interceptors) {
		this.interceptors = interceptors;
	}
	
	public <T> BindingClass<T> getBindingClass(Key<T> key, Constructor<? extends T> c) {
		if(!isAsyncClass(key)) {
			throw new IllegalArgumentException("class contains no asychornous annotations");
		}
		
		BindingConstructor constructor = getAopConstructor(key, c);
		BindingMethod[] methods = getAopMethods(key);
		
		return createAopClass(key, constructor, methods);
	}
    
    
    private BindingConstructor getAopConstructor(Key<?> key, Constructor<?> c) {
        Class<?> clazz = getRawType(key);
        
        BindingConstructor aopConstructor = null;
        if(c!=null) {
            aopConstructor = getAopConstructor(c);
        } else {
            aopConstructor = getAopConstructor(clazz);
        }
        
        return aopConstructor==null ? getAopConstructor() : aopConstructor;
    }
    
    private BindingConstructor getAopConstructor() {
        BindingConstructor aopConstructor = new BindingConstructor();
        aopConstructor.setArgumentTypes(new Class[0]);
        aopConstructor.setArgumentKeys(new Key[0]);
        
        return aopConstructor;
    }
    
    private BindingConstructor getAopConstructor(Class<?> clazz) {
        Constructor<?> c = findInjectConstructor(clazz);
        if(c==null) return null;
        
        Class<?>[] argumentTypes = c.getParameterTypes();
        Key<?>[] argumentKeys = new Key[argumentTypes.length];
        
        Provider<?>[] provider = new Provider[argumentTypes.length];
        for(int i=0; i<provider.length; i++) {
            Class<?> argclazz = argumentTypes[i];
            Key<?> key = getKey(argclazz, c.getParameterAnnotations()[i]);
            
            argumentKeys[i] = key;
        }
        
        BindingConstructor aopConstructor = new BindingConstructor();
        aopConstructor.setArgumentTypes(argumentTypes);
        aopConstructor.setArgumentKeys(argumentKeys);
        
        return aopConstructor;
    }
    
    private BindingConstructor getAopConstructor(Constructor<?> c) {
    	Class<?>[] argumentTypes = c.getParameterTypes();
        Key<?>[] argumentKeys = new Key[argumentTypes.length];
        
        for(int i=0; i<argumentTypes.length; i++) {
        	Key<?> key = getKey(argumentTypes[i], c.getParameterAnnotations()[i]);
            argumentKeys[i] = key;
        }
        
        BindingConstructor aopConstructor = new BindingConstructor();
        aopConstructor.setArgumentTypes(argumentTypes);
        aopConstructor.setArgumentKeys(argumentKeys);
        
        return aopConstructor;
    }
    
    private <T> Key<T> getKey(Class<T> clazz, Annotation[] annotations) {
        Annotation a = findBindingAnnotation(annotations);
        if(a!=null)
            return Key.get(clazz, a);
        else
            return Key.get(clazz);
    }
    
    private BindingMethod[] getAopMethods(Key<?> key) {
        if (key == null) return null;
        Class<?> clazz = getRawType(key);

        List<BindingMethod> value = new LinkedList<BindingMethod>();

        for (Method method : clazz.getDeclaredMethods()) {
            boolean isAsynchronous = method.isAnnotationPresent(Asynchronous.class);

            List<MethodInterceptor> list = new LinkedList<MethodInterceptor>();
            
            for(InterceptorBean ibean : interceptors) {
            	if(!ibean.getClassMatcher().matches(clazz)) { continue; }
            	if(!ibean.getMethodMatcher().matches(method)) { continue; }
            	
            	list.addAll(Arrays.asList(ibean.getInterceptors()));
            }

            if ((list != null && !list.isEmpty()) || isAsynchronous) {
                if (isAsynchronous) validateAsynchronousSignature(method);

                BindingMethod aMethod = new BindingMethod(method, isAsynchronous, list);
                value.add(aMethod);
            }
        }

        return value.isEmpty() ? null : value.toArray(new BindingMethod[value.size()]);
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
    
    private <T> BindingClass<T> createAopClass(Key<T> key, BindingConstructor constructor, BindingMethod[] methods) {
        BindingClass<T> aopClass = new BindingClass<T>();
        aopClass.setKey(key);
        aopClass.setConstructor(constructor);
        aopClass.setMethods(methods);

        return aopClass;
    }
}
