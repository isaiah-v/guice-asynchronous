package iv.guice.asynchronous.impl.aopclass;

import java.util.Arrays;

import com.google.inject.Key;

public class AopConstructor {
    private Class<?>[] argumentTypes;
    private Key<?>[] argumentKeys;
    
    public Class<?>[] getArgumentTypes() {
        return argumentTypes;
    }
    
    public void setArgumentTypes(Class<?>[] argumentTypes) {
        this.argumentTypes = argumentTypes;
    }
    
    public Key<?>[] getArgumentKeys() {
        return argumentKeys;
    }
    
    public void setArgumentKeys(Key<?>[] argumentKeys) {
        this.argumentKeys = argumentKeys;
    }

    @Override
    public String toString() {
        return "AopConstructor [argumentTypes=" + Arrays.toString(argumentTypes) + ", argumentKeys=" + Arrays.toString(argumentKeys) + "]";
    }

}
