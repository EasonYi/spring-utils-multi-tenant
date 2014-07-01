package biz.deinum.multitenant.cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;

import biz.deinum.multitenant.aop.target.TargetLookupFailureException;
import biz.deinum.multitenant.core.ContextHolder;

public class MultitenantCacheManagerTests {

	private CacheManager delegate;
	
	@Before
	public void setUp() {
		this.delegate = new NoOpCacheManager();
	}
	
	@Test
	public void testSingleArgConstructorDoesNotRequireContext() {
		MultitenantCacheManager cm = new MultitenantCacheManager(this.delegate);
		Assert.assertFalse(cm.isContextRequired());
	}
	
	@Test(expected=TargetLookupFailureException.class)
	public void testExceptionOccursWhenContextRequiredAndNull() {
		MultitenantCacheManager cm = new MultitenantCacheManager(this.delegate, true);
		ContextHolder.setContext(null);
		cm.getCache("foo");
	}
	
	@Test(expected=TargetLookupFailureException.class)
	public void testExceptionOccursWhenContextRequiredAndEmptyString() {
		MultitenantCacheManager cm = new MultitenantCacheManager(this.delegate, true);
		ContextHolder.setContext("");
		cm.getCache("foo");
	}
	
	@Test(expected=TargetLookupFailureException.class)
	public void testExceptionOccursWhenContextRequiredAndAllWhitespace() {
		MultitenantCacheManager cm = new MultitenantCacheManager(this.delegate, true);
		ContextHolder.setContext("  ");
		cm.getCache("foo");
	}
	
	@Test
	public void testOkWhenNoContextAndNotRequired() {
		MultitenantCacheManager cm = new MultitenantCacheManager(this.delegate, false);
		ContextHolder.setContext(null);
		String name = "foo";
		Cache c = cm.getCache(name);
		Assert.assertEquals(name, c.getName());
	}
	
	@Test
	public void testOkWhenContextAvailableAndRequired() {
		MultitenantCacheManager cm = new MultitenantCacheManager(this.delegate, true);
		String contextName = "context";
		ContextHolder.setContext(contextName);
		String name = "foo";
		Cache c = cm.getCache(name);
		String expectedCacheName = name + "." + contextName;
		Assert.assertEquals(expectedCacheName, c.getName());
	}
	
	@Test
	public void testOkWhenContextAvailableAndNotRequired() {
		MultitenantCacheManager cm = new MultitenantCacheManager(this.delegate, false);
		String contextName = "context";
		ContextHolder.setContext(contextName);
		String name = "foo";
		Cache c = cm.getCache(name);
		String expectedCacheName = name + "." + contextName;
		Assert.assertEquals(expectedCacheName, c.getName());
	}
}
