package org.ivcode.guice.asynchronous.impl.recorder;

import java.util.Collection;

public interface AsynchronousBindingProcessor {
	public void process(Collection<AsynchronousBindingBean<?>> asyncBindings);
}
