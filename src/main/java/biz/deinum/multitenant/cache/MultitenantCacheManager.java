package biz.deinum.multitenant.cache;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import biz.deinum.multitenant.aop.target.TargetLookupFailureException;
import biz.deinum.multitenant.core.ContextHolder;

/**
 * A simple {@link CacheManager} implementation that provides a form of multi-tenancy by translating
 * each cache name requested by clients into a new cache name that is specific to the multi-tenant
 * context defined by {@link ContextHolder}.    
 * 
 * Cache names are translated as follows:  Given a cache name of 'cacheName' and multi-tenant
 * context of 'context', the translated cache name will be 'cacheName.context'.  
 * 
 * @author Joe Laudadio (Joe.Laudadio@AltegraHealth.com)
 *
 */
public final class MultitenantCacheManager implements CacheManager {

	private final CacheManager delegate;
	private final boolean contextRequired;
	
	/**
	 * Creates a new {@link MultitenantCacheManager} that wraps the given delegate. The contextRequired
	 * parameter defines whether or not calls to {@link #getCache(String)} should fail if there is no
	 * Multitentnat context defined by the {@link ContextHolder}.
	 * @param delegate
	 * @param contextRequired
	 */
	public MultitenantCacheManager(final CacheManager delegate, final boolean contextRequired) {
		if (delegate == null) {
			throw new NullPointerException("delegate may not be null");
		}
		this.delegate = delegate;
		this.contextRequired = contextRequired;
	}

	/**
	 * Convenience constructor equivalent to {@link #MultitenantCacheManager(CacheManager, false)}
	 * @param delegate
	 * @throws TargetLookupFailureException
	 */
	public MultitenantCacheManager(final CacheManager delegate) throws TargetLookupFailureException {
		this(delegate, false);
	}
	
	@Override
	public Cache getCache(String name) {
		logger.debug("Translating cache name for '{}'", name);
		String translatedName = name;
		String context = ContextHolder.getContext();
		logger.debug("Multitenant context is '{}'", context);
		if (context != null && !context.trim().isEmpty()) {
			translatedName = translatedName + "." + context;
		}
		else if (this.contextRequired) {
			throw new TargetLookupFailureException("Multitenant context is not set and is required");
		}
		logger.debug("Translated cache name is '{}'", translatedName);
		return this.delegate.getCache(translatedName);
	}

	@Override
	public Collection<String> getCacheNames() {
		return this.delegate.getCacheNames();
	}
	
	public boolean isContextRequired() {
		return contextRequired;
	}
	
	private final Logger logger = LoggerFactory.getLogger(MultitenantCacheManager.class);
}
