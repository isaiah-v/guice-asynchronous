package org.ivcode.guice.asynchronous.internal.utils;

import org.ivcode.guice.asynchronous.AsynchronousModule;

public class ClassPreloader {
	private ClassPreloader() {
	}
	
	public static void loadAsynchronousClasses() throws ClassNotFoundException {
		// pre-load classes used to run asynchronous tasks
		
		AsynchronousModule.class.getClassLoader().loadClass("org.ivcode.guice.asynchronous.internal.proxy.AsynchronusInterceptor");
		AsynchronousModule.class.getClassLoader().loadClass("org.ivcode.guice.asynchronous.internal.proxy.AsynchronusInterceptor$TaskExecutor");
		AsynchronousModule.class.getClassLoader().loadClass("org.ivcode.guice.asynchronous.internal.proxy.InterceptorStackCallback$InterceptedMethodInvocation");
		AsynchronousModule.class.getClassLoader().loadClass("org.ivcode.guice.asynchronous.context.AsynchronousContextImpl$Task");
	}
}
