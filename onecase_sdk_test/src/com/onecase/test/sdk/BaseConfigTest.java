// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.test.sdk;

import com.onecase.sdk.BaseConfig;

import junit.framework.Assert;
import android.test.AndroidTestCase;

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
