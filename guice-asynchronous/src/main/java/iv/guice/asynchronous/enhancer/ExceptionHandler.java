package iv.guice.asynchronous.enhancer;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public abstract class ExceptionHandler implements MethodInterceptor {
	
	public Object invoke(MethodInvocation invocation) throws Throwable {
		try {
			return invocation.proceed();
		} catch(final Throwable th) {
			this.onException(invocation.getMethod(), th);
			throw th;
		}
	}
	
	protected abstract void onException(Method method, Throwable th);
}
