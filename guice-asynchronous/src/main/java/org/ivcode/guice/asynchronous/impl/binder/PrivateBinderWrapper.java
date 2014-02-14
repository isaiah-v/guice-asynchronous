package org.ivcode.guice.asynchronous.impl.binder;

import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedElementBuilder;

public class PrivateBinderWrapper extends BinderWrapper implements PrivateBinder{

	PrivateBinderWrapper(PrivateBinder binder) {
		super(binder);
	}

	protected PrivateBinder getBinder() {
		return (PrivateBinder) super.getBinder();
	}
	
	public void expose(Key<?> arg0) {
		getBinder().expose(arg0);
	}

	public AnnotatedElementBuilder expose(Class<?> arg0) {
		return getBinder().expose(arg0);
	}

	public AnnotatedElementBuilder expose(TypeLiteral<?> arg0) {
		return getBinder().expose(arg0);
	}
	
	public PrivateBinder withSource(Object arg0) {
		return (PrivateBinder) super.withSource(arg0);
	}
	
	@Override
	public PrivateBinder skipSources(@SuppressWarnings("rawtypes") Class... arg0) {
		return (PrivateBinder) super.skipSources(arg0);
	}
}
