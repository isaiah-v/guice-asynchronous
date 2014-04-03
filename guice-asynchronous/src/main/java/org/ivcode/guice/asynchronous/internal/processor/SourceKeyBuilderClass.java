package org.ivcode.guice.asynchronous.internal.processor;

import java.lang.annotation.Annotation;

import com.google.inject.Key;

class SourceKeyBuilderClass<T> implements SourceKeyBuilder<T> {

	private final Class<T> type;
	private AnnotatedBuilder builder;
	
	SourceKeyBuilderClass(Class<T> type) {
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
