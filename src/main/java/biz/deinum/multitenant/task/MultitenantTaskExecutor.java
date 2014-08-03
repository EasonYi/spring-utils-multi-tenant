package biz.deinum.multitenant.task;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class MultitenantTaskExecutor implements TaskExecutor {

	private final TaskExecutor delegateTaskExecutor;
	
	public MultitenantTaskExecutor(TaskExecutor delegate) {
		Objects.requireNonNull(delegate);
		this.delegateTaskExecutor = delegate;
	}
	
	@Override
	public void execute(Runnable task) {
		Objects.requireNonNull(task);
		String currentContext = ContextHolder.getContext();
		logger.debug("current context = {}", currentContext);
		Runnable r = new MultitenantRunnable(task, currentContext);
		this.delegateTaskExecutor.execute(r);
	}

	private static class MultitenantRunnable implements Runnable {

		private final Runnable task;
		private final String runAsTenantContext;
		private final Logger logger = LoggerFactory.getLogger(getClass());
		public MultitenantRunnable(Runnable task, String runAsTenantContext) {
			this.task = task;
			this.runAsTenantContext = runAsTenantContext;
		}
		
		@Override
		public void run() {
			String originalContext = ContextHolder.getContext();
			logger.debug("Starting tenant context: {}", originalContext);
			try {
				logger.debug("Switching tenant context to run-as context: {}", this.runAsTenantContext);
				ContextHolder.setContext(this.runAsTenantContext);
				logger.debug("Executing task");
				this.task.run();
				logger.debug("Task finished");
			}
			finally {
				ContextHolder.setContext(originalContext);
				logger.debug("Restored original tenant context: {}", originalContext);
			}
		}
		
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
}
