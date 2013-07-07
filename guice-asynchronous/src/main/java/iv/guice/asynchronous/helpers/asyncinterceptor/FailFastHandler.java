package iv.guice.asynchronous.helpers.asyncinterceptor;

import java.lang.reflect.Constructor;

import org.aopalliance.intercept.MethodInvocation;

class FailFastHandler {

	void handle(Integer thrownExceptions, MethodInvocation invocation) throws Throwable {
		FailFast failFast = invocation.getStaticPart().getAnnotation(FailFast.class);
		if(failFast==null) return;
		
		if (thrownExceptions > 0)
			throw createException(failFast.exception(), failFast.message());
	}

	private Throwable createException(Class<? extends Throwable> clazz, String msg) throws Throwable {
		Constructor<? extends Throwable> c = clazz.getConstructor(String.class);
		return c.newInstance(msg);
	}
}
