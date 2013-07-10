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
package iv.guice.asynchronous.impl.utils;

import com.google.inject.Key;
import com.google.inject.TypeLiteralFactory;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.InstanceBinding;

public class GuiceAsyncUtils {

    public static Object getSource() {
        return Thread.currentThread().getStackTrace()[2];
    }

    public static <T> InjectionRequest<T> requestInjection(T instance) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) instance.getClass();
        return new InjectionRequest<T>(getSource(), TypeLiteralFactory.<T> createTypeLiteral(clazz), instance);
    }

    public static <T> InstanceBinding<T> bindInstance(Key<T> key, T instance) {
        return new InstanceBindingImpl<T>(key, instance, getSource());
    }
}
