package org.ivcode.guice.asynchronous.internal.processor;

import java.lang.annotation.Annotation;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;

class AnnotationBuilderClass implements AnnotatedBuilder {

	private final Class<? extends Annotation> annotation;
	
	AnnotationBuilderClass( Class<? extends Annotation> annotation) {
		this.annotation = annotation;
	}
	
	public <T> Key<T> createKey(Class<T> type) {
		return Key.get(type, annotation);
	}

	public <T> Key<T> createKey(TypeLiteral<T> type) {
		return Key.get(type, annotation);
	}

}
