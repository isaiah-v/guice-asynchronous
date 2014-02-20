package org.ivcode.guice.asynchronous.internal.proxy;

import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClass;

public interface EnhancerFactory {
	public EnhancerData createEnhancer(AsynchronousClass<?> aopClass);
}