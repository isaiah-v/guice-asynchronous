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
package iv.guice.asynchronous.helpers.exceptionable;

import iv.guice.asynchronous.helpers.callbacks.Callback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Exceptionable {

    /**
     * If <code>true</code> and a {@link Callback} is present within the
     * method's parameters, the exception (or {@link Throwable}) will be
     * intercepted and passed to the callbacks.
     * 
     * @return <code>true</code> if exceptions should be intercepted and passed
     *         to the callback
     */
    public boolean isCallback() default true;
}
