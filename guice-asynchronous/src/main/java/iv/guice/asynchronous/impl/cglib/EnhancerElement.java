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
package iv.guice.asynchronous.impl.cglib;

import iv.guice.asynchronous.impl.bindingclass.BindingClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import net.sf.cglib.proxy.Enhancer;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.TypeLiteralFactory;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;

import static iv.guice.asynchronous.impl.utils.GuiceAsyncUtils.*;

/**
 * Used to bind {@link #getKey()} to the classes produced by
 * {@link #getEnhancer()}
 * 
 * @author isaiah
 * 
 * @param <T>
 *            the binding type
 */
public class EnhancerElement<T> implements Element {

    private final BindingClass<T> aopClass;
    private final Enhancer enhancer;

    public EnhancerElement(BindingClass<T> aopClass, Enhancer enhancer) {
        this.aopClass = aopClass;
        this.enhancer = enhancer;
    }

    public Object getSource() {
        return aopClass.getSource();
    }

    public void applyTo(Binder binder) {
        setWithSource(binder);
        
        // create the private binder
        PrivateBinder privateBinder = binder.newPrivateBinder();

        // bind the Enhancer to the private binder
        bindEnhancer(privateBinder);
        
        bindObjectFactory(privateBinder);
        
        // bind the key to the Enhancer's provider to the provate binder
        TypeLiteral<EnhancerProvider<T>> type = createEnhancerProviderType();
        ScopedBindingBuilder sbb = bindEnhancerProvider(privateBinder, type);
        
        // bind the key's scope to the original scope
        bindScope(binder, sbb);
        
        // Expose the key 
        privateBinder.expose(getKey());
    }
    
    private TypeLiteral<EnhancerProvider<T>> createEnhancerProviderType() {
        Type mainType = EnhancerProvider.class;
        Type genaricType = this.getKey().getTypeLiteral().getType();
        return TypeLiteralFactory.createParameterizedTypeLiteral(mainType, genaricType);
    }
    
    private void setWithSource(Binder binder) {
        Object source = getSource();
        if(source!=null) binder.withSource(source);
    }
    
    private void bindEnhancer(Binder binder) {
        binder.bind(Enhancer.class).toInstance(enhancer);
    }
    private ScopedBindingBuilder bindEnhancerProvider(Binder binder, TypeLiteral<EnhancerProvider<T>> type) {
        return binder.bind(getKey()).toProvider(type);
    }
    
    private void bindScope(Binder binder, ScopedBindingBuilder scopedBindingBuilder) {
        ScopeBinderVisitor sbv = new ScopeBinderVisitor(scopedBindingBuilder);
        aopClass.getBindingSource().acceptScopingVisitor(sbv);
    }

    public Key<T> getKey() {
        return aopClass.getKey();
    }

    public Enhancer getEnhancer() {
        return enhancer;
    }

    public <V> V acceptVisitor(ElementVisitor<V> visitor) {
        // not needed
        throw new UnsupportedOperationException();
    }
    
    public static <T> EnhancerElement<T> createEnhancerElement(BindingClass<T> aopClass, Enhancer enhancer) {
        return new EnhancerElement<T>(aopClass, enhancer);
    }
    
    public void bindObjectFactory(Binder binder) {
        Class<?>[] argumentTypes = aopClass.getConstructor().getArgumentTypes();
        Key<?>[] argumentKeys = aopClass.getConstructor().getArgumentKeys();
        
        assert argumentTypes.length==argumentKeys.length;
        
        Provider<?>[] provider = new Provider[argumentKeys.length];
        for(int i=0; i<provider.length; i++) {
            Key<?> key = argumentKeys[i];
            
            Provider<?> p = binder.getProvider(key);
            provider[i] = p;
        }
        
        binder.bind(Class[].class).toInstance(argumentTypes);
        binder.bind(Provider[].class).toInstance(provider);
    }
    
    private class ScopeBinderVisitor implements BindingScopingVisitor<Void> {

        private final ScopedBindingBuilder scopedBindingBuilder;
        
        ScopeBinderVisitor(ScopedBindingBuilder scopedBindingBuilder) {
            this.scopedBindingBuilder = scopedBindingBuilder;
        }
        
        public Void visitEagerSingleton() {
            scopedBindingBuilder.asEagerSingleton();
            return null;
        }

        public Void visitScope(Scope scope) {
            scopedBindingBuilder.in(scope);
            return null;
        }

        public Void visitScopeAnnotation(Class<? extends Annotation> scopeAnnotation) {
            scopedBindingBuilder.in(scopeAnnotation);
            return null;
        }

        public Void visitNoScoping() {
            Annotation a = findScopeAnnotation(getRawType(getKey()).getAnnotations());
            if(a!=null) visitScopeAnnotation(a.annotationType());
            return null;
        }
    }
}
