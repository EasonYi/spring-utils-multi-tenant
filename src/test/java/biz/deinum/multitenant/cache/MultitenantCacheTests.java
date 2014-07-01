package biz.deinum.multitenant.cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import biz.deinum.multitenant.aop.target.TargetLookupFailureException;
import biz.deinum.multitenant.core.ContextHolder;

public class MultitenantCacheTests {

	private Cache delegate;
	
	@Before
	public void setUp() {
		this.delegate = new ConcurrentMapCache("");
	}
	
	@Test
	public void testSingleArgConstructorDoesNotRequireContext() {
		MultitenantCache c = new MultitenantCache(this.delegate);
		Assert.assertFalse(c.isContextRequired());
	}
	
	@Test(expected=TargetLookupFailureException.class)
	public void testFailureIfNullContextAndContextRequired() {
		MultitenantCache c = new MultitenantCache(this.delegate, true);
		ContextHolder.setContext(null);
		c.get(null);
	}
	
	@Test(expected=TargetLookupFailureException.class)
	public void testFailureIfEmptyStringContextAndContextRequired() {
		MultitenantCache c = new MultitenantCache(this.delegate, true);
		ContextHolder.setContext("");
		c.get(null);
	}
	
	@Test(expected=TargetLookupFailureException.class)
	public void testFailureIfAllWhitespaceContextAndContextRequired() {
		MultitenantCache c = new MultitenantCache(this.delegate, true);
		ContextHolder.setContext(" ");
		c.get(null);
	}
	
	@Test
	public void testContextSwitch() {
		MultitenantCache c = new MultitenantCache(this.delegate, false);
		String key = "key";
		String context1 = "context1";
		String context2 = "context2";
		ContextHolder.setContext(context1);
		c.put(key, context1);
		Assert.assertEquals(context1, c.get(key).get());
		// Now change the context and you shouldn't see the old value
		ContextHolder.setContext(context2);
		Assert.assertNull(c.get(key));
		// Now set the context back to 1 and it should be equal again
		ContextHolder.setContext(context1);
		Assert.assertEquals(context1, c.get(key).get());
	}
	
	@Test
	public void testContextSwitchWithBothValues() {
		MultitenantCache c = new MultitenantCache(this.delegate, false);
		String key = "key";
		String context1 = "context1";
		String context2 = "context2";
		ContextHolder.setContext(context1);
		c.put(key, context1);
		Assert.assertEquals(context1, c.get(key).get());
		// Now change the context and you shouldn't see the old value
		ContextHolder.setContext(context2);
		Assert.assertNull(c.get(key));
		c.put(key, context2);
		Assert.assertNotNull(c.get(key));
		Assert.assertEquals(context2, c.get(key).get());
		// Now set the context back to 1 and it should be equal again
		ContextHolder.setContext(context1);
		Assert.assertNotNull(c.get(key));
		Assert.assertEquals(context1, c.get(key).get());
	}
	
	@Test
	public void testNoContext() {
		MultitenantCache c = new MultitenantCache(this.delegate, false);
		String key = "key";
		String value = "value";
		ContextHolder.setContext(null);
		c.put(key, value);
		Assert.assertNotNull(c.get(key));
		Assert.assertEquals(value, c.get(key).get());
		// Set the context, shouldn't be there now
		ContextHolder.setContext("somethign else");
		Assert.assertNull(c.get(key));
		
	}
}
