package org.ivcode.guice.asynchronous.impl.bindings;

import com.google.inject.binder.ScopedBindingBuilder;

public interface ScopeBinding {
	public void applyTo(ScopedBindingBuilder scopedBindingBuilder);
}
