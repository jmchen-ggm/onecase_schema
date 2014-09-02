// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk.log;

/**
 * Log interface
 * 
 * @author jiaminchen, [jiaminchen@tencent.com]
 **/
public interface ILog {
	
	public void logV(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);
	
	public void logD(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);
	
	public void logI(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);
	
	public void logW(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);
	
	public void logE(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);
	
	public void logF(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);
	
	public int getLogLevel();
	
	public void appenderClose();
	
	public void appenderFlush();
}
