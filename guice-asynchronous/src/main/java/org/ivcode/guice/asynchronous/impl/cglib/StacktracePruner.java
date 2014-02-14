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
package org.ivcode.guice.asynchronous.impl.cglib;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ivcode.guice.asynchronous.impl.cglib.InterceptorStackCallback.InterceptedMethodInvocation;
import org.ivcode.guice.asynchronous.impl.manager.AsynchronousManager;

import net.sf.cglib.proxy.MethodProxy;

public class StacktracePruner {

    private StacktracePruner() {}

    private static final Set<String> AOP_INTERNAL_CLASSES = new HashSet<String>(Arrays.asList(InterceptorStackCallback.class.getName(), InterceptedMethodInvocation.class.getName(),
            MethodProxy.class.getName(), AsynchronusInterceptor.class.getName(), AsynchronusInterceptor.TaskExecutor.class.getName(), DirectInterceptor.class.getName(),
            AsynchronousManager.class.getName(), AsynchronousManager.Task.class.getName()));

    /**
     * Removes stacktrace elements related to AOP internal mechanics from the
     * throwable's stack trace and any causes it may have.
     */
    public static void pruneStacktrace(Throwable throwable) {
        for (Throwable t = throwable; t != null; t = t.getCause()) {
            StackTraceElement[] stackTrace = t.getStackTrace();
            List<StackTraceElement> pruned = new ArrayList<StackTraceElement>();
            for (StackTraceElement element : stackTrace) {
                String className = element.getClassName();
                if (!AOP_INTERNAL_CLASSES.contains(className) && !className.contains("$EnhancerByGuiceAsynchronous$")) {
                    pruned.add(element);
                }
            }
            t.setStackTrace(pruned.toArray(new StackTraceElement[pruned.size()]));
        }
    }
}
