package org.ivcode.guice.asynchronous.impl.bindings;

import java.lang.annotation.Annotation;

import com.google.inject.binder.ScopedBindingBuilder;

public class AnnotationScopeBinding implements ScopeBinding {

	private final Class<? extends Annotation> scopeClass;
	
	public AnnotationScopeBinding(Class<? extends Annotation> scopeClass) {
		this.scopeClass = scopeClass;
	}
	
	public void applyTo(ScopedBindingBuilder scopedBindingBuilder) {
		scopedBindingBuilder.in(scopeClass);
	}

}
