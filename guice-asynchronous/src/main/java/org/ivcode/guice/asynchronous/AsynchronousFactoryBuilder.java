package org.ivcode.guice.asynchronous;

import com.google.inject.TypeLiteral;

public interface AsynchronousFactoryBuilder {
	public void to(Class<?> asyc);
	public void to(TypeLiteral<?> asyc);
}
