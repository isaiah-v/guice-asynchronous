package iv.guice.asynchronous.helpers.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.RejectedExecutionException;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FailFast {
	public Class<? extends Throwable> exception() default RejectedExecutionException.class;
	public String message() default "Fail Fast";
}
