// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import android.os.Looper;

import com.onecase.sdk.BaseConfig;
import com.onecase.sdk.handler.OCHandler;
import com.onecase.sdk.log.Log;

/**
 * Task runner impl
 * 
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
class TaskRunnerImpl implements ThreadIdleListener, ITaskRunner {
	private final static int MAX_THREAD_COUNT = BaseConfig.MAX_TASK_RUNNER_THREADS;
	private final static String TAG = "OneCase.TaskRunnerImpl";
	
	private HashMap<Long, OCHandler> threadMap;
	private HashSet<Long> runningThreadSet;
	
	public TaskRunnerImpl() {
		runningThreadSet = new HashSet<Long>(MAX_THREAD_COUNT);
		threadMap = new HashMap<Long, OCHandler>(MAX_THREAD_COUNT);
		for (int i = 0; i < MAX_THREAD_COUNT; i++) {
			Thread t = new Thread(TAG + " THUMB DECODE") {
				public void run() {
					Looper.prepare();
					OCHandler handler = new OCHandler(Looper.myLooper());
					threadMap.put(getId(), handler);
					Looper.loop();
				};
			};
			t.setPriority(Thread.MAX_PRIORITY);
			t.start();
		}
	}
	
	/**
	 * 自动找Handler来执行Task
	 */
	@Override
	public synchronized boolean startTaskAuto(BaseTask task) {
		if (task == null) {
			Log.e(TAG, "task is null");
			return false;
		}
		task.setIdleListener(this);
		Log.d(TAG, "running threads %s", runningThreadSet.toString());
		for (Entry<Long, OCHandler> entry : threadMap.entrySet()) {
			if (!runningThreadSet.contains(entry.getKey())) {
				entry.getValue().post(task);
				runningThreadSet.add(entry.getKey());
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void clearTask() {
		Log.i(TAG, "Clear task");
		for (OCHandler handler : threadMap.values()) {
			handler.removeCallbacksAndMessages(null);
		}
	}

	public void finish() {
		Log.i(TAG, "Finish the taskRunner");
		clearTask();
		for (OCHandler handler : threadMap.values()) {
			handler.getLooper().getThread().interrupt();
			handler.getLooper().quit();
		}
	}

	@Override
	public void idle(Long id) {
		idleThread(id);
	}
	
	private synchronized void idleThread(Long id) {
		if (runningThreadSet.remove(id)) {
			Log.d(TAG, "thread is idle, id=%d", id);
		}
	}
}
