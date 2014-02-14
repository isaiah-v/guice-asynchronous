package org.ivcode.guice.asynchronous.impl.elements;

import com.google.inject.Module;

public interface ElementContainerFactory {
    public ElementContainer createElementContainer(Module...modules);
}
