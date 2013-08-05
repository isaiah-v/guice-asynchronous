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
package iv.guice.asynchronous.impl.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.ScopeAnnotation;
import com.google.inject.TypeLiteralFactory;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.InstanceBinding;

public class GuiceAsyncUtils {

    public static Object getSource() {
        return Thread.currentThread().getStackTrace()[2];
    }

    public static <T> InjectionRequest<T> requestInjection(T instance) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) instance.getClass();
        return new InjectionRequest<T>(getSource(), TypeLiteralFactory.<T> createTypeLiteral(clazz), instance);
    }

    public static <T> InstanceBinding<T> bindInstance(Key<T> key, T instance) {
        return new InstanceBindingImpl<T>(key, instance, getSource());
    }

    public static Annotation findScopeAnnotation(Annotation[] annotations) {
        Annotation annotation = null;

        for (Annotation a : annotations) {
            if (a == null || !a.annotationType().isAnnotationPresent(ScopeAnnotation.class)) continue;
            if (annotation != null) throw new IllegalStateException("multiple scope annotations");
            annotation = a;
        }

        return annotation;
    }
    
    public static Annotation findBindingAnnotation(Annotation[] annotations) {
        Annotation annotation = null;

        for (Annotation a : annotations) {
            if (a == null || !a.annotationType().isAnnotationPresent(BindingAnnotation.class)) continue;
            if (annotation != null) throw new IllegalStateException("multiple scope annotations");
            annotation = a;
        }

        return annotation;
    }
    
    public static <T> Class<? super T> getRawType(Key<T> key) {
        return key.getTypeLiteral().getRawType();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> findInjectConstructor(Class<T> clazz) {
        Constructor<?> constructor = null;
        for(Constructor<?> c : clazz.getConstructors()) {
            if(c.getAnnotation(Inject.class)==null) continue;
            if(constructor!=null)
                throw new IllegalStateException("mutiple constructor");
            
            constructor = c;
        }
        return (Constructor<T>) constructor;
    }
}
