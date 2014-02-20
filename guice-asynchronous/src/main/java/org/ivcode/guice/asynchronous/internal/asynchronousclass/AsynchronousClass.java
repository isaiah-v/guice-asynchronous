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
package org.ivcode.guice.asynchronous.internal.asynchronousclass;

import java.util.Arrays;

import com.google.inject.Key;

public class AsynchronousClass<T> {

    private Key<T> key;
    private AsynchronousMethod[] methods;
    private AsynchronousConstructor constructor;

    public Key<T> getKey() {
        return key;
    }

    public void setKey(Key<T> key) {
        this.key = key;
    }

    public AsynchronousConstructor getConstructor() {
        return constructor;
    }

    public void setConstructor(AsynchronousConstructor constructor) {
        this.constructor = constructor;
    }

    public AsynchronousMethod[] getMethods() {
        return methods;
    }

    public void setMethods(AsynchronousMethod[] methods) {
        this.methods = methods;
    }

	@Override
	public String toString() {
		return "AsynchronousClass [key=" + key + ", methods="
				+ Arrays.toString(methods) + ", constructor=" + constructor
				+ "]";
	}
}
