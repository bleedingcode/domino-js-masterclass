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

import java.util.HashSet;
import java.util.Set;

import com.ibm.domino.das.service.RestService;

/**
 * @author Paul Withers
 * @since 1.0.0
 *
 *        Main Apache Wink Application class. Add any normal REST endpoint
 *        Resource classes to {@link #getClasses()}. For any singleton endpoint
 *        Resource classes (single REST service instance for all users), add an
 *        instance of the class to {@link #getSingletons()}.
 *
 */
public class ToDoApplication extends RestService {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.wink.common.WinkApplication#getClasses()
	 */
	@Override
	public Set<Class<?>> getClasses() {
		System.out.println("Loading classes");
		final Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(StoresResource.class);
		classes.add(StoreResource.class);
		classes.add(ToDoResource.class);
		classes.add(ToDosResource.class);
		classes.add(UserResource.class);
		classes.add(DebugResource.class);
		classes.add(UnitTestResource.class);
		return classes;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.ws.rs.core.Application#getSingletons()
	 */
	@Override
	public Set<Object> getSingletons() {
		final Set<Object> singletons = new HashSet<Object>();
		// Add any instances of classes to load as singletons here
		return singletons;
	}

}