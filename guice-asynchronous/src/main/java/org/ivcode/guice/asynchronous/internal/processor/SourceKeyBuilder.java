package org.ivcode.guice.asynchronous.internal.processor;

import java.lang.annotation.Annotation;

import com.google.inject.Key;

interface SourceKeyBuilder <T> {
	void annotatedWith(Annotation annotation);
	void annotatedWith(Class<? extends Annotation> annotationType);
	Key<T> build();
}
