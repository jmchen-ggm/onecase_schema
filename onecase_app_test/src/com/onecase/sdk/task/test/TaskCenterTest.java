// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.task.test;

import java.util.Random;

import android.test.AndroidTestCase;

import com.onecase.sdk.log.Log;
import com.onecase.sdk.task.BaseTask;
import com.onecase.sdk.task.TaskCenter;
import com.onecase.sdk.task.TaskFinishListener;

/**
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public class TaskCenterTest extends AndroidTestCase {
	private final static String TAG = "Onecase.TaskCenterTask";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testTask() {
		for (int i = 0; i < 10; i++) {
			TaskCenter.getTaskRunner().startTaskAuto(new TestTask(new TaskFinishListener() {
				@Override
				public void onFinish(BaseTask task, boolean isSuccess, Exception e) {
					Log.d(TAG, "finish Task %d", Thread.currentThread().getId());
				}
			}));
		}
		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
		}
	}
	
	class TestTask extends BaseTask {
		private final static String TAG = "Onecase.TestTask";
		
		public TestTask(TaskFinishListener finishListener) {
			super(finishListener);
		}

		@Override
		protected void execute() {
			int second = (int) (10 * new Random().nextFloat());
			Log.d(TAG, "%d Sleep %d", Thread.currentThread().getId(), second);
			try {
				Thread.sleep(second * 1000);
			} catch (InterruptedException e) {
			}
		}
	}
}
