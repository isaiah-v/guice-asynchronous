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
package org.ivcode.guice.asynchronous.helpers.callbacks;

/**
 * A generic asynchronous callback.
 * 
 * @author Isaiah van der Elst
 * @param <T>
 *      The return type
 */
public interface Callback<T> {
    
    /**
     * Called on the successful completion of an asynchronous operation
     * @param result
     *      the result of the asynchronous operation
     */
    public void onSuccess(T result);
    
    /**
     * Called on the event that an asynchronous operation fails
     * @param th
     *      The cause of the failed operation
     */
    public void onFail(Throwable th);
}
