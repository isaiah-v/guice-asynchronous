package iv.guice.asynchronous.impl.elements;

import com.google.inject.Module;

public interface ElementsBeanFactory {
    public ElementsBean createElementsBean(Module... modules);
}
