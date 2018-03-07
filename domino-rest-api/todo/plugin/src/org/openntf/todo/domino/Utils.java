/*******************************************************************************
 * Copyright 2018 Paul Withers
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.openntf.todo.domino;

import org.apache.commons.lang3.StringUtils;
import org.openntf.domino.Database;
import org.openntf.domino.Name;
import org.openntf.domino.Session;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.todo.authentication.ApplicationAuthenticationFactory;
import org.openntf.todo.exceptions.InvalidMetaversalIdException;

/**
 * @author Paul Withers
 * 
 *         Domino-specific Util methods
 *
 */
public class Utils {

	/**
	 * @return current Domino username
	 */
	public static String getCurrentUsername() {
		return Factory.getSession(SessionType.CURRENT).getEffectiveUserName();
	}

	/**
	 * Converts passed username to CommonName format
	 * 
	 * @param fullUserName
	 *            Hierarchical Domino username
	 * @return username in Common Name format
	 */
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
	 * @return username in Domino-specific Hierarchical format
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

	/**
	 * Converts database name to lower case replacing backslashes to forward slashes
	 * 
	 * @param db
	 * @return
	 */
	public static String getDbName(Database db) {
		return StringUtils.replace(db.getFilePath().toLowerCase(), "\\", "/");
	}

	/**
	 * Validates that the metaversalId passed is 48 characters
	 * 
	 * @param metaversalId
	 *            potential metaversalId passed
	 * @return valid
	 * @throws InvalidMetaversalIdException
	 *             exception confirming metaversalId wasn't 48 characters
	 */
	public static boolean validateMetaversalId(String metaversalId) throws InvalidMetaversalIdException {
		if (metaversalId.length() == 48) {
			return true;
		} else {
			throw new InvalidMetaversalIdException();
		}
	}

	/**
	 * Extract replicaId from metaversalId (first 16 characters)
	 * 
	 * @param metaversalId
	 *            potential metaversalId passed
	 * @return first 16 characters
	 * @throws InvalidMetaversalIdException
	 *             exception confirming metaversalId wasn't 48 characters
	 */
	public static String getReplicaIdFromMetaversalId(String metaversalId) throws InvalidMetaversalIdException {
		validateMetaversalId(metaversalId);
		return StringUtils.left(metaversalId, 16);
	}

}
