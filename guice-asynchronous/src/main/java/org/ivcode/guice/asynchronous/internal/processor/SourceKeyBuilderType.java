package org.ivcode.guice.asynchronous.internal.processor;

import java.lang.annotation.Annotation;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;

class SourceKeyBuilderType<T> implements SourceKeyBuilder<T> {

	private final TypeLiteral<T> type;
	private AnnotatedBuilder builder;
	
	SourceKeyBuilderType(TypeLiteral<T> type) {
		this.type = type;
	}
	
	public Key<T> build() {
		return builder==null ? createKey() : builder.createKey(type);
	}
	
	private Key<T> createKey() {
		return Key.get(type);
	}

	public void annotatedWith(Annotation annotation) {
		if(builder!=null) throw new IllegalStateException();
		this.builder = new AnnotationBuilderInstance(annotation);
	}

	public void annotatedWith(Class<? extends Annotation> annotationType) {
		if(builder!=null) throw new IllegalStateException();
		this.builder = new AnnotationBuilderClass(annotationType);
	}
}
