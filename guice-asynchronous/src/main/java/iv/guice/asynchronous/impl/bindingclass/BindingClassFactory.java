package iv.guice.asynchronous.impl.bindingclass;

import iv.guice.asynchronous.impl.elements.ElementsBean;

public interface BindingClassFactory {
    public BindingClass<?>[] getBindingClasses(ElementsBean elements);
}
