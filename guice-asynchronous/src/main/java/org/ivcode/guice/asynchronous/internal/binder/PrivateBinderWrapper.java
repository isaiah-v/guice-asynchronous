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
package org.ivcode.guice.asynchronous.internal.binder;

import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedElementBuilder;

class PrivateBinderWrapper extends BinderWrapper implements PrivateBinder{

	PrivateBinderWrapper(PrivateBinder binder) {
		super(binder);
	}

	protected PrivateBinder getBinder() {
		return (PrivateBinder) super.getBinder();
	}
	
	public void expose(Key<?> arg0) {
		getBinder().expose(arg0);
	}

	public AnnotatedElementBuilder expose(Class<?> arg0) {
		return getBinder().expose(arg0);
	}

	public AnnotatedElementBuilder expose(TypeLiteral<?> arg0) {
		return getBinder().expose(arg0);
	}
	
	public PrivateBinder withSource(Object arg0) {
		return (PrivateBinder) super.withSource(arg0);
	}
	
	@Override
	public PrivateBinder skipSources(@SuppressWarnings("rawtypes") Class... arg0) {
		return (PrivateBinder) super.skipSources(arg0);
	}

	@Override
	public String toString() {
		return "PrivateBinderWrapper [getBinder()=" + getBinder() + "]";
	}
}
