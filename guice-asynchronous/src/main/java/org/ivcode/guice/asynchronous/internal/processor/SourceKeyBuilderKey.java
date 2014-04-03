package org.ivcode.guice.asynchronous.internal.processor;

import java.lang.annotation.Annotation;

import com.google.inject.Key;

class SourceKeyBuilderKey<T> implements SourceKeyBuilder<T> {

	private final Key<T> key;
	
	SourceKeyBuilderKey(Key<T> key) {
		this.key = key;
	}
	
	public Key<T> build() {
		return key;
	}

	public void annotatedWith(Annotation annotation) {
		throw new IllegalStateException("key already defined");
	}

	public void annotatedWith(Class<? extends Annotation> annotationType) {
		throw new IllegalStateException("key already defined");
	}
}
