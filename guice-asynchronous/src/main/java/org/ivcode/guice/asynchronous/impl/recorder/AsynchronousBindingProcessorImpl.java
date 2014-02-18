package org.ivcode.guice.asynchronous.impl.recorder;

import java.util.Collection;

import org.ivcode.guice.asynchronous.impl.bindingclass.BindingClassFactory;
import org.ivcode.guice.asynchronous.impl.bindings.AsynchronousBinding;
import org.ivcode.guice.asynchronous.impl.cglib.EnhancerFactory;

import com.google.inject.Binder;

public class AsynchronousBindingProcessorImpl implements AsynchronousBindingProcessor {
	
	private final BindingClassFactory bindingClassFactory;
	private final EnhancerFactory enhancerFactory;
	
	public AsynchronousBindingProcessorImpl(BindingClassFactory bindingClassFactory, EnhancerFactory enhancerFactory) {
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
	
	private <T> AsynchronousBinding<T> createAsynchronousBinding(AsynchronousBindingBean<T> bean, BindingClassFactory bindingClassFactory, EnhancerFactory enhancerFactory) {
		AsynchronousBinding<T> binding = new AsynchronousBinding<T>(
				bean.getKey(),
				bean.getConstructor(),
				bean.getInterceptors().getInterceptors(),
				bean.getScopeBinding(), bean.getSource(),
				bindingClassFactory,
				enhancerFactory);
		
		return binding;
	}
}
