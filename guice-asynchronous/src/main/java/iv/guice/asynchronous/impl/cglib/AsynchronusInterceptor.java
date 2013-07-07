package iv.guice.asynchronous.impl.cglib;

import iv.guice.asynchronous.impl.manager.ExceptionListener;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


class AsynchronusInterceptor implements MethodInterceptor {

	private final Executor executor;
	private final MethodInterceptor methodInterceptor;
	private final ExceptionListener exceptionListener;
	
	public AsynchronusInterceptor(Executor executor, ExceptionListener exceptionListener, MethodInterceptor methodInterceptor) {
		this.executor = executor;
		this.methodInterceptor = methodInterceptor;
		this.exceptionListener = exceptionListener;
	}
	
	public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
		executor.execute(new Runnable() {
			public void run() {
				try {
					// the wrapped method intercepter should invoke the method 
					methodInterceptor.intercept(obj, method, args, proxy);
				} catch (Throwable th) {					
					th.printStackTrace();
					exceptionListener.onException(method, th);
				}
			}
		});
		
		// all asynchronous methods return void
		return null;
	}
}
