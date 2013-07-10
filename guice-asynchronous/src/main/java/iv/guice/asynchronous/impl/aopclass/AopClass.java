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
package iv.guice.asynchronous.impl.aopclass;

import java.util.Arrays;

import com.google.inject.Key;

public class AopClass<T> {

    private Key<T> key;
    private Object source;
    private AopMethod[] methods;

    public Key<T> getKey() {
        return key;
    }

    public void setKey(Key<T> key) {
        this.key = key;
    }

    public AopMethod[] getMethods() {
        return methods;
    }

    public void setMethods(AopMethod[] methods) {
        this.methods = methods;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "AopClass [key=" + key + ", source=" + source + ", methods=" + Arrays.toString(methods) + "]";
    }
}
