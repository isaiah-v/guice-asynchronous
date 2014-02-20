package org.ivcode.guice.asynchronous.internal.bindings;

import com.google.inject.binder.ScopedBindingBuilder;

public interface ScopeBinding {
	public void applyTo(ScopedBindingBuilder scopedBindingBuilder);
}
