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
package iv.guice.asynchronous.impl.bindingclass;

import iv.guice.asynchronous.Asynchronous;
import iv.guice.asynchronous.impl.elements.ElementContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.spi.ConstructorBinding;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InterceptorBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.UntargettedBinding;

import static iv.guice.asynchronous.impl.utils.GuiceAsyncUtils.*;

public class BindingClassFactoryImpl implements BindingClassFactory {

    public BindingClass<?>[] getBindingClasses(ElementContainer elements) {
        BindingsTargetVisitor btv = new BindingsTargetVisitor(elements);
        return getBindingClasses(elements, btv);
    }
    
    protected BindingClass<?>[] getBindingClasses(ElementContainer elements, BindingsTargetVisitor btv) {
        Collection<BindingClass<?>> value = new ArrayList<BindingClass<?>>();
        
        for (Binding<?> b : elements.getBindings().values()) {
            Key<?> key = b.acceptTargetVisitor(btv);
            if (key == null || !isAsyncClass(key)) continue;

            Object source = b.getSource();

            BindingConstructor constructor = getAopConstructor(key, b);
            BindingMethod[] methods = getAopMethods(key, elements);

            value.add(createAopClass(source, key, constructor, methods, b));
        }

        return value.isEmpty() ? null : value.toArray(new BindingClass[value.size()]);
    }
    
    private BindingConstructor getAopConstructor(Key<?> key, Binding<?> b) {
        Class<?> clazz = getRawType(key);
        
        BindingConstructor aopConstructor = null;
        if(b instanceof ConstructorBinding) {
            aopConstructor = getAopConstructor((ConstructorBinding<?>)b);
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
    
    private BindingConstructor getAopConstructor(ConstructorBinding<?> binding) {
        List<Dependency<?>> dependencies = binding.getConstructor().getDependencies();
        
        Class<?>[] argumentTypes = new Class[dependencies.size()];
        Key<?>[] argumentKeys = new Key[dependencies.size()];
        
        for(int i=0; i<argumentTypes.length; i++) {
            Dependency<?> d = dependencies.get(i);
            Key<?> key = d.getKey();
            
            argumentTypes[i] = getRawType(d.getKey());
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

    private BindingMethod[] getAopMethods(Key<?> key, ElementContainer elements) {
        if (key == null) return null;
        Class<?> clazz = getRawType(key);

        List<BindingMethod> value = new LinkedList<BindingMethod>();

        for (Method method : clazz.getDeclaredMethods()) {
            boolean isAsynchronous = method.isAnnotationPresent(Asynchronous.class);

            List<MethodInterceptor> list = null;
            for (InterceptorBinding ib : elements.getInterceptorBindings()) {
                if (!ib.getClassMatcher().matches(clazz) || !ib.getMethodMatcher().matches(method))
                    continue;

                if (list == null) list = new LinkedList<MethodInterceptor>();
                list.addAll(ib.getInterceptors());
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
    
    private <T> BindingClass<T> createAopClass(Object source, Key<T> key, BindingConstructor constructor, BindingMethod[] methods, Binding<?> binding) {
        BindingClass<T> aopClass = new BindingClass<T>();
        aopClass.setSource(source);
        aopClass.setKey(key);
        aopClass.setConstructor(constructor);
        aopClass.setMethods(methods);
        aopClass.setBindingSource(binding);

        return aopClass;
    }

    private class BindingsTargetVisitor extends DefaultBindingTargetVisitor<Object, Key<?>> {

        private final ElementContainer elements;

        BindingsTargetVisitor(ElementContainer elements) {
            this.elements = elements;
        }

        @Override
        public Key<?> visit(LinkedKeyBinding<? extends Object> binding) {
            Binding<?> targetBinding = elements.getBindings().get(binding.getLinkedKey());
            if (targetBinding == null)
                return binding.getKey();
            else
                return null; // only interested in the end target class
        }
        
        @Override
        public Key<?> visit(ConstructorBinding<? extends Object> binding) {
            Key<?> key = binding.getKey();
            return key;
        }

        @Override
        public Key<?> visit(UntargettedBinding<? extends Object> binding) {
            Key<?> key = binding.getKey();
            return key;
        }
    }
}
