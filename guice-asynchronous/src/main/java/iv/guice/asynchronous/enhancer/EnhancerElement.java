package iv.guice.asynchronous.enhancer;

import java.lang.reflect.Type;

import net.sf.cglib.proxy.Enhancer;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.TypeLiteral;
import com.google.inject.TypeLiteralFactory;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;

public class EnhancerElement<T> implements Element {

	private final Object source;
	private final Key<T> key;
	private final Enhancer enhancer;
	
	public EnhancerElement(Object source, Key<T> key, Enhancer enhancer) {
		this.source = source;
		this.key = key;
		this.enhancer = enhancer;
	}
	
	public Object getSource() {
		return source;
	}

	public void applyTo(Binder binder) {
		if(this.source!=null) binder = binder.withSource(source);
		final Key<T> key = this.getKey();
		
		PrivateBinder privateBinder = binder.newPrivateBinder();
		
		privateBinder.bind(Enhancer.class).toInstance(enhancer);
		
		Type mainType = EnhancerProvider.class;
		Type genaricType = key.getTypeLiteral().getType();
		TypeLiteral<EnhancerProvider<T>> enhancerProvider = TypeLiteralFactory.createParameterizedTypeLiteral(mainType, genaricType);
		privateBinder.bind(key).toProvider(enhancerProvider);
		
		privateBinder.expose(key);
	}
	
	public Key<T> getKey() {
		return key;
	}
	
	public Enhancer getEnhancer() {
		return enhancer;
	}

	public <V> V acceptVisitor(ElementVisitor<V> visitor) {
		// not needed
		throw new UnsupportedOperationException();
	}
	
	public static <T> EnhancerElement<T> createEnhancerElement(AopClass<T> aopClass, Enhancer enhancer) {
		final Object source = aopClass.getSource();
		final Key<T> key = aopClass.getKey();
		
		return new EnhancerElement<T>(source, key, enhancer);
	}
}
