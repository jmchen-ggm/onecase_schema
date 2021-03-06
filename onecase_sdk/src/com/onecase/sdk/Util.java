// Copyright 2014 By jiaminchen, [jmchen.ggm@gmail.com]
package com.onecase.sdk;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * @author jiaminchen, [jmchen.ggm@gmail.com]
 **/
public class Util {

	public final static boolean isNullOrEmpty(String str) {
		if (str != null && str.trim().length() > 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public final static String notNullStr(String target, String default_) {
		if (target == null) {
			return default_;
		} else {
			return target;
		}
	}
	
	public final static String notNullToString(Object obj) {
		if (obj != null) {
			return obj.toString();
		} else {
			return "null";
		}
	}
	
	public static JMStack getStack() {
		return new JMStack();
	}
	
	public static class JMStack {

		@Override
		public String toString() {
			return getStack(true);
		}

		public static String getStack(final boolean printLine) {
			StackTraceElement[] stes = new Throwable().getStackTrace();
			if ((stes == null) || (stes.length < 4)) {
				return "";
			}
			StringBuilder t = new StringBuilder();

			for (int i = 3; i < stes.length; i++) {
				if (stes[i].getClassName().contains(BaseConstants.LOG_CLASS)) {
					continue;
				}
				t.append("[");
				t.append(stes[i].getClassName());
				t.append(":");
				t.append(stes[i].getMethodName());
				if (printLine) {
					t.append("(" + stes[i].getLineNumber() + ")]");
				} else {
					t.append("]");
				}
			}
			return t.toString();
		}
	}
	
	public static String toJSONString(Object obj) {
		if (obj == null) {
			return "{}";
		} else {
			return JSON.toJSONString(obj);
		}
	}
	
	public static <T> T toJSONObject(String text, Class<T> clazz) {
		if (text == null) {
			try {
				return clazz.newInstance();
			} catch (Exception e) {
				return null;
			}
		} else {
			return JSON.parseObject(text, clazz);
		}
	}
	
	public static <T> List<T> toJSONArray(String text, Class<T> clazz) {
		if (text == null) {
			try {
				return new ArrayList<T>();
			} catch (Exception e) {
				return null;
			}
		} else {
			return JSON.parseArray(text, clazz);
		}
	}
}
