package org.ivcode.guice.asynchronous.impl.elements;

import java.util.List;
import java.util.Map;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.spi.DisableCircularProxiesOption;
import com.google.inject.spi.Element;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.InterceptorBinding;
import com.google.inject.spi.MembersInjectorLookup;
import com.google.inject.spi.Message;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.ProviderLookup;
import com.google.inject.spi.RequireExplicitBindingsOption;
import com.google.inject.spi.ScopeBinding;
import com.google.inject.spi.StaticInjectionRequest;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.spi.TypeListenerBinding;

public interface ElementContainer {
    public Map<Key<?>, Binding<?>> getBindings();
    public List<DisableCircularProxiesOption> getDisableCircularProxiesOptions();
    public List<InjectionRequest<?>> getInjectionRequests();
    public List<InterceptorBinding> getInterceptorBindings();
    public List<MembersInjectorLookup<?>> getMembersInjectorLookups();
    public List<Message> getMessages();
    public List<PrivateElements> getPrivateElements();
    public List<ProviderLookup<?>> getProviderLookups();
    public List<RequireExplicitBindingsOption> getRequireExplicitBindingsOptions();
    public List<ScopeBinding> getScopeBindings();
    public List<StaticInjectionRequest> getStaticInjectionRequests();
    public List<TypeConverterBinding> getTypeConverterBindings();
    public List<TypeListenerBinding> getTypeListenerBindings();
    public List<Element> getOthers();
    
    public Module createModule();
}
