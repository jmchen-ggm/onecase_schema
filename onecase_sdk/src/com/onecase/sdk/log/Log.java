// Copyright 2014 By jiaminchen, <jmchen.ggm@gmail.com>
package com.onecase.sdk.log;

import android.os.Looper;
import android.os.Process;

import com.onecase.sdk.BaseConfig;
import com.onecase.sdk.Util;


/**
 * Update At 2014年7月3日 By jiaminchen, <jmchen.ggm@gmail.com>
 * Create At 2014年7月3日 By jiaminchen, <jmchen.ggm@gmail.com>
 **/
public class Log {
	
	private static final String TAG = "Onecase.SDK.Log";

	public static final int LEVEL_VERBOSE = 0;
	public static final int LEVEL_DEBUG = 1;
	public static final int LEVEL_INFO = 2;
	public static final int LEVEL_WARNING = 3;
	public static final int LEVEL_ERROR = 4;
	public static final int LEVEL_FATAL = 5;
	public static final int LEVEL_NONE = 6;
	
	public static int level = BaseConfig.isDebug() ? LEVEL_VERBOSE : LEVEL_NONE;
	
	private static final ILog debugLog = new ILog() {
		
		@Override
		public void logW(String tag, String filename, String funcname, int line,
				int pid, long tid, long maintid, String log) {
			if (level <= LEVEL_WARNING) {
				android.util.Log.w(tag, log);
			}
		}
		
		@Override
		public void logV(String tag, String filename, String funcname, int line,
				int pid, long tid, long maintid, String log) {
			if (level <= LEVEL_VERBOSE) {
				android.util.Log.v(tag, log);
			}
		}
		
		@Override
		public void logI(String tag, String filename, String funcname, int line,
				int pid, long tid, long maintid, String log) {
			if (level <= LEVEL_INFO) {
				android.util.Log.i(tag, log);
			}
		}
		
		@Override
		public void logF(String tag, String filename, String funcname, int line,
				int pid, long tid, long maintid, String log) {
			if (level <= LEVEL_FATAL) {
				android.util.Log.e(tag, log);			
			}
		}
		
		@Override
		public void logE(String tag, String filename, String funcname, int line,
				int pid, long tid, long maintid, String log) {
			if (level <= LEVEL_ERROR) {
				android.util.Log.e(tag, log);
			}
		}
		
		@Override
		public void logD(String tag, String filename, String funcname, int line,
				int pid, long tid, long maintid, String log) {
			if (level <= LEVEL_DEBUG) {
				android.util.Log.d(tag, log);
			}
		}
		
		@Override
		public int getLogLevel() {
			return level;
		}
		
		@Override
		public void appenderFlush() {
			
		}
		
		@Override
		public void appenderClose() {
			
		}
	};
	
	private static ILog logImpl = debugLog;
	
	public static void setLogImp(ILog impl) {
		logImpl = impl;
	}


	public static ILog getImpl(){
		return logImpl;
	}
	
	
	public static void appenderClose() {
		if (logImpl != null) {
			logImpl.appenderClose();
		}
	}
	
	public static void appenderFlush() {
		if (logImpl != null) {
			logImpl.appenderFlush();
		}
	}
	
	public static int getLogLevel() {
		if (logImpl != null) {
			return logImpl.getLogLevel();
		}
		return LEVEL_NONE;
	}
	
	public static void v(String tag, String format, Object... args) {
		String log = Util.notNullStr(String.format(format, args), "");
		if (logImpl != null) {
			logImpl.logV(tag, "", "", 0, Process.myPid(), Thread.currentThread().getId(),
					Looper.getMainLooper().getThread().getId(), log);
		}
	}
	
	public static void d(String tag, String format, Object... args) {
		String log = Util.notNullStr(String.format(format, args), "");
		if (logImpl != null) {
			logImpl.logD(tag, "", "", 0, Process.myPid(), Thread.currentThread().getId(),
					Looper.getMainLooper().getThread().getId(), log);
		}
	}
	
	public static void i(String tag, String format, Object... args) {
		String log = Util.notNullStr(String.format(format, args), "");
		if (logImpl != null) {
			logImpl.logI(tag, "", "", 0, Process.myPid(), Thread.currentThread().getId(),
					Looper.getMainLooper().getThread().getId(), log);
		}
	}
	
	public static void w(String tag, String format, Object... args) {
		String log = Util.notNullStr(String.format(format, args), "");
		if (logImpl != null) {
			logImpl.logW(tag, "", "", 0, Process.myPid(), Thread.currentThread().getId(),
					Looper.getMainLooper().getThread().getId(), log);
		}
	}
	
	public static void e(String tag, String format, Object... args) {
		String log = Util.notNullStr(String.format(format, args), "");
		if (logImpl != null) {
			logImpl.logE(tag, "", "", 0, Process.myPid(), Thread.currentThread().getId(),
					Looper.getMainLooper().getThread().getId(), log);
		}
	}
	
	public static void f(String tag, String format, Object... args) {
		String log = Util.notNullStr(String.format(format, args), "");
		if (logImpl != null) {
			logImpl.logF(tag, "", "", 0, Process.myPid(), Thread.currentThread().getId(),
					Looper.getMainLooper().getThread().getId(), log);
		}
	}
	
	private static final String SYS_INFO;
	static {
		final StringBuilder sb = new StringBuilder();
		sb.append("VERSION.RELEASE:[" + android.os.Build.VERSION.RELEASE);
		sb.append("] VERSION.CODENAME:[" + android.os.Build.VERSION.CODENAME);
		sb.append("] VERSION.INCREMENTAL:[" + android.os.Build.VERSION.INCREMENTAL);
		sb.append("] BOARD:[" + android.os.Build.BOARD);
		sb.append("] DEVICE:[" + android.os.Build.DEVICE);
		sb.append("] DISPLAY:[" + android.os.Build.DISPLAY);
		sb.append("] FINGERPRINT:[" + android.os.Build.FINGERPRINT);
		sb.append("] HOST:[" + android.os.Build.HOST);
		sb.append("] MANUFACTURER:[" + android.os.Build.MANUFACTURER);
		sb.append("] MODEL:[" + android.os.Build.MODEL);
		sb.append("] PRODUCT:[" + android.os.Build.PRODUCT);
		sb.append("] TAGS:[" + android.os.Build.TAGS);
		sb.append("] TYPE:[" + android.os.Build.TYPE);
		sb.append("] USER:[" + android.os.Build.USER + "]");
		SYS_INFO = sb.toString();
		android.util.Log.d(TAG, SYS_INFO);
	}
	
	public static String getSystemInfo() {
		return SYS_INFO;
	}
}
