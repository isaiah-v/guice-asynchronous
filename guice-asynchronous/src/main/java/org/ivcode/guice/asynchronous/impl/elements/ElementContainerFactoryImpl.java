package org.ivcode.guice.asynchronous.impl.elements;

import java.util.Arrays;

import com.google.inject.Binding;
import com.google.inject.Module;
import com.google.inject.spi.DisableCircularProxiesOption;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.Elements;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.InterceptorBinding;
import com.google.inject.spi.MembersInjectorLookup;
import com.google.inject.spi.Message;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.ProviderLookup;
import com.google.inject.spi.RequireExplicitBindingsOption;
import com.google.inject.spi.ScopeBinding;
import com.google.inject.spi.StaticInjectionRequest;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.spi.TypeListenerBinding;

public class ElementContainerFactoryImpl implements ElementContainerFactory {

    public ElementContainer createElementContainer(Module... modules) {
        ElementContainerVisitor elementContainerVisitor = new ElementContainerVisitor();
        
        for(Element e : Elements.getElements(Arrays.asList(modules))) {
            e.acceptVisitor(elementContainerVisitor);
        }
        
        return elementContainerVisitor.getElementContainer();
    }
    
    static class ElementContainerVisitor implements ElementVisitor<Void> {
        
        private ElementContainer elementContainer = new ElementContainerImpl();

        public <T> Void visit(Binding<T> arg0) {
            elementContainer.getBindings().put(arg0.getKey(), arg0);
            return null;
        }

        public Void visit(InterceptorBinding arg0) {
            elementContainer.getInterceptorBindings().add(arg0);
            return null;
        }

        public Void visit(ScopeBinding arg0) {
            elementContainer.getScopeBindings().add(arg0);
            return null;
        }

        public Void visit(TypeConverterBinding arg0) {
            elementContainer.getTypeConverterBindings().add(arg0);
            return null;
        }

        public Void visit(InjectionRequest<?> arg0) {
            elementContainer.getInjectionRequests().add(arg0);
            return null;
        }

        public Void visit(StaticInjectionRequest arg0) {
            elementContainer.getStaticInjectionRequests().add(arg0);
            return null;
        }

        public <T> Void visit(ProviderLookup<T> arg0) {
            elementContainer.getProviderLookups().add(arg0);
            return null;
        }

        public <T> Void visit(MembersInjectorLookup<T> arg0) {
            elementContainer.getMembersInjectorLookups().add(arg0);
            return null;
        }

        public Void visit(Message arg0) {
            elementContainer.getMessages().add(arg0);
            return null;
        }

        public Void visit(PrivateElements arg0) {
            elementContainer.getPrivateElements().add(arg0);
            return null;
        }

        public Void visit(TypeListenerBinding arg0) {
            elementContainer.getTypeListenerBindings().add(arg0);
            return null;
        }

        public Void visit(RequireExplicitBindingsOption arg0) {
            elementContainer.getRequireExplicitBindingsOptions().add(arg0);
            return null;
        }

        public Void visit(DisableCircularProxiesOption arg0) {
            elementContainer.getDisableCircularProxiesOptions().add(arg0);
            return null;
        }
        
        public ElementContainer getElementContainer() {
            return elementContainer;
        }
    }

}
