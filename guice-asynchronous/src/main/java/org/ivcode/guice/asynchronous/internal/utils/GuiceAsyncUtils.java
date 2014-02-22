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
package org.ivcode.guice.asynchronous.internal.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.ScopeAnnotation;

public class GuiceAsyncUtils {

	/** The thread name prefix for the internal thread factory */
	private static final String THREAD_NAME_PREFIX = "guice-asynchronous";
	
    public static Object getSource() {
        return Thread.currentThread().getStackTrace()[2];
    }   

    public static <T> Annotation findScopeAnnotation(Key<T> key) {
    	return findScopeAnnotation(getRawType(key).getDeclaredAnnotations());
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
    
    /**
	 * Creates an executor service when one is not passed into the
	 * {@link #asynchronize(ExecutorService, Module...)} method
	 * 
	 * @return The default executor service
	 */
	public static ExecutorService createDefaultExecutor() {
		ThreadFactory threadFactory = new MyThreadFactory(THREAD_NAME_PREFIX + " : ", true);
		return Executors.newCachedThreadPool(threadFactory);
	}

	@Override
	public String toString() {
		return "GuiceAsyncUtils";
	}
}
