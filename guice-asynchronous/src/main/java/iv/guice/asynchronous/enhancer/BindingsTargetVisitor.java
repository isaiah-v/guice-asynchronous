package iv.guice.asynchronous.enhancer;


import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.UntargettedBinding;

public class BindingsTargetVisitor extends DefaultBindingTargetVisitor<Object, Key<?>> {
	private final ElementsBean elementViewer;
	
	public BindingsTargetVisitor(ElementsBean elementViewer) {
		this.elementViewer = elementViewer;
	}

	@Override
	public Key<?> visit(LinkedKeyBinding<? extends Object> binding) {
		Binding<?> targetBinding = elementViewer.getBindings().get(binding.getLinkedKey());
		if(targetBinding==null)
			return binding.getLinkedKey();
		else
			return null; // only interested in the end target class
	}

	@Override
	public Key<?> visit(UntargettedBinding<? extends Object> binding) {
		Key<?> key = binding.getKey();
		return key;
	}
}
