package org.ivcode.guice.asynchronous.helpers.utils;

import org.ivcode.guice.asynchronous.AsynchronousModule;
import org.ivcode.guice.asynchronous.GuiceAsynchronous;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class GuiceAsynchronousUtils {
	public static Injector createInjector(GuiceAsynchronous guiceAsync, AsynchronousModule... asyncModules) {
		return Guice.createInjector(guiceAsync.createModule(asyncModules));
	}
	
	public static AsynchronousModule install(Module...modules) {
		return new InstallerAsynchronousModule(modules);
	}
}
