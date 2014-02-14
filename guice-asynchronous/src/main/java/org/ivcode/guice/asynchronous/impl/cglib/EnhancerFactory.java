package org.ivcode.guice.asynchronous.impl.cglib;

import net.sf.cglib.proxy.Enhancer;

import org.ivcode.guice.asynchronous.impl.bindingclass.BindingClass;

public interface EnhancerFactory {
	public Enhancer createEnhancer(BindingClass<?> aopClass);
}