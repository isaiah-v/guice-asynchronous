package iv.guice.asynchronous.interceptors;

import java.util.logging.Logger;

import iv.guice.asynchronous.Asynchronous;
import iv.guice.asynchronous.AsynchronousContext;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.matcher.Matchers;


public class FailFastInterceptor implements MethodInterceptor {

	@Inject
	private Logger logger;
	
	@Inject
	private AsynchronousContext context;
	
	public FailFastInterceptor(Binder binder) {
		binder.requestInjection(this);
	}
	
	public FailFastInterceptor(AsynchronousContext context, Logger logger) {
		this.context = context;
		this.logger = logger;
	}
	
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if(context==null) {
			logger.warning("FAILFAST: asynchronous context not defined");
			return invocation.proceed();
		}
		
		if(context.getExceptionsThrown()>0) {
			logger.warning("FAILFAST: skipping task: " + invocation.getMethod());
			return null;
		}
		
		return invocation.proceed();
	}
	
	/**
	 * Binds the fail fast intercepter all asynchronous methods
	 * @param binder
	 * 		the binder to bind the intercepter to
	 */
	public static final void bind(Binder binder) {
		FailFastInterceptor ffi = new FailFastInterceptor(binder);
		binder.bindInterceptor(Matchers.any(), Matchers.annotatedWith(Asynchronous.class), ffi);
	}
}
