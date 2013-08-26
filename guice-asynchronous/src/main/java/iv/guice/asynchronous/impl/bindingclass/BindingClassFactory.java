package iv.guice.asynchronous.impl.bindingclass;

import iv.guice.asynchronous.impl.elements.ElementContainer;

public interface BindingClassFactory {
    public BindingClass<?>[] getBindingClasses(ElementContainer elements);
}
