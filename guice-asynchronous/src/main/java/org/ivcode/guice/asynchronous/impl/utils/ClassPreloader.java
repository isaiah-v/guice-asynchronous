package org.ivcode.guice.asynchronous.impl.utils;

import org.ivcode.guice.asynchronous.AsynchronousModule;

public class ClassPreloader {
	private ClassPreloader() {
	}
	
	public static void loadAsynchronousClasses() throws ClassNotFoundException {
		// pre-load classes used to run asynchronous tasks
		
		AsynchronousModule.class.getClassLoader().loadClass("org.ivcode.guice.asynchronous.impl.cglib.AsynchronusInterceptor");
		AsynchronousModule.class.getClassLoader().loadClass("org.ivcode.guice.asynchronous.impl.cglib.AsynchronusInterceptor$TaskExecutor");
		AsynchronousModule.class.getClassLoader().loadClass("org.ivcode.guice.asynchronous.impl.cglib.InterceptorStackCallback$InterceptedMethodInvocation");
		AsynchronousModule.class.getClassLoader().loadClass("org.ivcode.guice.asynchronous.impl.context.AsynchronousContextImpl$Task");
	}
}
