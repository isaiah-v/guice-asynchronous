package org.ivcode.guice.asynchronous;

import com.google.inject.PrivateBinder;

public interface AsynchronousPrivateBinder extends AsynchronousBinder, PrivateBinder {
	public AsynchronousPrivateBinder skipSources(@SuppressWarnings("rawtypes") Class... classesToSkip);
	public AsynchronousPrivateBinder withSource(Object source);
}
