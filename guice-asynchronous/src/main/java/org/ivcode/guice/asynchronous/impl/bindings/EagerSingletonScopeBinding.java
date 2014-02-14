package org.ivcode.guice.asynchronous.impl.bindings;

import com.google.inject.binder.ScopedBindingBuilder;

public class EagerSingletonScopeBinding implements ScopeBinding {
	public void applyTo(ScopedBindingBuilder scopedBindingBuilder) {
		scopedBindingBuilder.asEagerSingleton();
	}
}
