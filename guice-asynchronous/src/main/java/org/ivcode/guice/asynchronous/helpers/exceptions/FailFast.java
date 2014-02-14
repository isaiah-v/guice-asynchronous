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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.RejectedExecutionException;

import org.ivcode.guice.asynchronous.helpers.callbacks.Callback;


/**
 * Marks a method as part of the fail-fast mechanism. This can be a method that
 * needs to fail fast or just a method that triggers the fail-fast mechanism.<br>
 * <br>
 * Frequently operations perform many asynchronous task, none of which are aware
 * of the other tasks. However, the overall operation fails if any of the tasks
 * fail. The idea behind having a fail-fast mechanism is to stop the execution
 * of an operation as soon as something goes wrong rather than allowing tasks to
 * continue executing and submitting further downstream tasks.<br>
 * <br>
 * The fail-fast mechanism is triggered when a method, that contains this
 * annotation, throws an exception. After this occurs any attempt to call a
 * method that fails fast results in a thrown exception itself and the task does
 * not executed.<br>
 * <br>
 * This mechanism's logic is defined within the {@link ExceptionsInterceptor}
 * 
 * @author Isaiah van der Elst
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FailFast {

    /**
     * If <code>true</code>, an attempt will be made to forward any thrown
     * exceptions to any {@link Callback}s within the method's parameters.<br>
     * <br>
     * Having this value equal to <code>true</code> is equivalent to adding the
     * {@link CallbackExceptions} annotation onto the method.
     * 
     * @return <code>true</code> if an attempt should be made to forward
     *         exceptions to any {@link Callback}s within the method's
     *         parameters
     */
    public boolean isCallback() default true;

    /**
     * Enables/Disables the fail-fast mechanism on a method. If
     * <code>true</code> and a prior exception has been thrown from a method
     * with the {@link FailFast} annotation, the fail-fast exception will be
     * thrown immediately upon being called and the method's execution will be
     * skipped<br>
     * <br>
     * Setting this value to <code>false</code> will allow a method to trigger
     * the fail-fast mechanism without it ever failing fast itself.
     * 
     * @return <code>true</code> if a method should fail-fast when a prior
     *         exception has been thrown.
     */
    public boolean isFailFast() default true;

    /**
     * Defines the {@link Throwable} to throw when a task is called and the
     * fail-fast mechanism has been triggered. The class must have a single
     * string constructor to sets the error message.
     * 
     * @return The {@link Throwable} type to thrown when an operation is skipped
     *         due to it failing fast. (Default:
     *         {@link RejectedExecutionException}.class)
     * @see #message()
     */
    public Class<? extends Throwable> type() default RejectedExecutionException.class;

    /**
     * Defines the error message assigned to the {@link Throwable} when
     * 
     * @return The error message given to the when an operation is skipped due
     *         to it failing fast
     * @see #type()
     */
    public String message() default "FAILFAST: Operation Skipped";
}
