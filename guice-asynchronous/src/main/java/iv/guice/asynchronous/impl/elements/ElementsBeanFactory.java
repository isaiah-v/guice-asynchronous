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
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.InterceptorBinding;

public class ElementsBeanFactory {

    private ElementsBeanFactory() {}

    public static ElementsBean createElementsBean(Module... modules) {
        return createElementsBean(Elements.getElements(modules));
    }

    public static ElementsBean createElementsBean(Collection<Element> elements) {
        ElementsBeanVisitor visitor = new ElementsBeanVisitor();
        for (Element e : elements) {
            e.acceptVisitor(visitor);
        }
        return visitor.asElementsBean();
    }

    private static class ElementsBeanVisitor extends DefaultElementVisitor<Void> {

        private Collection<InterceptorBinding> interceptors = new ArrayList<InterceptorBinding>();
        private Collection<Element> others = new ArrayList<Element>();
        private Map<Key<?>, Binding<?>> bindings = new HashMap<Key<?>, Binding<?>>();

        @Override
        public <T> Void visit(Binding<T> binding) {
            bindings.put(binding.getKey(), binding);
            return null;
        }

        @Override
        public Void visit(InterceptorBinding interceptorBinding) {
            interceptors.add(interceptorBinding);
            return null;
        }

        @Override
        protected Void visitOther(Element element) {
            others.add(element);
            return null;
        }

        public ElementsBean asElementsBean() {
            return new ElementsBean(bindings, interceptors, others);
        }
    }
}
