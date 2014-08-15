package biz.deinum.multitenant.task;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import biz.deinum.multitenant.core.ContextHolder;

/**
 * Spring {@link TaskExecutor} implementation that is aware of multitenant context and takes
 * care of setting the context in the {@link ContextHolder} on whatever thread handles the task
 * execution.  The executation task's tenant context is inherited from whatever the tenant context
 * is on the thread that calls {@link #execute(Runnable)}.
 * 
 * @author Joe Laudadio <Joe.Laudadio@AltegraHealth.com>
 *
 */
public class MultitenantTaskExecutor implements AsyncTaskExecutor {

	private final AsyncTaskExecutor delegate;
	
	public MultitenantTaskExecutor(AsyncTaskExecutor delegate) {
		Objects.requireNonNull(delegate);
		this.delegate = delegate;
	}
	
	@Override
	public void execute(Runnable task) {
		Objects.requireNonNull(task);
		Runnable r = wrap(task);
		this.delegate.execute(r);
	}

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void execute(Runnable task, long startTimeout) {
		Objects.requireNonNull(task);
		Runnable r = wrap(task);
		this.delegate.execute(r, startTimeout);
	}

	@Override
	public Future<?> submit(Runnable task) {
		Objects.requireNonNull(task);
		Runnable r = wrap(task);
		return this.delegate.submit(r);
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		Objects.requireNonNull(task);
		Callable<T> c = wrap(task);
		return this.delegate.submit(c);
	}
	
	private Runnable wrap(Runnable task) {
		TaskInterceptor interceptor = createInterceptor();
		return new InterceptableRunnable(task, Collections.singletonList(interceptor));
	}
	
	private <V> Callable<V> wrap(Callable<V> task) {		
		TaskInterceptor interceptor = createInterceptor();
		return new InterceptableCallable(task, Collections.singletonList(interceptor));
	}
	
	private MultitenantContextTaskInterceptor createInterceptor() {
		String currentContext = ContextHolder.getContext();
		logger.debug("current context = {}", currentContext);
		return new MultitenantContextTaskInterceptor(currentContext);		
	}
}
