package org.ivcode.guice.asynchronous.internal.proxy.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.ivcode.guice.asynchronous.internal.utils.AssistedProvider;

import com.google.inject.Inject;

public class FactoryInvocationHandler<T> implements InvocationHandler {

	private final AssistedProvider<T> provider;
	private final Map<Method, int[]> indexMap;
	
	@Inject
	public FactoryInvocationHandler(AssistedProvider<T> provider, Map<Method, int[]> indexMap, String toString) {
		this.provider = provider;
		this.indexMap = indexMap;
	}
	
	public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
		
		int[] indexMapping = indexMap.get(method);
		if(indexMapping!=null) {
			return createInstance(indexMapping, arguments);
		} 
		
		if(Object.class.equals(method.getDeclaringClass())) {
			return method.invoke(this, arguments);
		}
		
		throw new UnsupportedOperationException();
	}
	
	private T createInstance(int[] indexMapping, Object[] arguments) {
		final int size = provider.getArgumentCount();
		assert indexMapping.length==size;
		
		Object[] myArgs = new Object[size];
		
		for(int i=0; i<size; i++) {
			int index = indexMapping[i];
			myArgs[i] = index<0 ? provider.getArgumentInstance(i) : arguments[index];
		}
		
		return provider.get(myArgs);
	}
	
	public String toString() {
		return "factoryProxy";
	}
}
