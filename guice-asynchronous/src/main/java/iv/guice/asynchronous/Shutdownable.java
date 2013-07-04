package iv.guice.asynchronous;

public interface Shutdownable {
	public void shutdown() throws InterruptedException;
}
