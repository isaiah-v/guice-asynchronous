package org.ivcode.guice.asynchronous;

import java.lang.annotation.Annotation;

public interface AsynchronousAnnotatedBindingBuilder<T> extends AsynchronousLinkedBindingBuilder<T> {
	AsynchronousLinkedBindingBuilder<T> annotatedWith(Annotation annotation);
	AsynchronousLinkedBindingBuilder<T> annotatedWith(Class<? extends Annotation> annotationType);
}
