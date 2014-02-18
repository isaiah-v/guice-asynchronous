package org.ivcode.guice.asynchronous.impl.bindingclass;

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.ivcode.guice.asynchronous.impl.binder.InterceptorBean;

import com.google.inject.Key;

public interface BindingClassFactory {
    public <T> BindingClass<T> getBindingClass(Key<T> key, Constructor<? extends T> constructor, Collection<InterceptorBean> interceptors);
}
