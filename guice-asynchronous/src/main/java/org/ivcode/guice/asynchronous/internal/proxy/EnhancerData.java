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
package org.ivcode.guice.asynchronous.internal.proxy;

import java.util.Arrays;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.reflect.FastConstructor;

public class EnhancerData {
	private final FastConstructor fastConstructor;
	private final Callback[] callbacks;
	
	public EnhancerData(FastConstructor fastConstructor, Callback[] callbacks) {
		this.fastConstructor = fastConstructor;
		this.callbacks = callbacks;
	}

	public FastConstructor getFastConstructor() {
		return fastConstructor;
	}
	
	public Callback[] getCallbacks() {
		return callbacks;
	}

	@Override
	public String toString() {
		return "EnhancerData [fastConstructor=" + fastConstructor
				+ ", callbacks=" + Arrays.toString(callbacks) + "]";
	}
}
