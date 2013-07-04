package iv.guice.asynchronous.impl.aopclass;

import java.util.Arrays;

import com.google.inject.Key;

public class AopClass<T> {
	private Key<T> key;
	private Object source;
	private AopMethod[] methods;
	
	public Key<T> getKey() {
		return key;
	}
	public void setKey(Key<T> key) {
		this.key = key;
	}
	public AopMethod[] getMethods() {
		return methods;
	}
	public void setMethods(AopMethod[] methods) {
		this.methods = methods;
	}
	public Object getSource() {
		return source;
	}
	public void setSource(Object source) {
		this.source = source;
	}
	@Override
	public String toString() {
		return "AopClass [key=" + key + ", source=" + source + ", methods="
				+ Arrays.toString(methods) + "]";
	}
}
