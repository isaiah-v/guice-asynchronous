package iv.guice.asynchronous.enhancer;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.InterceptorBinding;

/**
 * <b>not thread-safe</b>
 * @author isaiah
 */
public class ElementsBeanVisitor extends DefaultElementVisitor<Void> {

	Collection<InterceptorBinding> interceptors = new ArrayList<InterceptorBinding>();
	Collection<Element> others = new ArrayList<Element>();
	Map<Key<?>, Binding<?>> bindings = new HashMap<Key<?>, Binding<?>>();
	
	@Override
	public <T> Void visit(Binding<T> binding) {
		bindings.put(binding.getKey(), binding);
		return null;
	}
	
	@Override
	public Void visit(InterceptorBinding interceptorBinding) {
		interceptors.add(interceptorBinding);
		return null;
	}
	
	@Override
	protected Void visitOther(Element element) {
		others.add(element);
		return null;
	}

	public void clear() {
		bindings.clear();
		interceptors.clear();
		others.clear();
	}

	public ElementsBean asElementsBean() {
		return new ElementsBean(bindings,interceptors,others);
	}
}
