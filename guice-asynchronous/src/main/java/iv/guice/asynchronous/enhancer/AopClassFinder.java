package iv.guice.asynchronous.enhancer;

import iv.guice.asynchronous.Asynchronous;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.spi.InterceptorBinding;

public class AopClassFinder {
	private AopClassFinder() {}
	
	public static AopClass<?>[] findAopClasses(ElementsBean bindings) {
		Collection<AopClass<?>> value = new ArrayList<AopClass<?>>();
		
		BindingsTargetVisitor tv = new BindingsTargetVisitor(bindings);
		for(Binding<?> b : bindings.getBindings().values()) {
			Key<?> key = b.acceptTargetVisitor(tv);
			if(key==null) continue;
			
			Object source = b.getSource();
			
			AopMethod[] methods = getAopMethods(key, bindings);
			
			value.add(createAopClass(source, key, methods));
		}
		
		return value.isEmpty() ? null : value.toArray(new AopClass[value.size()]);
	}
	
	
	private static AopMethod[] getAopMethods(Key<?> key, ElementsBean bindings) {
		if(key==null) return null;
		Class<?> clazz = key.getTypeLiteral().getRawType();
		
		List<AopMethod> value = new LinkedList<AopMethod>();
		
		for(Method method : clazz.getMethods()) {
			boolean isAsynchronous = method.isAnnotationPresent(Asynchronous.class);
			
			List<MethodInterceptor> list = null;
			for(InterceptorBinding ib : bindings.getInterceptors()) {
				if(!ib.getClassMatcher().matches(clazz) || !ib.getMethodMatcher().matches(method))
					continue;
				
				if(list==null) list = new LinkedList<MethodInterceptor>();
				list.addAll(ib.getInterceptors());
			}
			
			if((list!=null && !list.isEmpty()) || isAsynchronous) {
				if(isAsynchronous) validateAsynchronousSignature(method);
				
				AopMethod aMethod = new AopMethod(method, isAsynchronous, list);
				value.add(aMethod);
			}
		}
		
		return value.isEmpty() ? null : value.toArray(new AopMethod[value.size()]);
	}
	
	private static void validateAsynchronousSignature(Method method) {
		if(!void.class.equals(method.getReturnType())) {
			throw new RuntimeException("Asynchronous methods must return void: " + method);
		}
	}
	
	private static <T> AopClass<T> createAopClass(Object source, Key<T> key, AopMethod[] methods) {
		AopClass<T> aopClass = new AopClass<T>();
		aopClass.setSource(source);
		aopClass.setKey(key);
		aopClass.setMethods(methods);
		
		return aopClass;
	}
}
