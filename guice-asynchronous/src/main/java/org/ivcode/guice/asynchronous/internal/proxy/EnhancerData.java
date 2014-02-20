package org.ivcode.guice.asynchronous.internal.proxy;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.reflect.FastConstructor;

public class EnhancerData {
	private final FastConstructor fastConstructor;
	private final Callback[] callbacks;
	
	public EnhancerData(FastConstructor fastConstructor, Callback[] callbacks) {
		this.fastConstructor = fastConstructor;
		this.callbacks = callbacks;
	}

	public FastConstructor getFastConstructor() {
		return fastConstructor;
	}
	
	public Callback[] getCallbacks() {
		return callbacks;
	}
}
