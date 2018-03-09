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
package org.openntf.todo.httpService;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.openntf.todo.ToDoUtils;
import org.openntf.todo.domino.ToDoStoreFactory;

import com.ibm.designer.runtime.domino.adapter.ComponentModule;
import com.ibm.designer.runtime.domino.adapter.LCDEnvironment;
import com.ibm.designer.runtime.domino.bootstrap.adapter.HttpServletRequestAdapter;
import com.ibm.designer.runtime.domino.bootstrap.adapter.HttpServletResponseAdapter;
import com.ibm.designer.runtime.domino.bootstrap.adapter.HttpSessionAdapter;

/**
 * @author Paul Withers
 * 
 *         HttpService allows us to run code every 30 seconds
 *
 */
public class ToDoHttpService extends com.ibm.designer.runtime.domino.adapter.HttpService {
	public static ToDoHttpService INSTANCE;
	public boolean hasRun;

	public static ToDoHttpService createInstance(final LCDEnvironment lcdEnv) {
		INSTANCE = new ToDoHttpService(lcdEnv);
		return INSTANCE;
		// This runs before ODA starts and ODA won't start until this completes,
		// so don't use any ODA code here
	}

	/**
	 * @return this HttpService
	 */
	public static ToDoHttpService getInstance() {
		return INSTANCE;
	}

	/**
	 * Constructor
	 * 
	 * @param lcdEnv
	 *            passed from HttpServiceFactory
	 */
	public ToDoHttpService(final LCDEnvironment lcdEnv) {
		super(lcdEnv);
	}

	@Override
	// This method runs 30 seconds after server starts and every 30 seconds
	// thereafter
	public void checkTimeout(final long arg0) {
		// Don't bother running if we've already run
		if (!hasRun) {
			// In case it takes too long, set hasRun to true to it won't run again.
			// If there's a serious issue, we have a bigger problem!
			hasRun = true;
			// ToDoUtils.toggleProfiling();
			long startTimer = ToDoUtils.startTimer();
			try {
				// Try to load the stores (they might already have been loaded by a REST service call
				ToDoStoreFactory.getInstance().loadStores();
			} catch (Exception e) {
				e.printStackTrace();
			}
			ToDoUtils.debugTimer(startTimer, "Timeout run");
		}
		super.checkTimeout(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.designer.runtime.domino.adapter.HttpService#doService(java.lang.String, java.lang.String,
	 * com.ibm.designer.runtime.domino.bootstrap.adapter.HttpSessionAdapter,
	 * com.ibm.designer.runtime.domino.bootstrap.adapter.HttpServletRequestAdapter,
	 * com.ibm.designer.runtime.domino.bootstrap.adapter.HttpServletResponseAdapter)
	 */
	@Override
	public boolean doService(final String arg0, final String arg1, final HttpSessionAdapter arg2,
			final HttpServletRequestAdapter arg3, final HttpServletResponseAdapter arg4)
			throws ServletException, IOException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.designer.runtime.domino.adapter.HttpService#getModules(java.util.List)
	 */
	@Override
	public void getModules(final List<ComponentModule> arg0) {

	}

	// This gives the HttpService a priority level
	@Override
	public int getPriority() {
		return 50;
	}

}
