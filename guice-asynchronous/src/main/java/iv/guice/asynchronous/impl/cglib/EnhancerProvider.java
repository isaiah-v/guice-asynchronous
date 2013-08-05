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

import iv.guice.asynchronous.impl.utils.AssistedProvider;
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
class EnhancerProvider<T> implements AssistedProvider<T> {

    /** injects the members into the instance variable */
    @Inject
    private MembersInjector<T> membersInjector;

    /** creates an instances of <code>T</code> */
    @Inject
    private Enhancer enhancer;
    
    @Inject
    @SuppressWarnings("rawtypes")
    private Class[] argumentTypes;
    
    @Inject
    @SuppressWarnings("rawtypes")
    private Provider[] providers;

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
        return (T) enhancer.create(argumentTypes, arguments);
    }

    public int getArgumentCount() {
        return argumentTypes.length;
    }

    public Object getArgumentInstance(int index) {
        return providers[index].get();
    }

    public Class<?>[] getArgumentTypes() {
        return argumentTypes;
    }

    public Object[] getArgumentInstances() {
        final int count = getArgumentCount();
        
        Object[] arguments = new Object[count];
        for(int i=0; i<count; i++) 
            arguments[i] = getArgumentInstance(i);
        
        return arguments;
    }
}
