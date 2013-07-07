package iv.guice.asynchronous.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * The {@link AsynchronousInterceptor} works in connection with the {@link Callback}
 * interface.
 * 
 * @author isaiah
 * 
 */
public class AsynchronousInterceptor implements MethodInterceptor {

	private int thrownExceptions = 0;
	
	private FailFastHandler failFast = new FailFastHandler();
	private FailExceptionHandler failExceptions = new FailExceptionHandler();

	public Object invoke(MethodInvocation invocation) throws Throwable {
		try {
			failFast.handle(thrownExceptions, invocation);
			return invocation.proceed();
		} catch (final Throwable th) {
			thrownExceptions++;
			
			if(failExceptions.handle(th, invocation)>0)
				return null;
			else
				throw th;
		}
	}
}
