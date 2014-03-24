package org.ivcode.guice.asynchronous.helpers.utils;

import org.ivcode.guice.asynchronous.AsynchronousBinder;
import org.ivcode.guice.asynchronous.AsynchronousModule;

import com.google.inject.Module;

class InstallerAsynchronousModule implements AsynchronousModule {

	private final Module[] modules;
	
	InstallerAsynchronousModule(Module[] modules) {
		this.modules = modules;
	}
	
	public void configure(AsynchronousBinder binder) {
		for(Module module : modules) {
			binder.install(module);
		}
	}

}
