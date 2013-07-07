package iv.guice.asynchronous.impl.manager;

import java.lang.reflect.Method;

public interface ExceptionListener {
	public void onException(Method method, Throwable th);
}
