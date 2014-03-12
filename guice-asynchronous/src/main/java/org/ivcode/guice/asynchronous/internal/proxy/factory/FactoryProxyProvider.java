package org.ivcode.guice.asynchronous.internal.proxy.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class FactoryProxyProvider<T> implements Provider<T> {
	
	private final InvocationHandler invocationHander;
	private final Class<?>[] proxyInterfaces;
	private final ClassLoader classLoader;
	
	@Inject
	public FactoryProxyProvider(InvocationHandler invocationHander, Class<T> proxyInterface) {
		this.invocationHander = invocationHander;
		this.proxyInterfaces = new Class<?>[] {proxyInterface};
		this.classLoader = this.getClass().getClassLoader();
	}
	
	public T get() {
		@SuppressWarnings("unchecked")
		T value = (T) Proxy.newProxyInstance(classLoader, proxyInterfaces, invocationHander);
		
		return value;
	}

}
