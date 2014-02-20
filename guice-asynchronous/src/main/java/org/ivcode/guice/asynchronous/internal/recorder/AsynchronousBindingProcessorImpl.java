package org.ivcode.guice.asynchronous.internal.recorder;

import java.util.Collection;

import org.ivcode.guice.asynchronous.internal.asynchronousclass.AsynchronousClassFactory;
import org.ivcode.guice.asynchronous.internal.bindings.AsynchronousBinding;
import org.ivcode.guice.asynchronous.internal.proxy.EnhancerFactory;

import com.google.inject.Binder;

public class AsynchronousBindingProcessorImpl implements AsynchronousBindingProcessor {
	
	private final AsynchronousClassFactory bindingClassFactory;
	private final EnhancerFactory enhancerFactory;
	
	public AsynchronousBindingProcessorImpl(AsynchronousClassFactory bindingClassFactory, EnhancerFactory enhancerFactory) {
		this.bindingClassFactory = bindingClassFactory;
		this.enhancerFactory = enhancerFactory;
	}

	public void process(Collection<AsynchronousBindingBean<?>> asyncBindings) {
		for (AsynchronousBindingBean<?> bean : asyncBindings) {
			Binder binder = bean.getBinder();
			
			AsynchronousBinding<?> binding = createAsynchronousBinding(bean,bindingClassFactory, enhancerFactory);
			binding.applyTo(binder);
		}
	}
	
	private <T> AsynchronousBinding<T> createAsynchronousBinding(AsynchronousBindingBean<T> bean, AsynchronousClassFactory bindingClassFactory, EnhancerFactory enhancerFactory) {
		AsynchronousBinding<T> binding = new AsynchronousBinding<T>(
				bean.getKey(),
				bean.getConstructor(),
				bean.getInterceptors(),
				bean.getScopeBinding(), bean.getSource(),
				bindingClassFactory,
				enhancerFactory);
		
		return binding;
	}
}
