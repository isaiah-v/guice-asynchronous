package com.google.inject;

import java.lang.reflect.Type;

import com.google.inject.util.Types;

public class TypeLiteralFactory {
	
	public static <T> TypeLiteral<T> createParameterizedTypeLiteral(Type rawType, Type... typeArguments) {
		// operation is not type safe
		Type t = Types.newParameterizedType(rawType, typeArguments);
		return new TypeLiteral<T>(t);
	}
	
	public static <T> TypeLiteral<T> createTypeLiteral(Class<T> type) {
		return new TypeLiteral<T>(){};
	}
}
