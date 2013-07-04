package iv.guice.asynchronous.enhancer;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


public class AsynchronusInterceptor implements MethodInterceptor {

	private final Executor executor;
	private final MethodInterceptor methodInterceptor;
	
	public AsynchronusInterceptor(Executor executor, MethodInterceptor methodInterceptor) {
		this.executor = executor;
		this.methodInterceptor = methodInterceptor;
	}
	
	public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
		executor.execute(new Runnable() {
			public void run() {
				try {
					// the wrapped method intercepter should invoke the method 
					methodInterceptor.intercept(obj, method, args, proxy);
				} catch (Throwable th) {
					th.printStackTrace();
				}
			}
		});
		
		// all asynchronous methods return void
		return null;
	}
}
