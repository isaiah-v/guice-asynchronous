package org.ivcode.guice.asynchronous.internal.binding;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClass;
import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClassFactory;
import org.ivcode.guice.asynchronous.internal.binder.InterceptorElement;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerData;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerFactory;
import org.ivcode.guice.asynchronous.internal.proxy.factory.IndexMapFactory;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;

public class FactoryBinding implements Binding {

	private final AsynchronousClassFactory asyncClassFactory;
	private final EnhancerFactory enhancerFactory;
	private final IndexMapFactory indexMapFactory;
	
	private final Binder binder;
	
	private final Key<?> factoryKey;
	private final Key<?> asyncKey;
	
	private final Collection<InterceptorElement> interceptors;
	private final Object source;
	
	public FactoryBinding(AsynchronousClassFactory asyncClassFactory,
			EnhancerFactory enhancerFactory, IndexMapFactory indexMapFactory, Binder binder, Key<?> factoryKey,
			Key<?> asyncKey,
			Collection<InterceptorElement> interceptors, Object source) {
		this.asyncClassFactory = asyncClassFactory;
		this.enhancerFactory = enhancerFactory;
		this.indexMapFactory = indexMapFactory;
		this.binder = binder;
		this.factoryKey = factoryKey;
		this.asyncKey = asyncKey;
		this.interceptors = interceptors;
		this.source = source;
	}

	public void bind() {
		AsynchronousClass<?> asyncClass = asyncClassFactory.createAsynchronousClass(asyncKey, null, interceptors);
		EnhancerData enhancerData = enhancerFactory.createEnhancer(asyncClass);
		
		Map<Method, int[]> methodMapping = indexMapFactory.createIndexMap(
												factoryKey.getTypeLiteral().getRawType(),
												asyncKey.getTypeLiteral().getRawType(),
												asyncClass.getConstructor().getArgumentKeys());
		
		setWithSource(binder);
		
		PrivateBinder privateBinder = binder.newPrivateBinder();
		
		privateBinder.install(new FactoryBindingModule(factoryKey, asyncKey, methodMapping, asyncClass, enhancerData));
		
		privateBinder.expose(factoryKey);
	}
	
	private void setWithSource(Binder binder) {
        if(source!=null) binder.withSource(source);
    }

}
