package org.ivcode.guice.asynchronous.internal.binding;

import java.lang.reflect.Type;

import org.ivcode.guice.asynchronous.internal.proxy.EnhancerProvider;
import org.ivcode.guice.asynchronous.internal.proxy.factory.FactoryInvocationHandler;
import org.ivcode.guice.asynchronous.internal.proxy.factory.FactoryProxyProvider;
import org.ivcode.guice.asynchronous.internal.utils.AssistedProvider;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.TypeLiteralFactory;

public class Utils {
	static <T> TypeLiteral<EnhancerProvider<T>> createEnhancerProviderType(Key<T> key) {
        Type mainType = EnhancerProvider.class;
        Type genaricType = key.getTypeLiteral().getType();
        return TypeLiteralFactory.createParameterizedTypeLiteral(mainType, genaricType);
    }
	
	static <T> TypeLiteral<AssistedProvider<T>> createAssistedProviderType(Key<T> key) {
        Type mainType = AssistedProvider.class;
        Type genaricType = key.getTypeLiteral().getType();
        return TypeLiteralFactory.createParameterizedTypeLiteral(mainType, genaricType);
    }
	
	static <T> TypeLiteral<FactoryProxyProvider<T>> createFactoryProxyProviderType(Key<T> key) {
        Type mainType = FactoryProxyProvider.class;
        Type genaricType = key.getTypeLiteral().getType();
        return TypeLiteralFactory.createParameterizedTypeLiteral(mainType, genaricType);
    }
	
	static <T> TypeLiteral<FactoryInvocationHandler<T>> createFactoryInvocationHandler(Key<T> key) {
		Type mainType = FactoryInvocationHandler.class;
        Type genaricType = key.getTypeLiteral().getType();
        return TypeLiteralFactory.createParameterizedTypeLiteral(mainType, genaricType);
	}
}