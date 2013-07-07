package iv.guice.asynchronous.impl.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.spi.Element;
import com.google.inject.spi.InterceptorBinding;

public class ElementsBean {
	
	private final Map<Key<?>, Binding<?>> bindings;
	private final Collection<InterceptorBinding> interceptors;
	private final Collection<Element> others;
	
	public ElementsBean(Map<Key<?>, Binding<?>> bindings, Collection<InterceptorBinding> interceptors, Collection<Element> others) {
		this.bindings = bindings;
		this.interceptors = interceptors;
		this.others = others;
	}

	public Map<Key<?>, Binding<?>> getBindings() {
		return bindings;
	}

	public Collection<InterceptorBinding> getInterceptors() {
		return interceptors;
	}
	
	public Collection<Element> getOthers() {
		return others;
	}
	
	public Collection<Element> createElementCollection() {
		int size = bindings.size() + interceptors.size() + others.size();
		
		Collection<Element> value = new ArrayList<Element>(size);
		
		value.addAll(bindings.values());
		value.addAll(interceptors);
		value.addAll(others);
		
		return value;
	}
}
