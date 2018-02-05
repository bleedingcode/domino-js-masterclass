package org.openntf.todo.authentication;

import java.util.ServiceLoader;

public class Authenticator {
	private boolean authenticationFactoriesSearched;
	private IAuthenticationFactory authenticationFactory;
	private static Authenticator INSTANCE = null;

	private Authenticator() {

	}

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
	 */
	public void setAuthenticationFactory(IAuthenticationFactory factory) {
		this.authenticationFactoriesSearched = true;
		this.authenticationFactory = factory;
	}

}
