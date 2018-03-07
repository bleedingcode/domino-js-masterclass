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

import org.openntf.todo.ODADataServlet;

/**
 * 
 * @author Paul Withers
 * 
 *         This interface allows us to use multiple authentication methods, picking the relevant one via the
 *         META-INF/services file or programmatically setting it via
 *         {@link Authenticator#setAuthenticationFactory(IAuthenticationFactory)}. Whichever authentication factory is
 *         used, we can call the {@link #isAuthenticated(HttpServletRequest)} method to provide
 *         AuthenticationFactory-specific code for how to work out whether or not the user is authenticated.
 * 
 */
public interface IAuthenticationFactory {

	/**
	 * @param request
	 *            HttpServletRequest. The method will be called from the {@link ODADataServlet}. This will allow the
	 *            code to load the SessionFactory with the relevant CURRENT session.
	 * @return boolean whether an authenticated REST service session can be identified
	 */
	public boolean isAuthenticated(HttpServletRequest request);

}
