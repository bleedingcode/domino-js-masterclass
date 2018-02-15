package org.openntf.todo;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openntf.todo.model.Store.StoreType;

public class ToDoUtils {
	public static String DEBUG_PREFIX = "[TODO_APP]: ";
	private static boolean PROFILING = false;

	public static void debugPrint(String msg) {
		System.out.println(DEBUG_PREFIX + msg);
	}

	public static long startTimer() {
		return System.currentTimeMillis();
	}

	public static boolean toggleProfiling() {
		PROFILING = !PROFILING;
		System.out.println(DEBUG_PREFIX + "Profiling enabled: " + PROFILING);
		return PROFILING;
	}

	public static void debugTimer(long startTimer, String msg) {
		if (PROFILING) {
			long now = System.currentTimeMillis();
			System.out.println(DEBUG_PREFIX + msg + " completed in " + (now - startTimer) + " milliseconds");
		}
	}

	public static boolean validateBody(Map<String, Object> body, String... keys) {
		for (String key : keys) {
			if (!body.containsKey(key)) {
				return false;
			}
		}
		return true;
	}

	public static StoreType validateStoreType(String type) {
		for (StoreType st : StoreType.values()) {
			if (StringUtils.equalsIgnoreCase(st.getValue(), type)) {
				return st;
			}
		}
		return null;
	}

}
