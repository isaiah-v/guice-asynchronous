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

import iv.guice.asynchronous.impl.bindingclass.BindingClass;
import iv.guice.asynchronous.impl.bindingclass.BindingMethod;
import iv.guice.asynchronous.impl.manager.ExceptionListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;


import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.NoOp;

/**
 * Creates asynchronous {@link Enhancer} objects.
 * 
 * @author isaiah
 */
public class EnhancerFactory {

    private static final NamingPolicy ASYNCHRONOUS_NAMING_POLICY = new DefaultNamingPolicy() {
        @Override
        protected String getTag() {
            return "ByGuiceAsynchronous";
        }
    };

    /**
     * Creates an asynchronous {@link Enhancer} based on the given
     * {@link BindingClass}
     * 
     * @param executor
     *            The executor used to run asynchronous tasks
     * @param aopClass
     *            The aop class that defines the executor's structure
     * @return A new asynchronous {@link Enhancer} based on the given
     *         {@link BindingClass}
     */
    public static Enhancer createEnhancer(Executor executor, ExceptionListener exceptionListener, BindingClass<?> aopClass) {
        Class<?> clazz = aopClass.getKey().getTypeLiteral().getRawType();

        Enhancer enhancer = new Enhancer();

        enhancer.setNamingPolicy(ASYNCHRONOUS_NAMING_POLICY);
        enhancer.setSuperclass(clazz);
        enhancer.setUseFactory(false);
        enhancer.setClassLoader(clazz.getClassLoader());

        Map<Method, Integer> filterMap = new HashMap<Method, Integer>();
        List<Callback> callbackList = new ArrayList<Callback>();

        @SuppressWarnings("rawtypes")
        List<Class> typeList = new ArrayList<Class>();

        // NoOp at index=0
        callbackList.add(new BasicNoOp());
        typeList.add(NoOp.class);

        for (BindingMethod method : aopClass.getMethods()) {
            if (method == null) continue;

            List<org.aopalliance.intercept.MethodInterceptor> interceptors = method.getInterceptors();

            MethodInterceptor mi = interceptors == null ? new DirectInterceptor() : new InterceptorStackCallback(method.getMethod(), interceptors);
            if (method.isAsynchronous()) mi = new AsynchronusInterceptor(executor, exceptionListener, mi);

            boolean b1 = callbackList.add(mi);
            boolean b2 = typeList.add(MethodInterceptor.class);
            assert b1 && b2;

            Object o = filterMap.put(method.getMethod(), callbackList.size() - 1);

            // if true, we've mapped the same method twice
            if (o != null) throw new IllegalStateException();
        }

        CallbackFilter callbackFilter = new EnhancerCallbackFilter(filterMap);
        Callback[] callbacks = callbackList.toArray(new Callback[callbackList.size()]);

        @SuppressWarnings("rawtypes")
        Class[] callbackTypes = typeList.toArray(new Class[typeList.size()]);

        enhancer.setCallbackFilter(callbackFilter);
        enhancer.setCallbacks(callbacks);
        enhancer.setCallbackTypes(callbackTypes);

        return enhancer;
    }

    /**
     * The {@link Callback} asynchronous {@link Enhancer}'s
     * 
     * @author isaiah
     */
    private static class EnhancerCallbackFilter implements CallbackFilter {

        private final Map<Method, Integer> filterMap;

        EnhancerCallbackFilter(Map<Method, Integer> filterMap) {
            this.filterMap = filterMap;
        }

        public int accept(Method method) {
            Integer i = filterMap.get(method);
            return i == null ? 0 : i;
        }
    }
}
