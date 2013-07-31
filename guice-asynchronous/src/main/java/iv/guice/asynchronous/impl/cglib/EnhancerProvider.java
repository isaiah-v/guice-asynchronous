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
package iv.guice.asynchronous.impl.cglib;

import net.sf.cglib.proxy.Enhancer;

import com.google.inject.Inject;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;

/**
 * A {@link Provider} that wraps the {@link Enhancer}
 * 
 * @author isaiah
 * 
 * @param <T>
 *            The provider type
 */
class EnhancerProvider<T> implements Provider<T> {

    /** injects the members into the instance variable */
    @Inject
    private MembersInjector<T> membersInjector;

    /** creates an instances of <code>T</code> */
    @Inject
    private Enhancer enhancer;
    
    @Inject(optional=true)
    @SuppressWarnings("rawtypes")
    private Class[] argumentTypes;
    
    @Inject(optional=true)
    @SuppressWarnings("rawtypes")
    private Provider[] providers;

    public T get() {
        return injectMembers((T) getInstance());
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
    
    @SuppressWarnings("unchecked")
    private T getInstance() {
        if(argumentTypes==null || providers==null)
            return (T) enhancer.create();
        assert argumentTypes.length==providers.length;
        
        return (T) enhancer.create(argumentTypes, createArgumentArray()); 
    }
    
    private Object[] createArgumentArray() {
        Object[] arguments = new Object[providers.length];
        for(int i=0; i<arguments.length; i++) {
            arguments[i]=providers[i].get();
        }
        return arguments;
    }
}
