package org.ivcode.guice.asynchronous.impl.bindings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Collection;

import org.ivcode.guice.asynchronous.impl.binder.InterceptorBean;
import org.ivcode.guice.asynchronous.impl.bindingclass.BindingClass;
import org.ivcode.guice.asynchronous.impl.bindingclass.BindingClassFactory;
import org.ivcode.guice.asynchronous.impl.cglib.EnhancerData;
import org.ivcode.guice.asynchronous.impl.cglib.EnhancerFactory;
import org.ivcode.guice.asynchronous.impl.cglib.EnhancerProvider;
import org.ivcode.guice.asynchronous.impl.utils.GuiceAsyncUtils;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.reflect.FastConstructor;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.TypeLiteralFactory;
import com.google.inject.binder.ScopedBindingBuilder;

public class AsynchronousBinding<T> implements Binding  {
	
	private final BindingClassFactory bindingClassFactory;
	private final EnhancerFactory enhancerFactory;
	
	private final Key<T> key;
	private final Constructor<? extends T> constructor;
	private final Collection<InterceptorBean> interceptors;
	private final ScopeBinding scopeBinding;
	private final Object source;
	
	public AsynchronousBinding(Key<T> key, Constructor<? extends T> c, Collection<InterceptorBean> interceptors, ScopeBinding scopeBinding, Object source, BindingClassFactory bindingClassFactory, EnhancerFactory enhancerFactory) {
		if(key==null) { throw new NullPointerException(); }
		
		this.key = key;
		this.constructor = c;
		this.interceptors = interceptors;
		this.scopeBinding = scopeBinding;
		this.source = source;
		
		this.bindingClassFactory = bindingClassFactory;
		this.enhancerFactory = enhancerFactory;
	}
	
	public void applyTo(Binder binder) {
        final BindingClass<?> aopClass = bindingClassFactory.getBindingClass(key, constructor, interceptors);
		
		setWithSource(binder);
        
        // create the private binder
        PrivateBinder privateBinder = binder.newPrivateBinder();

        // bind the EnhancerData to the private binder
        bindEnhancerData(privateBinder, aopClass);
        
        bindObjectFactory(privateBinder, aopClass);
        
        // bind the key to the Enhancer's provider to the provate binder
        TypeLiteral<EnhancerProvider<T>> type = createEnhancerProviderType();
        ScopedBindingBuilder sbb = bindEnhancerProvider(privateBinder, type);
        
        // bind the key's scope to the original scope
        if(scopeBinding!=null) {
        	scopeBinding.applyTo(sbb);
        } else {
        	Annotation a = GuiceAsyncUtils.findScopeAnnotation(key);
        	if(a!=null) { sbb.in(a.annotationType()); }
        }
        
        // Expose the key 
        privateBinder.expose(key);
    }
	
	private void setWithSource(Binder binder) {
        if(source!=null) binder.withSource(source);
    }
    
    private void bindEnhancerData(Binder binder, BindingClass<?> aopClass) {
    	EnhancerData ed = enhancerFactory.createEnhancer(aopClass);
    	
        binder.bind(FastConstructor.class).toInstance(ed.getFastConstructor());
        binder.bind(Callback[].class).toInstance(ed.getCallbacks());
    }
    
    private ScopedBindingBuilder bindEnhancerProvider(Binder binder, TypeLiteral<EnhancerProvider<T>> type) {
        return binder.bind(key).toProvider(type);
    }
    
    public void bindObjectFactory(Binder binder, BindingClass<?> aopClass) {
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
    
    private TypeLiteral<EnhancerProvider<T>> createEnhancerProviderType() {
        Type mainType = EnhancerProvider.class;
        Type genaricType = key.getTypeLiteral().getType();
        return TypeLiteralFactory.createParameterizedTypeLiteral(mainType, genaricType);
    }
}
