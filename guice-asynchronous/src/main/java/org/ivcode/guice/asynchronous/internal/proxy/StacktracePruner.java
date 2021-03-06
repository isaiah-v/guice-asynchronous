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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ivcode.guice.asynchronous.internal.utils.InternalClasses;

public class StacktracePruner {

    private static final Set<String> AOP_INTERNAL_CLASSES;
    
    static {
    	Set<String> names = new HashSet<String>();
    	
    	for(Class<?> clazz : InternalClasses.getInternalClasses()) {
    		names.add(clazz.getName());
    	}
    	
    	AOP_INTERNAL_CLASSES = names;
    }
    
    private StacktracePruner() {
    }
    
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
