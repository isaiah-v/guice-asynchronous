package iv.guice.asynchronous.impl.aopclass;

import iv.guice.asynchronous.Asynchronous;
import iv.guice.asynchronous.impl.elements.ElementSplice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.InterceptorBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.UntargettedBinding;

public class AopClassFinder {
	private AopClassFinder() {}
	
	public static AopClass<?>[] findAopClasses(ElementSplice elements) {
		Collection<AopClass<?>> value = new ArrayList<AopClass<?>>();
		
		BindingsTargetVisitor tv = new BindingsTargetVisitor(elements);
		for(Binding<?> b : elements.getBindings().values()) {
			Key<?> key = b.acceptTargetVisitor(tv);
			if(key==null) continue;
			
			Object source = b.getSource();
			
			AopMethod[] methods = getAopMethods(key, elements);
			
			value.add(createAopClass(source, key, methods));
		}
		
		return value.isEmpty() ? null : value.toArray(new AopClass[value.size()]);
	}
	
	
	private static AopMethod[] getAopMethods(Key<?> key, ElementSplice elements) {
		if(key==null) return null;
		Class<?> clazz = key.getTypeLiteral().getRawType();
		
		List<AopMethod> value = new LinkedList<AopMethod>();
		
		for(Method method : clazz.getMethods()) {
			boolean isAsynchronous = method.isAnnotationPresent(Asynchronous.class);
			
			List<MethodInterceptor> list = null;
			for(InterceptorBinding ib : elements.getInterceptors()) {
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
	
	private static class BindingsTargetVisitor extends DefaultBindingTargetVisitor<Object, Key<?>> {
		private final ElementSplice elementViewer;
		
		BindingsTargetVisitor(ElementSplice elementViewer) {
			this.elementViewer = elementViewer;
		}

		@Override
		public Key<?> visit(LinkedKeyBinding<? extends Object> binding) {
			Binding<?> targetBinding = elementViewer.getBindings().get(binding.getLinkedKey());
			if(targetBinding==null)
				return binding.getLinkedKey();
			else
				return null; // only interested in the end target class
		}

		@Override
		public Key<?> visit(UntargettedBinding<? extends Object> binding) {
			Key<?> key = binding.getKey();
			return key;
		}
	}

}