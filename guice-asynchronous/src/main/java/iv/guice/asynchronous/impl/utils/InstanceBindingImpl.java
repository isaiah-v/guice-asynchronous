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
package iv.guice.asynchronous.impl.utils;

import java.util.Set;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.InstanceBinding;

public class InstanceBindingImpl<T> implements InstanceBinding<T> {

	private final Key<T> key;
	private final T instance;
	private final Object source;
	
	public InstanceBindingImpl(Key<T> key, T instance, Object source) {
		this.key = key;
		this.instance = instance;
		this.source = source;
	}
	
	public Key<T> getKey() {
		return key;
	}

	public Provider<T> getProvider() {
		// no provider
		return null;
	}

	public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
		return visitor.visit(this);
	}

	public <V> V acceptScopingVisitor(BindingScopingVisitor<V> visitor) {
		throw new UnsupportedOperationException();
	}

	public Object getSource() {
		return source;
	}

	public <V> V acceptVisitor(ElementVisitor<V> visitor) {
		return visitor.visit(this);
	}

	public void applyTo(Binder binder) {
		if(source!=null)
			binder = binder.withSource(source);
		binder.bind(getKey()).toInstance(getInstance());
	}

	public Set<Dependency<?>> getDependencies() {
		
		// TODO Auto-generated method stub
		return null;
	}

	public T getInstance() {
		return instance;
	}

	public Set<InjectionPoint> getInjectionPoints() {
		// TODO Auto-generated method stub
		return null;
	}
}
