package biz.deinum.multitenant.cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;

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
}
