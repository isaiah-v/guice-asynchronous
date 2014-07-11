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
import org.ivcode.guice.asynchronous.helpers.callbacks.Callback;

/**
 * Forwards thrown exceptions to any {@link Callback}s within the method's
 * parameters.<br>
 * <br>
 * It some situations it's important to known when tasks throw exceptions, even
 * if it's an uncaught runtime exceptions. This mechanism will catch any thrown
 * {@link Throwable}s and forward them to all {@link Callback}s defined within
 * the method's parameters<br>
 * <br>
 * This mechanism's logic is defined within the {@link ExceptionsInterceptor}
 * 
 * @author Isaiah van der Elst
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CallbackExceptions {}
