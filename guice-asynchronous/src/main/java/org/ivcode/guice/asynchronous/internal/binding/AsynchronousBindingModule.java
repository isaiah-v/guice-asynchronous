package org.ivcode.guice.asynchronous.internal.binding;

import java.lang.annotation.Annotation;

import static org.ivcode.guice.asynchronous.internal.binding.Utils.*;

import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClass;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerData;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerProvider;
import org.ivcode.guice.asynchronous.internal.utils.GuiceAsyncUtils;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.ScopedBindingBuilder;

public class AsynchronousBindingModule<T> implements Module {
	
	private final Key<? super T> sourceKey;
	private final Key<T> targetKey;
	private final ScopeBinding scopeBinding;
	
	private final AsynchronousClass<?> asyncClass;
	private final EnhancerData enhancerData;
	
	public AsynchronousBindingModule(Key<? super T> sourceKey, Key<T> targetKey, ScopeBinding scopeBinding,
			AsynchronousClass<?> asyncClass, EnhancerData enhancerData) {
		this.sourceKey = sourceKey;
		this.targetKey = targetKey;
		this.scopeBinding = scopeBinding;
		this.asyncClass = asyncClass;
		this.enhancerData = enhancerData;
	}

	public void configure(Binder binder) {
		binder.install(new ConstructorProvidersModule(asyncClass));
		binder.install(new EnhancerDataModule(enhancerData));
		
		ScopedBindingBuilder sbb = bindEnhancerProvider(binder, targetKey);
		if(scopeBinding!=null) {
        	scopeBinding.applyTo(sbb);
        } else {
        	Annotation a = GuiceAsyncUtils.findScopeAnnotation(targetKey);
        	if(a!=null) { sbb.in(a.annotationType()); }
        }
		
		if(!sourceKey.equals(targetKey)) {
			binder.bind(sourceKey).to(targetKey);
		}
	}
	
	private ScopedBindingBuilder bindEnhancerProvider(Binder binder, Key<T> key) {
		TypeLiteral<EnhancerProvider<T>> type = createEnhancerProviderType(key);
        return binder.bind(key).toProvider(type);
    }
}
