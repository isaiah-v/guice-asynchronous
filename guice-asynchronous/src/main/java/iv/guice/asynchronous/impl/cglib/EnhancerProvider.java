package iv.guice.asynchronous.impl.cglib;

import net.sf.cglib.proxy.Enhancer;

import com.google.inject.Inject;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;

/**
 * A {@link Provider} that wraps the {@link Enhancer}
 * @author isaiah
 *
 * @param <T>
 * 		The provider type
 */
class EnhancerProvider<T> implements Provider<T> {

	/** injects the members into the instance variable */
	@Inject
	private MembersInjector<T> membersInjector;
	
	/** creates an instances of <code>T</code> */
	@Inject
	private Enhancer enhancer;
	
	@SuppressWarnings("unchecked")
	public T get() {
		return injectMembers((T)enhancer.create());
	}
	
	/**
	 * Injects the dependencies into the given instance variable
	 * @param t
	 * 		the instance to inject the dependencies
	 * @return
	 * 		the instance
	 */
	private T injectMembers(T t) {
		membersInjector.injectMembers(t);
		return t;
	}
}
