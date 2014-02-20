package org.ivcode.guice.asynchronous.internal.binder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.Message;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeListener;

class BinderWrapper implements Binder {

	private final Binder binder;
	
	BinderWrapper(Binder binder) {
		this.binder = binder;
	}
	
	protected Binder getBinder() {
		return binder;
	}
	
	public void addError(Throwable arg0) {
		binder.addError(arg0);
	}

	public void addError(Message arg0) {
		binder.addError(arg0);
	}

	public void addError(String arg0, Object... arg1) {
		binder.addError(arg0, arg1);
	}

	public <T> LinkedBindingBuilder<T> bind(Key<T> arg0) {
		return binder.bind(arg0);
	}

	public <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> arg0) {
		return binder.bind(arg0);
	}

	public <T> AnnotatedBindingBuilder<T> bind(Class<T> arg0) {
		return binder.bind(arg0);
	}

	public AnnotatedConstantBindingBuilder bindConstant() {
		return binder.bindConstant();
	}

	public void bindInterceptor(Matcher<? super Class<?>> arg0, Matcher<? super Method> arg1, MethodInterceptor... arg2) {
		binder.bindInterceptor(arg0, arg1, arg2);
	}

	public void bindListener(Matcher<? super TypeLiteral<?>> arg0, TypeListener arg1) {
		binder.bindListener(arg0, arg1);
	}

	public void bindScope(Class<? extends Annotation> arg0, Scope arg1) {
		binder.bindScope(arg0, arg1);
	}

	public void convertToTypes(Matcher<? super TypeLiteral<?>> arg0, TypeConverter arg1) {
		binder.convertToTypes(arg0, arg1);
	}

	public Stage currentStage() {
		return binder.currentStage();
	}

	public void disableCircularProxies() {
		binder.disableCircularProxies();
	}

	public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> arg0) {
		return binder.getMembersInjector(arg0);
	}

	public <T> MembersInjector<T> getMembersInjector(Class<T> arg0) {
		return binder.getMembersInjector(arg0);
	}

	public <T> Provider<T> getProvider(Key<T> arg0) {
		return binder.getProvider(arg0);
	}

	public <T> Provider<T> getProvider(Class<T> arg0) {
		return binder.getProvider(arg0);
	}

	public void install(Module arg0) {
		binder.install(arg0);
	}

	public PrivateBinder newPrivateBinder() {
		return binder.newPrivateBinder();
	}

	public void requestInjection(Object arg0) {
		binder.requestInjection(arg0);
	}

	public <T> void requestInjection(TypeLiteral<T> arg0, T arg1) {
		binder.requestInjection(arg0, arg1);
	}

	public void requestStaticInjection(Class<?>... arg0) {
		binder.requestStaticInjection(arg0);
	}

	public void requireExplicitBindings() {
		binder.requireExplicitBindings();
	}

	public Binder skipSources(@SuppressWarnings("rawtypes") Class... arg0) {
		return binder.skipSources(arg0);
	}

	public Binder withSource(Object arg0) {
		return binder.withSource(arg0);
	}

}
