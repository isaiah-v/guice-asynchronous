package org.ivcode.guice.asynchronous.internal.binding;

import org.ivcode.guice.asynchronous.internal.binding.Binding;
import org.ivcode.guice.asynchronous.internal.binding.BindingFactory;

public interface BindingBuilder {
	public Binding build(BindingFactory bindingFactory);
}
