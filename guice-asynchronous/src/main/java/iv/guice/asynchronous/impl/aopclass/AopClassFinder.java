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
package iv.guice.asynchronous.impl.aopclass;

import iv.guice.asynchronous.Asynchronous;
import iv.guice.asynchronous.impl.elements.ElementsBean;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.InterceptorBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.UntargettedBinding;

public class AopClassFinder {

    private AopClassFinder() {}

    public static AopClass<?>[] findAopClasses(ElementsBean elements) {
        Collection<AopClass<?>> value = new ArrayList<AopClass<?>>();

        BindingsTargetVisitor tv = new BindingsTargetVisitor(elements);
        
        for (Binding<?> b : elements.getBindings().values()) {
            Key<?> key = b.acceptTargetVisitor(tv);
            if (key == null || !isAsyncClass(key)) continue;

            Object source = b.getSource();

            AopMethod[] methods = getAopMethods(key, elements);

            value.add(createAopClass(source, key, methods, b));
        }

        return value.isEmpty() ? null : value.toArray(new AopClass[value.size()]);
    }

    private static AopMethod[] getAopMethods(Key<?> key, ElementsBean elements) {
        if (key == null) return null;
        Class<?> clazz = key.getTypeLiteral().getRawType();

        List<AopMethod> value = new LinkedList<AopMethod>();

        for (Method method : clazz.getDeclaredMethods()) {
            boolean isAsynchronous = method.isAnnotationPresent(Asynchronous.class);

            List<MethodInterceptor> list = null;
            for (InterceptorBinding ib : elements.getInterceptors()) {
                if (!ib.getClassMatcher().matches(clazz) || !ib.getMethodMatcher().matches(method))
                    continue;

                if (list == null) list = new LinkedList<MethodInterceptor>();
                list.addAll(ib.getInterceptors());
            }

            if ((list != null && !list.isEmpty()) || isAsynchronous) {
                if (isAsynchronous) validateAsynchronousSignature(method);

                AopMethod aMethod = new AopMethod(method, isAsynchronous, list);
                value.add(aMethod);
            }
        }

        return value.isEmpty() ? null : value.toArray(new AopMethod[value.size()]);
    }

    private static boolean isAsyncClass(Key<?> key) {
        Class<?> clazz = key.getTypeLiteral().getRawType();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Asynchronous.class)) return true;
        }
        return false;
    }

    private static void validateAsynchronousSignature(Method method) {
        if (!void.class.equals(method.getReturnType())) {
            throw new RuntimeException("Asynchronous methods must return void: " + method);
        }
        if(Modifier.isPrivate(method.getModifiers())) {
            throw new RuntimeException("Asynchronous methods must return void: " + method);
        }
    }
    
    private static <T> AopClass<T> createAopClass(Object source, Key<T> key, AopMethod[] methods, Binding<?> binding) {
        AopClass<T> aopClass = new AopClass<T>();
        aopClass.setSource(source);
        aopClass.setKey(key);
        aopClass.setMethods(methods);
        aopClass.setBindingSource(binding);

        return aopClass;
    }

    private static class BindingsTargetVisitor extends DefaultBindingTargetVisitor<Object, Key<?>> {

        private final ElementsBean elementViewer;

        BindingsTargetVisitor(ElementsBean elementViewer) {
            this.elementViewer = elementViewer;
        }

        @Override
        public Key<?> visit(LinkedKeyBinding<? extends Object> binding) {
            Binding<?> targetBinding = elementViewer.getBindings().get(binding.getLinkedKey());
            if (targetBinding == null)
                return binding.getLinkedKey();
            else
                return null; // only interested in the end target class
        }

        @Override
        public Key<?> visit(UntargettedBinding<? extends Object> binding) {
            Key<?> key = binding.getKey();
            return key;
        }
    }
}
