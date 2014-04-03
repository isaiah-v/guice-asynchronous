package org.ivcode.guice.asynchronous.internal.processor;

import org.ivcode.guice.asynchronous.AsynchronousAnnotatedBindingBuilder;
import org.ivcode.guice.asynchronous.AsynchronousLinkedBindingBuilder;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class AsyncBuilderFactory {
	private AsyncBuilderFactory() {}
	
	public static <T> AsynchronousLinkedBindingBuilder<T> createAsynchronousAnnotatedBindingBuilder(Key<T> key, AsynchronousBindingBuilder builder) {
		MyAsynchronousAnnotatedBindingBuilder<T> value = new MyAsynchronousAnnotatedBindingBuilder<T>(new SourceKeyBuilderKey<T>(key));
		builder.setMyBindingBuilder(value);
		
		return value;
	}
	
	public static <T> AsynchronousAnnotatedBindingBuilder<T> createAsynchronousAnnotatedBindingBuilder(Class<T> clazz, AsynchronousBindingBuilder builder) {
		MyAsynchronousAnnotatedBindingBuilder<T> value = new MyAsynchronousAnnotatedBindingBuilder<T>(new SourceKeyBuilderClass<T>(clazz));
		builder.setMyBindingBuilder(value);
		
		return value;
	}
	
	public static <T> AsynchronousAnnotatedBindingBuilder<T> createAsynchronousAnnotatedBindingBuilder(TypeLiteral<T> type, AsynchronousBindingBuilder builder) {
		MyAsynchronousAnnotatedBindingBuilder<T> value = new MyAsynchronousAnnotatedBindingBuilder<T>(new SourceKeyBuilderType<T>(type));
		builder.setMyBindingBuilder(value);
		
		return value;
	}
}
