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
package org.ivcode.guice.asynchronous.helpers.exceptions;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.ivcode.guice.asynchronous.helpers.callbacks.Callback;

class CallbackExceptionsHandler {

    private CallbackExceptionsHandler() {
    }

    public static int callbackException(Throwable th, Method method, Object... args) throws Throwable {
        return fail(th, method, args);
    }

    private static int fail(Throwable th, Method method, Object[] args) throws Throwable {
        Callback<?>[] callbacks = findCallbacks(method, args);

        return fail(callbacks, th);
    }

    private static Callback<?>[] findCallbacks(Method method, Object[] args) {
        List<Callback<?>> callbacks = new ArrayList<Callback<?>>();

        Class<?>[] clazzes = method.getParameterTypes();
        for (int i = 0; i < clazzes.length; i++) {
            Class<?> clazz = clazzes[i];

            if (!Callback.class.isAssignableFrom(clazz)) continue;

            Callback<?> callback = (Callback<?>) args[i];
            if (callback == null) continue;

            callbacks.add((Callback<?>) args[i]);
        }

        return callbacks.toArray(new Callback[callbacks.size()]);
    }

    private static int fail(Callback<?>[] callbacks, Throwable th) {
        int i = 0;
        for (Callback<?> callback : callbacks)
            i += fail(callback, th);

        return i;
    }

    private static int fail(Callback<?> callback, Throwable th) {
        if (callback == null) return 0;

        try {
            callback.onFail(th);
        }
        catch (Throwable thro) {
            thro.printStackTrace();
        }

        return 1;
    }
}
