package org.ivcode.guice.asynchronous.internal.bindings;

import com.google.inject.Scope;
import com.google.inject.binder.ScopedBindingBuilder;

public class InstanceScopeBinding implements ScopeBinding {

	private final Scope scope;
	
	public InstanceScopeBinding(Scope scope) {
		this.scope = scope;
	}
	
	public void applyTo(ScopedBindingBuilder scopedBindingBuilder) {
		scopedBindingBuilder.in(scope);
	}
	
}
