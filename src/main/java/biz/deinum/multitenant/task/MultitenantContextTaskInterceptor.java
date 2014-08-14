package biz.deinum.multitenant.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.deinum.multitenant.core.ContextHolder;

/**
 * {@link TaskInterceptor} implementation that manages the multi-tenant context 
 * information in {@link ContextHolder}.
 * 
 * <p>
 * <b>THIS CLASS IS NOT THREAD-SAFE.</b>
 * </p>
 */
public class MultitenantContextTaskInterceptor implements TaskInterceptor {

	private final String tenantContext;
	
	private String originalTenantContext;
	
	/**
	 * Creates an instance that will use the instantiating thread's tenant context during the
	 *execution of the task. 
	 */
	public MultitenantContextTaskInterceptor() {
		this(ContextHolder.getContext());
	}
	
	/**
	 * Creates an instance that will place the given tenant context holder into the {@link ContextHolder}.
	 * 
	 * @param tenantContext Context to set during execution of the task.  <code>null</code> is a valid
	 * value here if you don't wish the task to execute under any specific tenant context.
	 */
	public MultitenantContextTaskInterceptor(String tenantContext) {
		this.tenantContext = tenantContext;
	}
	
	
	@Override
	public void beforeExecution() throws Exception {
		this.originalTenantContext = ContextHolder.getContext();
		logger.debug("Current tenant context is '{}'", this.originalTenantContext);
		logger.debug("Setting tenant context to '{}'", this.tenantContext);
		ContextHolder.setContext(this.tenantContext);
	}

	@Override
	public void afterExecution() throws Exception {
		logger.debug("Resetting tenant context by to '{}'", this.originalTenantContext);
		ContextHolder.setContext(this.originalTenantContext);
	}

	private final Logger logger = LoggerFactory.getLogger(getClass());
}
