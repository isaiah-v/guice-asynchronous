package iv.guice.asynchronous.helpers.interceptor;

public interface Callback <T> {
	public void onSuccess(T result);
	public void onFail(Throwable th);
}
