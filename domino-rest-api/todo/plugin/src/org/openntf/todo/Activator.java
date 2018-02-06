/*
 * Copyright 2018
 *
 * @author Paul Withers (pwithers@intec.co.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
package org.openntf.todo;

import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.wink.common.internal.runtime.RuntimeDelegateImpl;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * @author Paul Withers
 * @since 1.0.0
 *
 *        Activator class, nothing to see here, please move along!
 *
 */
public class Activator extends Plugin {

	private static BundleContext context;

	/**
	 * @return BundleContext
	 */
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework. BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		System.out.println("Starting Wink Application...");
		final ClassLoader oldcl = Thread.currentThread().getContextClassLoader();
		final ClassLoader newcl = RuntimeDelegate.class.getClassLoader();

		Thread.currentThread().setContextClassLoader(newcl);
		try {
			RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
		} finally {
			Thread.currentThread().setContextClassLoader(oldcl);
		}
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
