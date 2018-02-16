package org.openntf.todo;

import org.apache.commons.lang3.StringUtils;
import org.openntf.todo.model.Store;
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

	public static String getStoreFilePath(String name, StoreType type) {
		name = StringUtils.replace(StringUtils.replace(name, "/", "_"), " ", "_");
		return StringUtils.lowerCase(Store.TODO_PATH + type.getValue() + "/" + name + ".nsf");
	}

}
