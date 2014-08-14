package biz.deinum.multitenant.task;

/**
 * Workflow interface for things that need to be done before and after tasks executed by Spring's
 * AsyncTaskExecutor. 
 * 
 * @author Joe Laudadio (Joe.Laudadio@AltegraHealth.com)
 *
 */
public interface TaskInterceptor {

	void beforeExecution() throws Exception;
	void afterExecution() throws Exception;
}
