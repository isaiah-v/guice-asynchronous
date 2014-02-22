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
package org.ivcode.guice.asynchronous.helpers.exceptions;

import java.lang.reflect.Constructor;

class FailFastHandler {

    public static void failFast(Class<? extends Throwable> type, String message) throws Throwable {
        throw createException(type, message);
    }

    private static Throwable createException(Class<? extends Throwable> clazz, String msg) throws Throwable {
        Constructor<? extends Throwable> c = clazz.getConstructor(String.class);
        return c.newInstance(msg);
    }

	@Override
	public String toString() {
		return "FailFastHandler";
	}
}
