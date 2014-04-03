package org.ivcode.guice.asynchronous.internal.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Collection;

import org.ivcode.guice.asynchronous.AsynchronousAnnotatedBindingBuilder;
import org.ivcode.guice.asynchronous.AsynchronousBindingBuilder;
import org.ivcode.guice.asynchronous.AsynchronousLinkedBindingBuilder;
import org.ivcode.guice.asynchronous.internal.binder.InterceptorElement;
import org.ivcode.guice.asynchronous.internal.binding.AnnotationScopeBinding;
import org.ivcode.guice.asynchronous.internal.binding.Binding;
import org.ivcode.guice.asynchronous.internal.binding.BindingFactory;
import org.ivcode.guice.asynchronous.internal.binding.EagerSingletonScopeBinding;
import org.ivcode.guice.asynchronous.internal.binding.InstanceScopeBinding;
import org.ivcode.guice.asynchronous.internal.binding.ScopeBinding;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.ScopedBindingBuilder;

class MyAsynchronousAnnotatedBindingBuilder<S> implements AsynchronousAnnotatedBindingBuilder<S> {

	private final SourceKeyBuilder<S> sourceKeyBuilder;
	private TargetAsynchronousLinkedBindingBuilder<S, ? extends S> target;
	
	MyAsynchronousAnnotatedBindingBuilder(SourceKeyBuilder<S> sourceKeyBuilder) {
		this.sourceKeyBuilder = sourceKeyBuilder;
	}
	
	public <T extends S> AsynchronousBindingBuilder<T> to(Key<T> targetKey) {
		return createTarget(targetKey);
	}

	public <T extends S> AsynchronousBindingBuilder<T> to(TypeLiteral<T> implementation) {
		return to(Key.get(implementation));
	}

	public <T extends S> AsynchronousBindingBuilder<T> to(Class<T> implementation) {
		return to(Key.get(implementation));
	}

	public ScopedBindingBuilder withConstructor(Constructor<S> c) {
		AsynchronousBindingBuilder<S> abb = createTarget(sourceKeyBuilder.build());
		return abb.withConstructor(c);
	}

	public void in(Class<? extends Annotation> scopeAnnotation) {
		ScopedBindingBuilder sbb = createTarget(sourceKeyBuilder.build());
		sbb.in(scopeAnnotation);
	}

	public void in(Scope scope) {
		ScopedBindingBuilder sbb = createTarget(sourceKeyBuilder.build());
		sbb.in(scope);
	}

	public void asEagerSingleton() {
		ScopedBindingBuilder sbb = createTarget(sourceKeyBuilder.build());
		sbb.asEagerSingleton();
	}

	public AsynchronousLinkedBindingBuilder<S> annotatedWith(Annotation annotation) {
		sourceKeyBuilder.annotatedWith(annotation);
		return this;
	}

	public AsynchronousLinkedBindingBuilder<S> annotatedWith(Class<? extends Annotation> annotationType) {
		sourceKeyBuilder.annotatedWith(annotationType);
		return this;
	}
	
	private <T extends S> AsynchronousBindingBuilder<T> createTarget(Key<T> key) {
		if(target!=null) { throw new IllegalStateException(); }
		TargetAsynchronousLinkedBindingBuilder<S, T> mytarget = new TargetAsynchronousLinkedBindingBuilder<S, T>(key);
		
		target = mytarget;
		
		return mytarget;
	}
	
	Binding createBinding(BindingFactory bindingFactory, Binder binder, Object source, Collection<InterceptorElement> interceptors) {
		Key<S> sourceKey = this.sourceKeyBuilder.build();
		if(this.target==null) {
			createTarget(sourceKey);
		}
		
		return createBinding(bindingFactory, binder, source, interceptors, this.sourceKeyBuilder.build(), target);
	}
	
	private static <S, T extends S> Binding createBinding(BindingFactory factory, Binder binder, Object source, Collection<InterceptorElement> interceptors, Key<S> sourceKey, TargetAsynchronousLinkedBindingBuilder<S, T> target) {
		
		Key<T> targetKey = target==null ? null : target.targetKey;
		Constructor<T> constructor = target==null ? null : target.constructor;
		ScopeBinding scopeBinding = target==null ? null : target.scopeBinding;
		
		
		return factory.createAsynchronousBinding(binder, sourceKey, targetKey, constructor, interceptors, scopeBinding, source);
	}
	
	private static class TargetAsynchronousLinkedBindingBuilder<S, T extends S> implements AsynchronousBindingBuilder<T> {
		
		private final Key<T> targetKey;
		private Constructor<T> constructor;
		private ScopeBinding scopeBinding;
		
		TargetAsynchronousLinkedBindingBuilder(Key<T> key) {
			this.targetKey = key;
		}

		public void in(Class<? extends Annotation> scopeAnnotation) {
			this.scopeBinding = new AnnotationScopeBinding(scopeAnnotation);
		}

		public void in(Scope scope) {
			this.scopeBinding = new InstanceScopeBinding(scope);
		}

		public void asEagerSingleton() {
			this.scopeBinding = new EagerSingletonScopeBinding();
		}

		public ScopedBindingBuilder withConstructor(Constructor<T> c) {
			this.constructor = c;
			return this;
		}
	}
}
