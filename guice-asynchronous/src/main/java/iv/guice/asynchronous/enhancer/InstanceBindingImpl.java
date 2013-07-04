package iv.guice.asynchronous.enhancer;

import java.util.Set;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.InstanceBinding;

public class InstanceBindingImpl<T> implements InstanceBinding<T> {

	private final Key<T> key;
	private final T instance;
	private final Object source;
	
	public InstanceBindingImpl(Key<T> key, T instance, Object source) {
		this.key = key;
		this.instance = instance;
		this.source = source;
	}
	
	@Override
	public Key<T> getKey() {
		return key;
	}

	@Override
	public Provider<T> getProvider() {
		// no provider
		return null;
	}

	@Override
	public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <V> V acceptScopingVisitor(BindingScopingVisitor<V> visitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getSource() {
		return source;
	}

	@Override
	public <V> V acceptVisitor(ElementVisitor<V> visitor) {
		return visitor.visit(this);
	}

	@Override
	public void applyTo(Binder binder) {
		if(source!=null)
			binder = binder.withSource(source);
		binder.bind(getKey()).toInstance(getInstance());
	}

	@Override
	public Set<Dependency<?>> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T getInstance() {
		return instance;
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		// TODO Auto-generated method stub
		return null;
	}
}
