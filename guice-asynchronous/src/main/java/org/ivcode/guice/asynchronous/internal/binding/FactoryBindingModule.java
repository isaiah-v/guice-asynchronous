package org.ivcode.guice.asynchronous.internal.binding;

import static org.ivcode.guice.asynchronous.internal.binding.Utils.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClass;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerData;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerProvider;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.TypeLiteralFactory;

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
		binder.install(new ConstructorProvidersModule(asyncClass));
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
        return TypeLiteralFactory.createParameterizedTypeLiteral(Map.class, Method.class, int[].class);
    }
	
	@SuppressWarnings("unchecked")
	private <T> void bindClass(Binder binder, Key<T> key) {
		binder.bind(createClassType(key)).toInstance((Class<T>)key.getTypeLiteral().getRawType());
	}
	
	private <T> TypeLiteral<Class<T>> createClassType(Key<T> key) {
        return TypeLiteralFactory.createParameterizedTypeLiteral(Class.class, key.getTypeLiteral().getRawType());
    }
}
