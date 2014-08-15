package biz.deinum.multitenant.task;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Runnable} implementation that allows you to wrap {@link TaskInterceptor}s around
 * the execution of some other {@link Runnable} task.
 * 
 * @author Joe Laudadio (Joe.Laudadio@AltegraHealth.com)
 *
 */
class InterceptableRunnable implements Runnable {

	private final Runnable task;
	private final TaskInterceptorChain interceptorChain;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public InterceptableRunnable(Runnable task, List<TaskInterceptor> interceptors) {
		this(task, new TaskInterceptorChain(interceptors));
	}
	
	public InterceptableRunnable(Runnable task, TaskInterceptorChain interceptorChain) {
		Objects.requireNonNull(task);
		this.task = task;
		this.interceptorChain = interceptorChain;
	}
	
	@Override
	public void run() {
		try {
			if (this.interceptorChain != null) {
				this.interceptorChain.applyBeforeTask();
			}
			
			this.task.run();
			
			if (this.interceptorChain != null) {
				this.interceptorChain.applyAfterTask();
			}
		} catch (Exception ex) {
			logger.error("Failed to execute task", ex);
			throw new RuntimeException(ex);
		}
	}
	
}