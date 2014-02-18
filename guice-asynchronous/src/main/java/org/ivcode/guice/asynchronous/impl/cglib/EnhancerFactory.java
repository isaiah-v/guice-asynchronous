package org.ivcode.guice.asynchronous.impl.cglib;

import org.ivcode.guice.asynchronous.impl.bindingclass.BindingClass;

public interface EnhancerFactory {
	public EnhancerData createEnhancer(BindingClass<?> aopClass);
}