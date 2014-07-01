/**
 * Provides some options for using Spring's Cache Abstraction in a multi-tenant environment.  Two
 * strategies are provided: {@link biz.deinum.multitenant.cache.MultitenantCacheManager} and
 * {@link biz.deinum.multitenant.cache.MultitenantCache}.  They are NOT meant to be used together.
 * You would pick one or the other. 
 */
package biz.deinum.multitenant.cache;