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
package iv.guice.asynchronous.helpers.exceptions;

import java.lang.reflect.Method;

import iv.guice.asynchronous.impl.cglib.StacktracePruner;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import iv.guice.asynchronous.helpers.callbacks.Callback;

/**
 * A helper intercepter intended to aid in exception handling and processing for
 * asynchronous tasks. Currently it supports two mechanisms:<br/>
 * <br/>
 * 1) {@link CallbackExceptions}: forwards uncaught exceptions to
 * {@link Callback}s<br/>
 * 2) {@link FailFast}: helps operations fail-fast when an asynchronous task
 * throws an exception<br/>
 * 
 * @author Isaiah van der Elst
 */
public class ExceptionsInterceptor implements MethodInterceptor {

    private volatile int thrownExceptions = 0;

    public Object invoke(MethodInvocation inv) throws Throwable {

        FailFast failfast = getFailFast(inv);

        try {
            if (isFailFast(failfast) && thrownExceptions > 0) FailFastHandler.failFast(getFailFastType(failfast), getMessage(failfast));

            return inv.proceed();
        }
        catch (final Throwable th) {
            StacktracePruner.pruneStacktrace(th);

            incrementExceptions(failfast);
            if (isCallback(inv, failfast)) callback(th, inv.getMethod(), inv.getArguments());

            throw th;
        }
    }

    private void callback(Throwable th, Method method, Object... args) throws Throwable {
        CallbackExceptionsHandler.callbackException(th, method, args);
    }

    private void incrementExceptions(FailFast failfast) {
        if (failfast != null) thrownExceptions++;
    }

    private FailFast getFailFast(MethodInvocation inv) {
        return inv.getStaticPart().getAnnotation(FailFast.class);
    }

    private boolean isFailFast(FailFast failFast) {
        return failFast == null ? false : failFast.isFailFast();
    }

    private boolean isCallback(MethodInvocation inv, FailFast failfast) {
        return isCallback(inv) || isCallback(failfast);
    }

    private boolean isCallback(FailFast failFast) {
        return failFast == null ? false : failFast.isCallback();
    }

    private boolean isCallback(MethodInvocation inv) {
        return inv.getStaticPart().isAnnotationPresent(CallbackExceptions.class);
    }

    private Class<? extends Throwable> getFailFastType(FailFast failFast) {
        return failFast == null ? null : failFast.type();
    }

    private String getMessage(FailFast failFast) {
        return failFast == null ? null : failFast.message();
    }
}
