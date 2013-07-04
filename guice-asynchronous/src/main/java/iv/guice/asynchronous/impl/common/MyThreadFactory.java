package iv.guice.asynchronous.impl.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class MyThreadFactory implements ThreadFactory {

	private static final boolean DEFAULT_IS_DAEMON = false;
	private static final int DEFAULT_PRIORITY = Thread.NORM_PRIORITY;
	
	private final ThreadGroup group;
	private final AtomicInteger count = new AtomicInteger(1);
	
	private final String namePrefix;
	private final boolean isDaemon;
	private final int priority;
	
	public MyThreadFactory(String prefix, boolean isDaemon, int priority) {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		
		this.namePrefix = prefix;
		this.isDaemon = isDaemon;
		this.priority = priority;
	}
	
	public MyThreadFactory(String prefix) {
		this(prefix, DEFAULT_IS_DAEMON, DEFAULT_PRIORITY);
	}
	
	public MyThreadFactory(String prefix, boolean isDaemon) {
		this(prefix, isDaemon, DEFAULT_PRIORITY);
	}
	
	public MyThreadFactory(String prefix, int priority) {
		this(prefix, DEFAULT_IS_DAEMON, priority);
	}
	
	public Thread newThread(Runnable r) {
		Thread value = new Thread(group,r,namePrefix+(count.getAndIncrement()));
		
		if (value.isDaemon()!=isDaemon)
			value.setDaemon(isDaemon);
		if (value.getPriority() != priority)
			value.setPriority(priority);
		
		return value;
	}

}