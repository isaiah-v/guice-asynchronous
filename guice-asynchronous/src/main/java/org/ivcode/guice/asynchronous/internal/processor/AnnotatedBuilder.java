package org.ivcode.guice.asynchronous.internal.processor;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;

interface AnnotatedBuilder {
	public <T> Key<T> createKey(Class<T> type);
	public <T> Key<T> createKey(TypeLiteral<T> type);
}
