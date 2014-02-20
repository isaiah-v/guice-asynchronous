package org.ivcode.guice.asynchronous.internal.proxy;

import java.lang.reflect.Method;

public class AsyncTaskException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final Method method;
	
	public AsyncTaskException(Method method, Throwable cause) {
		super("Uncaught Exception : Method="+method.getName(), cause);
		this.method = method;
	}
	
	public Method getMethod() {
		return method;
	}
}
