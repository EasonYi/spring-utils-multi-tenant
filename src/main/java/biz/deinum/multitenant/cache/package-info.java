/**
 * <p>
 * Classes for adding multi-tenancy support for Spring's cache abstraction. The {@link biz.deinum.multitenant.cache.MultitenantCacheManager}
 * class provides a {@link org.springframework.cache.CacheManager} implementation that creates
 * {@link biz.deinum.multitenant.cache.MultitenantCache} caches in a fairly transparent manner.  This
 * package does not provide any actual caching implementations but instead decorates existing cache
 * implementations in such a way that they support mult-tenancy. 
 * </p> 
 * 
 * <p>
 * Here is an example of what your spring configuration might look like:
 * <pre>
 * &lt;bean id="delegateCacheManager" class="org.springframework.cache.support.SimpleCacheManager"&gt;
 *    &lt;property name="caches"&gt;
 *			&lt;list&gt;
 *					&lt;bean class="org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean"&gt;
 *						&lt;property name="name" value="my-cache"/&gt;
 *					&lt;/bean&gt;
 *			&lt;/list&gt;
 *		&lt;/property&gt;
 *	&lt;/bean&gt;
 *
 *	&lt;bean id="cacheManager" class="biz.deinum.multitenant.cache.MultitenantCacheManager"&gt;
 *    &lt;constructor-arg ref="delegateCacheManager"/&gt;
 *    &lt;constructor-arg value="false"/&gt;
 * 	&lt;/bean&gt;
 * </pre>
 * </p>
 *  
 */
package biz.deinum.multitenant.cache;