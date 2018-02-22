package org.openntf.todo.domino;

import org.apache.commons.lang3.StringUtils;
import org.openntf.domino.Database;
import org.openntf.domino.Name;
import org.openntf.domino.Session;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.todo.authentication.ApplicationAuthenticationFactory;
import org.openntf.todo.exceptions.InvalidMetaversalIdException;

public class Utils {

	public static String getCurrentUsername() {
		return Factory.getSession(SessionType.CURRENT).getEffectiveUserName();
	}

	public static String getCommonName(String fullUserName) {
		Session sess = Factory.getSession(SessionType.NATIVE);
		Name name = sess.createName(fullUserName);
		return name.getCommon();
	}

	/**
	 * Converts passed username to username format used by the application
	 * 
	 * @param username
	 *            passed in, may be hierarchical username or just a name like "Fred Bloggs"
	 * @return
	 */
	public static String getAsUsername(String username) {
		if (StringUtils.contains(username, "/")) {
			return username;
		} else {
			String currName = Factory.getSession(SessionType.CURRENT).getEffectiveUserName();
			return "CN=" + username + "/" + StringUtils.substringAfter(currName, "/");
		}
	}

	/**
	 * Converts username to store name
	 * 
	 * @return store in filename format, e.g. for "Paul Withers/Intec" returns paul_withers
	 */
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

	public static String getDbName(Database db) {
		return db.getFilePath().toLowerCase();
	}

	public static boolean validateMetaversalId(String metaversalId) throws InvalidMetaversalIdException {
		if (metaversalId.length() == 48) {
			return true;
		} else {
			throw new InvalidMetaversalIdException();
		}
	}

	public static String getReplicaIdFromMetaversalId(String metaversalId) throws InvalidMetaversalIdException {
		validateMetaversalId(metaversalId);
		return StringUtils.left(metaversalId, 16);
	}

}
