package org.openntf.todo;

import org.apache.commons.lang3.StringUtils;
import org.openntf.domino.Name;
import org.openntf.domino.Session;
import org.openntf.todo.authentication.ApplicationAuthenticationFactory;

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

}
