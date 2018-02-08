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
package org.openntf.todo.authentication;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.xsp.session.DasCurrentSessionFactory;
import org.openntf.domino.xsp.session.XPageNamedSessionFactory;

import com.ibm.commons.util.StringUtil;

public class ApplicationAuthenticationFactory implements IAuthenticationFactory {
	private final String API_KEY = "i49chtnbea5h1dfolcqoh2qght";
	public static final String OU = "qemqa5tno4roja5bg71j1puk3h";

	@Override
	public boolean isAuthenticated(HttpServletRequest request) {
		// In reality, this would be held in e.g. notes.ini and API would look up to that
		if (!StringUtil.equalsIgnoreCase(API_KEY, request.getHeader("X-TODO-API-KEY"))) {
			System.out.println("Invalid API key from HTTP Request");
			return false;
		}
		// Build username
		String userKey = request.getHeader("X-TODO-USER-KEY");
		// Temporarily set as Anonymous, so we won't get an error when accessing SessonType.NATIVE

		String serverName = new DasCurrentSessionFactory(null).createSession().getServerName();
		String ORG = "/O=" + StringUtils.substringAfter(serverName, "/O=");
		// Now set the CURRENT session to who we actually want it to be
		Factory.setSessionFactory(new XPageNamedSessionFactory("CN=" + userKey + "/OU=" + OU + ORG, false),
				SessionType.CURRENT);
		return true;
	}

}
