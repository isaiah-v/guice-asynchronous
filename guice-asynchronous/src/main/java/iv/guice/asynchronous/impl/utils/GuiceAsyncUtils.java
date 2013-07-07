package iv.guice.asynchronous.impl.utils;

import com.google.inject.Key;
import com.google.inject.TypeLiteralFactory;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.InstanceBinding;

public class GuiceAsyncUtils {
	
	public static Object getSource() {
		return Thread.currentThread().getStackTrace()[2];
	}
	
	public static <T> InjectionRequest<T> requestInjection(T instance) {
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) instance.getClass();
		return new InjectionRequest<T>(getSource(), TypeLiteralFactory.<T>createTypeLiteral(clazz), instance);
	}
	
	public static <T> InstanceBinding<T> bindInstance(Key<T> key, T instance) {
		return new InstanceBindingImpl<T>(key, instance, getSource());
	}
}
