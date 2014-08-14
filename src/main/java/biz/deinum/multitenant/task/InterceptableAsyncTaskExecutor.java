package biz.deinum.multitenant.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.springframework.core.task.AsyncTaskExecutor;

/**
 * Spring {@link AsyncTaskExecutor} implementation that allows you to attached interceptors
 * that will execute before and after some task executes.
 * 
 * @author Joe Laudadio <Joe.Laudadio@AltegraHealth.com>
 *
 */
public class InterceptableAsyncTaskExecutor implements AsyncTaskExecutor {

	private final AsyncTaskExecutor delegateTaskExecutor;
	private final List<TaskInterceptor> interceptors;
	
	public InterceptableAsyncTaskExecutor(AsyncTaskExecutor delegate, List<TaskInterceptor> taskInterceptors) 
	{
		Objects.requireNonNull(delegate);
		this.delegateTaskExecutor = delegate;
		this.interceptors = new ArrayList<>();
		if (taskInterceptors != null) {
			this.interceptors.addAll(taskInterceptors);
		}
	}
	
	@Override
	public void execute(Runnable task) {
		Objects.requireNonNull(task);
		Runnable wrapped = wrap(task);
		this.delegateTaskExecutor.execute(wrapped);
	}

	@Override
	public void execute(Runnable task, long startTimeout) {
		Objects.requireNonNull(task);
		Runnable wrapped = wrap(task);
		this.delegateTaskExecutor.execute(wrapped, startTimeout);
	}
	
	@Override
	public Future<?> submit(Runnable task) {
		Objects.requireNonNull(task);
		Runnable wrapped = wrap(task);
		return this.delegateTaskExecutor.submit(wrapped);
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		Objects.requireNonNull(task);
		Callable<T> wrapped = wrap(task);
		return this.delegateTaskExecutor.submit(wrapped);
	}
	
	private TaskInterceptorChain buildInterceptorChain() {
		TaskInterceptorChain chain = new TaskInterceptorChain(this.interceptors);
		return chain;
	}
	
	private Runnable wrap(Runnable task) {
		return new InterceptableRunnable(task, buildInterceptorChain());
	}
	
	private <V> Callable<V> wrap(Callable<V> task) {
		return new InterceptableCallable<V>(task, buildInterceptorChain());
	}
}
