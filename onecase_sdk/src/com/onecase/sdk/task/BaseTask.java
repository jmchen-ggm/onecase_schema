// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.task;

/**
 * Base Task
 * 
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public abstract class BaseTask implements Runnable {
	
	private ThreadIdleListener idleListener;
	private TaskFinishListener finishListener;

	public BaseTask() {
		this(null);
	}
	
	public BaseTask(TaskFinishListener finishListener) {
		this.finishListener = finishListener;
	}
	
	void setIdleListener(ThreadIdleListener idleListener) {
		this.idleListener = idleListener;
	}
	
	@Override
	public void run() {
		Exception exception = null;
		boolean isSuccess = false;
		try {
			execute();
			isSuccess = true;
		} catch (Exception e) {
			isSuccess = false;
			exception = e;
		} finally {
			if (idleListener != null) {
				idleListener.idle(Thread.currentThread().getId());
				idleListener = null;
			}
			if (finishListener != null) {
				finishListener.onFinish(this, isSuccess, exception);
			}
		}
	}

	protected abstract void execute();
}
