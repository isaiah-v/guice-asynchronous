package iv.guice.asynchronous.enhancer;

import java.util.Collection;

import com.google.inject.Module;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;

public class ElementsBeanFactory {
	
	private ElementsBeanFactory() {}
	
	public static ElementsBean createElementsBean(Module... modules) {
		return createElementsBean(Elements.getElements(modules));
	}
	
	public static ElementsBean createElementsBean(Collection<Element> elements) {
		ElementsBeanVisitor visitor = new ElementsBeanVisitor();
		for(Element e : elements) {
			e.acceptVisitor(visitor);
		}
		return visitor.asElementsBean();
	}
}
