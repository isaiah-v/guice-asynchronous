package org.ivcode.guice.asynchronous.internal.asynchronousclass;

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.ivcode.guice.asynchronous.internal.binder.InterceptorElement;

import com.google.inject.Key;

public interface AsynchronousClassFactory {
    public <T> AsynchronousClass<T> getBindingClass(Key<T> key, Constructor<? extends T> constructor, Collection<InterceptorElement> interceptors);
}
