package iv.guice.asynchronous.impl.utils;

import com.google.inject.Provider;

public interface AssistedProvider<T> extends Provider<T> {

    public T get(Object[] arguments);

    public Class<?>[] getArgumentTypes();

    public int getArgumentCount();

    public Object getArgumentInstance(int index);
    public Object[] getArgumentInstances();
}
