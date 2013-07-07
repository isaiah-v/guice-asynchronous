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
package iv.guice.asynchronous.impl.cglib;

import iv.guice.asynchronous.impl.aopclass.AopClass;

import java.lang.reflect.Type;

import net.sf.cglib.proxy.Enhancer;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.TypeLiteral;
import com.google.inject.TypeLiteralFactory;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;

/**
 * Used to bind {@link #getKey()} to the classes produced by {@link #getEnhancer()}
 * @author isaiah
 * 
 * @param <T>
 * 		the binding type
 */
public class EnhancerElement<T> implements Element {

	private final Object source;
	private final Key<T> key;
	private final Enhancer enhancer;
	
	public EnhancerElement(Object source, Key<T> key, Enhancer enhancer) {
		this.source = source;
		this.key = key;
		this.enhancer = enhancer;
	}
	
	public Object getSource() {
		return source;
	}

	public void applyTo(Binder binder) {
		if(this.source!=null) binder = binder.withSource(source);
		final Key<T> key = this.getKey();
		
		PrivateBinder privateBinder = binder.newPrivateBinder();
		
		privateBinder.bind(Enhancer.class).toInstance(enhancer);
		
		Type mainType = EnhancerProvider.class;
		Type genaricType = key.getTypeLiteral().getType();
		TypeLiteral<EnhancerProvider<T>> enhancerProvider = TypeLiteralFactory.createParameterizedTypeLiteral(mainType, genaricType);
		privateBinder.bind(key).toProvider(enhancerProvider);
		
		privateBinder.expose(key);
	}
	
	public Key<T> getKey() {
		return key;
	}
	
	public Enhancer getEnhancer() {
		return enhancer;
	}

	public <V> V acceptVisitor(ElementVisitor<V> visitor) {
		// not needed
		throw new UnsupportedOperationException();
	}
	
	public static <T> EnhancerElement<T> createEnhancerElement(AopClass<T> aopClass, Enhancer enhancer) {
		final Object source = aopClass.getSource();
		final Key<T> key = aopClass.getKey();
		
		return new EnhancerElement<T>(source, key, enhancer);
	}
}
