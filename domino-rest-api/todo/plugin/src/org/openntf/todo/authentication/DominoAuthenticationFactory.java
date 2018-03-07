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

import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.xsp.session.DasCurrentSessionFactory;

import com.ibm.domino.osgi.core.context.ContextInfo;

/**
 * @author Paul Withers
 * 
 *         IAuthenticationFactory for using the Basic Authentication credentials passed and ensuring valid credentials
 *         were passed from the REST service.
 *
 */
public class DominoAuthenticationFactory implements IAuthenticationFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openntf.todo.authentication.IAuthenticationFactory#isAuthenticated(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public boolean isAuthenticated(HttpServletRequest request) {
		boolean anon = ContextInfo.isAnonymous();
		Factory.setSessionFactory(new DasCurrentSessionFactory(null), SessionType.CURRENT);
		return !anon;
	}

}
