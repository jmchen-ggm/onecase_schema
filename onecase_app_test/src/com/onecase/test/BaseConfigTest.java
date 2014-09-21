// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.test;

import junit.framework.Assert;
import android.test.AndroidTestCase;

import com.onecase.sdk.BaseConfig;

/**
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public class BaseConfigTest extends AndroidTestCase {

	public void testIsDebug() {
		Assert.assertTrue(BaseConfig.isDebug());
		BaseConfig.BUILD_STATUS = BaseConfig.BUILD_STATUS_RELEASE;
		Assert.assertTrue(!BaseConfig.isDebug());
	}
}
