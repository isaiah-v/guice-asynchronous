package iv.guice.asynchronous.impl.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.InterceptorBinding;

public class ElementSpliceFactory {
	
	private ElementSpliceFactory() {}
	
	public static ElementSplice createElementsBean(Module... modules) {
		return createElementsBean(Elements.getElements(modules));
	}
	
	public static ElementSplice createElementsBean(Collection<Element> elements) {
		ElementsSpliceVisitor visitor = new ElementsSpliceVisitor();
		for(Element e : elements) {
			e.acceptVisitor(visitor);
		}
		return visitor.asElementsBean();
	}
	
	private static class ElementsSpliceVisitor extends DefaultElementVisitor<Void> {

		private Collection<InterceptorBinding> interceptors = new ArrayList<InterceptorBinding>();
		private Collection<Element> others = new ArrayList<Element>();
		private Map<Key<?>, Binding<?>> bindings = new HashMap<Key<?>, Binding<?>>();
		
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

		public ElementSplice asElementsBean() {
			return new ElementSplice(bindings,interceptors,others);
		}
	}
}
