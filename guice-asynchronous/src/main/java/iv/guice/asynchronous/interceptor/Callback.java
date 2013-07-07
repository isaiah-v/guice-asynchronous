package iv.guice.asynchronous.interceptor;

public interface Callback <T> {
	public void onSuccess(T result);
	public void onFail(Throwable th);
}
