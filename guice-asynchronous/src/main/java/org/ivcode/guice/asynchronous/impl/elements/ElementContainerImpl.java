package org.ivcode.guice.asynchronous.impl.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.spi.DisableCircularProxiesOption;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
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

public class ElementContainerImpl implements ElementContainer {

    private Map<Key<?>, Binding<?>> bindings;
    private List<DisableCircularProxiesOption> disableCircularProxiesOptions;
    private List<InjectionRequest<?>> injectionRequests;
    private List<InterceptorBinding> interceptorBindings;
    private List<MembersInjectorLookup<?>> membersInjectorLookups;
    private List<Message> messages;
    private List<PrivateElements> privateElements;
    private List<ProviderLookup<?>> providerLookups;
    private List<RequireExplicitBindingsOption> requireExplicitBindingsOptions;
    private List<ScopeBinding> scopeBindings;
    private List<StaticInjectionRequest> staticInjectionRequests;
    private List<TypeConverterBinding> typeConverterBindings;
    private List<TypeListenerBinding> typeListenerBindings;
    private List<Element> others;

    public Map<Key<?>, Binding<?>> getBindings() {
        if (bindings == null)
            bindings = new HashMap<Key<?>, Binding<?>>();
        return bindings;
    }

    public List<DisableCircularProxiesOption> getDisableCircularProxiesOptions() {
        if (disableCircularProxiesOptions == null)
            disableCircularProxiesOptions = new ArrayList<DisableCircularProxiesOption>();
        return disableCircularProxiesOptions;
    }

    public List<InjectionRequest<?>> getInjectionRequests() {
        if (injectionRequests == null)
            injectionRequests = new ArrayList<InjectionRequest<?>>();
        return injectionRequests;
    }

    public List<InterceptorBinding> getInterceptorBindings() {
        if (interceptorBindings == null)
            interceptorBindings = new ArrayList<InterceptorBinding>();
        return interceptorBindings;
    }

    public List<MembersInjectorLookup<?>> getMembersInjectorLookups() {
        if (membersInjectorLookups == null)
            membersInjectorLookups = new ArrayList<MembersInjectorLookup<?>>();
        return membersInjectorLookups;
    }

    public List<Message> getMessages() {
        if (messages == null)
            messages = new ArrayList<Message>();
        return messages;
    }

    public List<PrivateElements> getPrivateElements() {
        if (privateElements == null)
            privateElements = new ArrayList<PrivateElements>();
        return privateElements;
    }

    public List<ProviderLookup<?>> getProviderLookups() {
        if (providerLookups == null)
            providerLookups = new ArrayList<ProviderLookup<?>>();
        return providerLookups;
    }

    public List<RequireExplicitBindingsOption> getRequireExplicitBindingsOptions() {
        if (requireExplicitBindingsOptions == null)
            requireExplicitBindingsOptions = new ArrayList<RequireExplicitBindingsOption>();
        return requireExplicitBindingsOptions;
    }

    public List<ScopeBinding> getScopeBindings() {
        if (scopeBindings == null)
            scopeBindings = new ArrayList<ScopeBinding>();
        return scopeBindings;
    }

    public List<StaticInjectionRequest> getStaticInjectionRequests() {
        if (staticInjectionRequests == null)
            staticInjectionRequests = new ArrayList<StaticInjectionRequest>();
        return staticInjectionRequests;
    }

    public List<TypeConverterBinding> getTypeConverterBindings() {
        if (typeConverterBindings == null)
            typeConverterBindings = new ArrayList<TypeConverterBinding>();
        return typeConverterBindings;
    }

    public List<TypeListenerBinding> getTypeListenerBindings() {
        if (typeListenerBindings == null)
            typeListenerBindings = new ArrayList<TypeListenerBinding>();
        return typeListenerBindings;
    }

    public List<Element> getOthers() {
        if (others == null)
            others = new ArrayList<Element>();
        return others;
    }

    public Module createModule() {
        List<Element> elements = new ArrayList<Element>();

        if (bindings != null)
            elements.addAll(bindings.values());
        if (disableCircularProxiesOptions != null)
            elements.addAll(disableCircularProxiesOptions);
        if (injectionRequests != null)
            elements.addAll(injectionRequests);
        if (interceptorBindings != null)
            elements.addAll(interceptorBindings);
        if (membersInjectorLookups != null)
            elements.addAll(membersInjectorLookups);
        if (messages != null)
            elements.addAll(messages);
        if (privateElements != null)
            elements.addAll(privateElements);
        if (providerLookups != null)
            elements.addAll(providerLookups);
        if (requireExplicitBindingsOptions != null)
            elements.addAll(requireExplicitBindingsOptions);
        if (scopeBindings != null)
            elements.addAll(scopeBindings);
        if (staticInjectionRequests != null)
            elements.addAll(staticInjectionRequests);
        if (typeConverterBindings != null)
            elements.addAll(typeConverterBindings);
        if (typeListenerBindings != null)
            elements.addAll(typeListenerBindings);
        if (others != null)
            elements.addAll(others);

        return Elements.getModule(elements);
    }

    @Override
    public String toString() {
        return "ElementContainerImpl [bindings=" + bindings
                + ", disableCircularProxiesOptions="
                + disableCircularProxiesOptions + ", injectionRequests="
                + injectionRequests + ", interceptorBindings="
                + interceptorBindings + ", membersInjectorLookups="
                + membersInjectorLookups + ", messages=" + messages
                + ", privateElements=" + privateElements + ", providerLookups="
                + providerLookups + ", requireExplicitBindingsOptions="
                + requireExplicitBindingsOptions + ", scopeBindings="
                + scopeBindings + ", staticInjectionRequests="
                + staticInjectionRequests + ", typeConverterBindings="
                + typeConverterBindings + ", typeListenerBindings="
                + typeListenerBindings + ", others=" + others + "]";
    }
}