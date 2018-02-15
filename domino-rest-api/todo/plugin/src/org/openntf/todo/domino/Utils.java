package org.openntf.todo.domino;

import org.apache.commons.lang3.StringUtils;
import org.openntf.domino.Name;
import org.openntf.domino.Session;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.todo.authentication.ApplicationAuthenticationFactory;

public class Utils {

	public static String getCurrentUsername() {
		return Factory.getSession(SessionType.CURRENT).getEffectiveUserName();
	}

	public static String getAsUsername(String username) {
		if (StringUtils.contains(username, "/")) {
			return username;
		} else {
			String currName = Factory.getSession(SessionType.CURRENT).getEffectiveUserName();
			return "CN=" + username + "/" + StringUtils.substringAfter(currName, "/");
		}
	}

	public static String getPersonalStoreName() {
		String name;
		Session sess = Factory.getSession(SessionType.CURRENT);
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
