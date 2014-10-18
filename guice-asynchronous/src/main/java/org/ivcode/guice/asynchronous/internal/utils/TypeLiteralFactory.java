/**
 * Copyright (C) 2013 Isaiah van der Elst (isaiah.vanderelst@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ivcode.guice.asynchronous.internal.utils;

import java.lang.reflect.Type;

import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * A factory class for creating {@link TypeLiteral}s
 * 
 * @author Isaiah van der Elst
 */
public class TypeLiteralFactory {

	private TypeLiteralFactory() {
	}

	/**
	 * Creates a {@link TypeLiteral} that's of the given type ({@code rawType})
	 * and containing the given generic types ({@code typeArguments})
	 * 
	 * @param rawType
	 *            The type literal type
	 * @param typeArguments
	 *            The type arguments (generic types)
	 * @return a {@link TypeLiteral} that's of the given type ({@code rawType})
	 *         and containing the given generic types ({@code typeArguments})
	 */
	@SuppressWarnings("unchecked")
	public static <T> TypeLiteral<T> createParameterizedTypeLiteral(Type rawType, Type... typeArguments) {
		// operation is not type safe (be careful)
		Type t = Types.newParameterizedType(rawType, typeArguments);
		return (TypeLiteral<T>) TypeLiteral.get(t);
	}
}
