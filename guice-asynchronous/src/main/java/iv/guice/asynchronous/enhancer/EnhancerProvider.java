package iv.guice.asynchronous.enhancer;

import net.sf.cglib.proxy.Enhancer;

import com.google.inject.Inject;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;

class EnhancerProvider<T> implements Provider<T> {

	@Inject
	private MembersInjector<T> membersInjector;
	
	@Inject
	private Enhancer enhancer;
	
	
	@SuppressWarnings("unchecked")
	public T get() {
		return injectMembers((T)enhancer.create());
	}
	
	private T injectMembers(T t) {
		membersInjector.injectMembers(t);
		return t;
	}
}
