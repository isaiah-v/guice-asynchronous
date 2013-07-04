package iv.guice.asynchronous.enhancer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import com.google.inject.internal.PublicInterceptorStackCallback;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.NoOp;

public class EnhancerFactory {
	
	@SuppressWarnings("rawtypes")
	public static Enhancer createEnhancer(Executor executor, AopClass<?> aopClass) {
		Class<?> clazz = aopClass.getKey().getTypeLiteral().getRawType();
		
		Enhancer enhancer = new Enhancer();
		
		enhancer.setSuperclass(clazz);
		enhancer.setUseFactory(false);
		enhancer.setNamingPolicy(new DefaultNamingPolicy());
		enhancer.setClassLoader(clazz.getClassLoader());
		
		Map<Method, Integer> filterMap = new HashMap<Method, Integer>();
		List<Callback> callbackList = new ArrayList<Callback>();
		List<Class> typeList = new ArrayList<Class>(); 
		
		// NoOp at index=0
		callbackList.add(new BasicNoOp());
		typeList.add(NoOp.class);
		
		for(AopMethod method : aopClass.getMethods()) {
			if(method==null) continue;
			
			List<org.aopalliance.intercept.MethodInterceptor> interceptors = method.getInterceptors();
			
			MethodInterceptor mi = interceptors==null ? new DirectInterceptor() : new PublicInterceptorStackCallback(method.getMethod(), interceptors);
			if(method.isAsynchronous())
				mi = new AsynchronusInterceptor(executor, mi);
			
			boolean b1 = callbackList.add(mi);
			boolean b2 = typeList.add(MethodInterceptor.class);
			assert b1 && b2;
			
			Object o = filterMap.put(method.getMethod(), callbackList.size()-1);
			
			// if true, we've mapped the same method twice
			if(o!=null) throw new IllegalStateException();
		}
		
		CallbackFilter callbackFilter = new EnhancerCallbackFilter(filterMap);
		Callback[] callbacks = callbackList.toArray(new Callback[callbackList.size()]);
		Class[] callbackTypes = typeList.toArray(new Class[typeList.size()]);
		
		enhancer.setCallbackFilter(callbackFilter);
		enhancer.setCallbacks(callbacks);
		enhancer.setCallbackTypes(callbackTypes);
		
		return enhancer;
	}
	
	private static class EnhancerCallbackFilter implements CallbackFilter {

		private final Map<Method,Integer> filterMap;
		
		EnhancerCallbackFilter(Map<Method,Integer> filterMap) {
			this.filterMap = filterMap;
		}
		
		@Override
		public int accept(Method method) {
			Integer i = filterMap.get(method);
			return i==null ? 0 : i;
		}
	}
}
