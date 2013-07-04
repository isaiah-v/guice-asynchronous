package iv.guice.asynchronous.impl.aopclass;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;

public class AopMethod {
	private final Method method;
	private final boolean isAsynchronous;
	private final List<MethodInterceptor> interceptors;

	public AopMethod(Method method, boolean isAsynchronous,
			List<MethodInterceptor> interceptors) {
		this.method = method;
		this.isAsynchronous = isAsynchronous;
		this.interceptors = interceptors == null ? null
				: Collections
						.unmodifiableList(new ArrayList<MethodInterceptor>(
								interceptors));
	}

	public Method getMethod() {
		return method;
	}

	public boolean isAsynchronous() {
		return isAsynchronous;
	}

	public List<MethodInterceptor> getInterceptors() {
		return interceptors;
	}

	@Override
	public String toString() {
		return "AopMethod [method=" + method + ", isAsynchronous="
				+ isAsynchronous + ", interceptors=" + interceptors + "]";
	}
}
