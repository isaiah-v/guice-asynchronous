package org.ivcode.guice.asynchronous.internal.binding;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.reflect.FastConstructor;

import org.ivcode.guice.asynchronous.internal.proxy.EnhancerData;

import com.google.inject.Binder;
import com.google.inject.Module;

public class EnhancerDataModule implements Module {

	private final EnhancerData enhancerData;
	
	public EnhancerDataModule(EnhancerData enhancerData) {
		this.enhancerData = enhancerData;
	}

	public void configure(Binder binder) {
        binder.bind(FastConstructor.class).toInstance(enhancerData.getFastConstructor());
        binder.bind(Callback[].class).toInstance(enhancerData.getCallbacks());
	}
}
