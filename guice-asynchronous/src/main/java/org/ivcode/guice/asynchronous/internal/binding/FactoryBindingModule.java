package org.ivcode.guice.asynchronous.internal.binding;

import static org.ivcode.guice.asynchronous.internal.binding.Utils.*;
import static org.ivcode.guice.asynchronous.internal.utils.TypeLiteralFactory.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClass;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerData;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerProvider;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class FactoryBindingModule implements Module {
	
	private final Key<?> factoryKey;
	private final Key<?> asycKey;
	
	private final Map<Method, int[]> methodMapping;
	private final AsynchronousClass<?> asyncClass;
	private final EnhancerData enhancerData;
	
	public FactoryBindingModule(Key<?> factoryKey, Key<?> asycKey,
			Map<Method, int[]> methodMapping, AsynchronousClass<?> asyncClass,
			EnhancerData enhancerData) {
		this.factoryKey = factoryKey;
		this.asycKey = asycKey;
		this.methodMapping = methodMapping;
		this.asyncClass = asyncClass;
		this.enhancerData = enhancerData;
	}

	public void configure(Binder binder) {
		Collection<Key<?>> ignoreSet = createIgnoreSet(methodMapping, asyncClass.getConstructor().getArgumentKeys());
		
		binder.install(new ConstructorProvidersModule(asyncClass, ignoreSet));
		binder.install(new EnhancerDataModule(enhancerData));
		
		bindAssistedProvider(binder, asycKey);
		
		bindClass(binder, factoryKey);
		binder.bind(createMappingType()).toInstance(methodMapping);
		binder.bind(InvocationHandler.class).to(createFactoryInvocationHandler(asycKey));
		
		bindFactory(binder, factoryKey);
	}
	
	private <T> void bindAssistedProvider(Binder binder, Key<T> key) {		
		TypeLiteral<EnhancerProvider<T>> type = createEnhancerProviderType(key);
        binder.bind(createAssistedProviderType(key)).to(type);
    }
	
	private <T> void bindFactory(Binder binder, Key<T> key) {
		binder.bind(key).toProvider(createFactoryProxyProviderType(key));
	}
	
	private <T> TypeLiteral<Map<Method,int[]>> createMappingType() {
        return createParameterizedTypeLiteral(Map.class, Method.class, int[].class);
    }
	
	@SuppressWarnings("unchecked")
	private <T> void bindClass(Binder binder, Key<T> key) {
		binder.bind(createClassType(key)).toInstance((Class<T>)key.getTypeLiteral().getRawType());
	}
	
	private <T> TypeLiteral<Class<T>> createClassType(Key<T> key) {
        return createParameterizedTypeLiteral(Class.class, key.getTypeLiteral().getRawType());
    }
	
	/**
	 * Figures out what parameter providers are not needed for the factory  
	 * @param methodMapping
	 * 		the parameter mappings
	 * @param keys
	 * 		Parameter Keys
	 * @return
	 * 		A collection of keys that we don't need to provider to
	 */
	private Collection<Key<?>> createIgnoreSet(Map<Method, int[]> methodMapping, Key<?>[] keys) {
		Key<?>[] myKeys = keys.clone();
		
		for(Map.Entry<Method, int[]> entry : methodMapping.entrySet()) {
			int[] mappings = entry.getValue();
			for(int i=0; i<mappings.length; i++) {
				if(mappings[i]>=0) { continue; }
				myKeys[i] = null;
			}
		}
		
		return new HashSet<Key<?>>(Arrays.asList(myKeys));
	}
}
