package biz.deinum.multitenant.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskInterceptorChain {

	private final List<TaskInterceptor> interceptors;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public TaskInterceptorChain(List<TaskInterceptor> taskInterceptors) {
		this.interceptors = new ArrayList<>();
		if (taskInterceptors != null) {
			this.interceptors.addAll(taskInterceptors);
		}
	}
	
	public void applyBeforeTask() throws Exception {
		for (TaskInterceptor taskInterceptor: this.interceptors) {
			logger.trace("Invoking before-task interceptor {}'", taskInterceptor);
			taskInterceptor.beforeExecution();
		}
	}
	
	public void applyAfterTask() throws Exception {
		// Unwind the chain by running the last interceptor executed and move backwards to the start
		for (int i = this.interceptors.size() - 1; i >= 0; i--) {
			TaskInterceptor taskInterceptor = this.interceptors.get(i);
			logger.trace("Invoking after-task interceptor {}'", taskInterceptor);
			taskInterceptor.afterExecution();
		}
	}
}
