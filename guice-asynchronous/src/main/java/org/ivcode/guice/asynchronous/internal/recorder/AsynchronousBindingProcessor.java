package org.ivcode.guice.asynchronous.internal.recorder;

import java.util.Collection;

public interface AsynchronousBindingProcessor {
	public void process(Collection<AsynchronousBindingBean<?>> asyncBindings);
}
