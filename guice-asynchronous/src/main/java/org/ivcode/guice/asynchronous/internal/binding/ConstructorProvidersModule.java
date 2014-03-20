package org.ivcode.guice.asynchronous.internal.binding;

import java.util.Collection;
import java.util.Collections;

import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClass;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;

public class ConstructorProvidersModule implements Module {

	private final AsynchronousClass<?> asyncClass;
	private final Collection<Key<?>> noProviderKeys;
	
	public ConstructorProvidersModule(AsynchronousClass<?> asyncClass) {
		this(asyncClass, Collections.<Key<?>>emptyList());
	}
	
	public ConstructorProvidersModule(AsynchronousClass<?> asyncClass, Collection<Key<?>> noProviderKeys) {
		if(asyncClass==null || noProviderKeys==null) {
			throw new NullPointerException();
		}
		
		this.asyncClass = asyncClass;
		this.noProviderKeys = noProviderKeys;
	}

	public void configure(Binder binder) {
		Key<?>[] argumentKeys = asyncClass.getConstructor().getArgumentKeys();
        
        Provider<?>[] provider = new Provider[argumentKeys.length];
        for(int i=0; i<provider.length; i++) {
            Key<?> key = argumentKeys[i];
            if(isIgnore(key)) { continue; }
            
            Provider<?> p = binder.getProvider(key);
            provider[i] = p;
        }
        
        binder.bind(Provider[].class).toInstance(provider);
	}
	
	private boolean isIgnore(Key<?> key) {
		return noProviderKeys.contains(key);
	}

}
