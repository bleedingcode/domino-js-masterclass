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

import java.util.ServiceLoader;

/**
 * @author Paul Withers
 * 
 *         The Authenticator allows us to load a specific {@link IAuthenticationFactory} to choose how to authenticate
 *         users
 *
 */
public class Authenticator {
	private boolean authenticationFactoriesSearched;
	private IAuthenticationFactory authenticationFactory;
	private static Authenticator INSTANCE = null;

	/**
	 * Constructor
	 */
	private Authenticator() {

	}

	/**
	 * @return Authenticator as a singleton
	 */
	public static Authenticator getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new Authenticator();
		}
		return INSTANCE;
	}

	/**
	 * Loads the relevant authentication factory based on src/META-INF/services/IAuthenticationFactory
	 * 
	 * @return implementation of IAuthenticationFactory
	 */
	public IAuthenticationFactory getAuthenticationFactory() {
		if (this.authenticationFactoriesSearched) {
			return authenticationFactory;
		}

		this.authenticationFactoriesSearched = true;

		ServiceLoader<IAuthenticationFactory> loader = ServiceLoader.load(IAuthenticationFactory.class);

		for (IAuthenticationFactory factory : loader) {
			this.authenticationFactory = factory;
			break;
		}

		return this.authenticationFactory;

	}

	/**
	 * Setter to override IAuthenticationFactory as set in src/META-INF/services/IAuthenticationFactory. You might want
	 * to do that to use a specific authentication factory for development or testing, for example
	 * 
	 * @param factory
	 *            specific instance of IAuthenticationFactory to install
	 */
	public void setAuthenticationFactory(IAuthenticationFactory factory) {
		this.authenticationFactoriesSearched = true;
		this.authenticationFactory = factory;
	}

}
