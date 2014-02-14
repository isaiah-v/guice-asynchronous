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
package org.ivcode.guice.asynchronous.impl.bindingclass;

import java.util.Arrays;

import com.google.inject.Key;

public class BindingConstructor {
    private Class<?>[] argumentTypes;
    private Key<?>[] argumentKeys;
    
    public Class<?>[] getArgumentTypes() {
        return argumentTypes;
    }
    
    public void setArgumentTypes(Class<?>[] argumentTypes) {
        this.argumentTypes = argumentTypes;
    }
    
    public Key<?>[] getArgumentKeys() {
        return argumentKeys;
    }
    
    public void setArgumentKeys(Key<?>[] argumentKeys) {
        this.argumentKeys = argumentKeys;
    }

    @Override
    public String toString() {
        return "BindingConstructor [argumentTypes="
                + Arrays.toString(argumentTypes) + ", argumentKeys="
                + Arrays.toString(argumentKeys) + "]";
    }
}
