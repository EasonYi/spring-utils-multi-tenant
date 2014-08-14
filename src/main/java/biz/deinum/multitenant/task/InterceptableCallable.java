package biz.deinum.multitenant.task;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * A {@link Callable} implementation that allows you to wrap {@link TaskInterceptor}s around
 * the execution of some other {@link Callable} task.
 * 
 * @author Joe Laudadio (Joe.Laudadio@AltegraHealth.com)
 *
 * @param <V> The {@link Callable#call()} method return type
 */
class InterceptableCallable<V> implements Callable<V> {

	private final Callable<V> task;
	private final TaskInterceptorChain interceptorChain;
	
	public InterceptableCallable(Callable<V> task, List<TaskInterceptor> interceptors) {
		this(task, new TaskInterceptorChain(interceptors));
	}
	
	public InterceptableCallable(Callable<V> task, TaskInterceptorChain interceptorChain) {
		Objects.requireNonNull(task);
		this.task = task;
		this.interceptorChain = interceptorChain;
	}
	
	@Override
	public V call() throws Exception {
		if (this.interceptorChain != null) {
			this.interceptorChain.applyBeforeTask();
		}
		
		V result = this.task.call();
		
		if (this.interceptorChain != null) {
			this.interceptorChain.applyAfterTask();
		}
		
		return result;
	}
}