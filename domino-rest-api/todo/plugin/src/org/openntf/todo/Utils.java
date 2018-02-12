package org.openntf.todo;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openntf.domino.Name;
import org.openntf.domino.Session;
import org.openntf.todo.authentication.ApplicationAuthenticationFactory;
import org.openntf.todo.model.Store.StoreType;

public class Utils {
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

	public static String getPersonalStoreName(Session sess) {
		String name;
		Name username = sess.createName(sess.getEffectiveUserName());
		if (ApplicationAuthenticationFactory.OU.equals(username.getOrgUnit1())) {
			name = username.getCommon();
		} else {
			name = StringUtils.substringBeforeLast(username.getAbbreviated(), "/");
			name = StringUtils.replace(StringUtils.replace(name, "/", "_"), " ", "_");
		}
		return name.toLowerCase();
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
