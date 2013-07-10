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
package iv.guice.asynchronous.impl.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.spi.Element;
import com.google.inject.spi.InterceptorBinding;

public class ElementsBean {

    private final Map<Key<?>, Binding<?>> bindings;
    private final Collection<InterceptorBinding> interceptors;
    private final Collection<Element> others;

    public ElementsBean(Map<Key<?>, Binding<?>> bindings, Collection<InterceptorBinding> interceptors, Collection<Element> others) {
        this.bindings = bindings;
        this.interceptors = interceptors;
        this.others = others;
    }

    public Map<Key<?>, Binding<?>> getBindings() {
        return bindings;
    }

    public Collection<InterceptorBinding> getInterceptors() {
        return interceptors;
    }

    public Collection<Element> getOthers() {
        return others;
    }

    public Collection<Element> createElementCollection() {
        int size = bindings.size() + interceptors.size() + others.size();

        Collection<Element> value = new ArrayList<Element>(size);

        value.addAll(bindings.values());
        value.addAll(interceptors);
        value.addAll(others);

        return value;
    }
}
