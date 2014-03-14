package org.ivcode.guice.asynchronous.internal.proxy.factory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ivcode.guice.asynchronous.internal.utils.GuiceAsyncUtils;

import com.google.inject.Key;

public class IndexMapFactory {
	
	public Map<Method, int[]> createIndexMap(Key<?> factoryKey, Key<?> targetkey, Key<?>... constructorKeys) {
		Class<?> factory = factoryKey.getTypeLiteral().getRawType();
		Class<?> targetType = targetkey.getTypeLiteral().getRawType();
		
		if(!factory.isInterface()) {
			throw new IllegalArgumentException("the factory class must be an interface");
		}
		if(factory.getInterfaces().length>0) {
			throw new IllegalArgumentException("the factory class cannot extend other interfaces");
		}
		
		final Map<Key<?>, List<Integer>> indexLookup = createIndexLookup(constructorKeys);
		final Map<Method, int[]> mappings = new HashMap<Method, int[]>();
		
		for(Method method : factory.getDeclaredMethods()) {
			List<Key<?>> methodKeys = GuiceAsyncUtils.createMethodKeys(factoryKey.getTypeLiteral(), method);
			
			Class<?> rtype = method.getReturnType();
			
			if(!rtype.isAssignableFrom(targetType)) {
				throw new IllegalArgumentException("methods in the factory class must return a type that is assignable from the target type : method=" + method + " : targetType=" + targetType);
			}
			
			int[] indexMap = new int[constructorKeys.length];
			Arrays.fill(indexMap, -1);
			
			for(int i=0; i<methodKeys.size(); i++) {
				Key<?> key = methodKeys.get(i);
				List<Integer> indexes = indexLookup.get(key);
				if(indexes==null) { continue; }
				
				for(Integer index : indexes) {
					indexMap[index] = i;
				}
			}
			
			mappings.put(method, indexMap);
		}
		
		return mappings;
	}
	
	private final Map<Key<?>, List<Integer>> createIndexLookup(Key<?>[] keys) {
		Map<Key<?>, List<Integer>> lookup = new HashMap<Key<?>, List<Integer>>();
		
		for(int i=0; i<keys.length; i++) {
			putIndexLookup(keys[i], i, lookup);
		}
		
		return lookup;
	}
	
	private final void putIndexLookup(Key<?> key, Integer index, Map<Key<?>, List<Integer>> lookup) {
		List<Integer> indexCollection = lookup.get(key);
		if(indexCollection==null) {
			indexCollection = new ArrayList<Integer>(1);
			lookup.put(key, indexCollection);
		}
		
		indexCollection.add(index);
	}
}
