package org.ivcode.guice.asynchronous.internal.binding;

import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClass;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;

public class ConstructorProvidersModule implements Module {

	private final AsynchronousClass<?> asyncClass;
	
	public ConstructorProvidersModule(AsynchronousClass<?> asyncClass) {
		this.asyncClass = asyncClass;
	}

	public void configure(Binder binder) {
		Key<?>[] argumentKeys = asyncClass.getConstructor().getArgumentKeys();
        
        Provider<?>[] provider = new Provider[argumentKeys.length];
        for(int i=0; i<provider.length; i++) {
            Key<?> key = argumentKeys[i];
            
            Provider<?> p = binder.getProvider(key);
            provider[i] = p;
        }
        
        binder.bind(Provider[].class).toInstance(provider);
	}

}
