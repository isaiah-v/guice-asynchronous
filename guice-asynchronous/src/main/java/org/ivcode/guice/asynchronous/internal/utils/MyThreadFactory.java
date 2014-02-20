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
package org.ivcode.guice.asynchronous.internal.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class MyThreadFactory implements ThreadFactory {

    private static final int DEFAULT_PRIORITY = Thread.NORM_PRIORITY;

    private final ThreadGroup group;
    private final AtomicInteger count = new AtomicInteger(1);

    private final String namePrefix;
    private final boolean isDaemon;
    private final int priority;

    public MyThreadFactory(String prefix, boolean isDaemon, int priority) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();

        this.namePrefix = prefix;
        this.isDaemon = isDaemon;
        this.priority = priority;
    }

    public MyThreadFactory(String prefix, boolean isDaemon) {
        this(prefix, isDaemon, DEFAULT_PRIORITY);
    }

    public Thread newThread(Runnable r) {
        Thread value = new Thread(group, r, namePrefix + (count.getAndIncrement()));

        if (value.isDaemon() != isDaemon) value.setDaemon(isDaemon);
        if (value.getPriority() != priority) value.setPriority(priority);

        return value;
    }

}
