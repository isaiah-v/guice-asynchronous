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
package org.ivcode.guice.asynchronous.internal.proxy;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.ivcode.guice.asynchronous.internal.utils.AssistedProvider;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.reflect.FastConstructor;

import com.google.inject.Inject;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;

/**
 * A {@link Provider} that wraps the {@link Enhancer}
 * 
 * @author Isaiah van der Elst
 * 
 * @param <T>
 *            The provider type
 */
public class EnhancerProvider<T> implements AssistedProvider<T> {
	
    private final FastConstructor fastConstructor;
    
    private final Callback[] callbacks;
    
    @SuppressWarnings("rawtypes")
    private final Provider[] providers;
    
    private final MembersInjector<T> membersInjector;
    
    @Inject
    public EnhancerProvider(FastConstructor fastConstructor, Callback[] callbacks, @SuppressWarnings("rawtypes") Provider[] providers, MembersInjector<T> membersInjector) {
    	this.fastConstructor = fastConstructor;
    	this.callbacks = callbacks;
    	this.providers = providers;
    	this.membersInjector = membersInjector;
    }

    public T get() {
        return injectMembers(createInstance());
    }
    
    public T get(Object[] arguments) {
        return injectMembers(createInstance(arguments));
    }

    /**
     * Injects the dependencies into the given instance variable
     * 
     * @param t
     *            the instance to inject the dependencies
     * @return the instance
     */
    private T injectMembers(T t) {
        membersInjector.injectMembers(t);
        return t;
    }
    
    private T createInstance() {
        return createInstance(getArgumentInstances()); 
    }
    
    @SuppressWarnings("unchecked")
    private T createInstance(Object[] arguments) {
    	final Class<?> enhanced = fastConstructor.getDeclaringClass();
    	
    	synchronized (enhanced) {
	    	Enhancer.registerCallbacks(enhanced, callbacks);
	    	try {
	    		return (T) fastConstructor.newInstance(arguments);
	    	} catch (InvocationTargetException e) {
	    		throw new RuntimeException(e.getTargetException());
	    	} finally {
	    		Enhancer.registerCallbacks(enhanced, null);
	    	}
    	}
    }

    public Object getArgumentInstance(int index) {
        return providers[index].get();
    }

    public int getArgumentCount() {
        return providers.length;
    }

    public Object[] getArgumentInstances() {
        final int count = getArgumentCount();
        
        Object[] arguments = new Object[count];
        for(int i=0; i<count; i++) 
            arguments[i] = getArgumentInstance(i);
        
        return arguments;
    }

	@Override
	public String toString() {
		return "EnhancerProvider [membersInjector=" + membersInjector
				+ ", fastConstructor=" + fastConstructor + ", callbacks="
				+ Arrays.toString(callbacks) + ", providers="
				+ Arrays.toString(providers) + "]";
	}
}
