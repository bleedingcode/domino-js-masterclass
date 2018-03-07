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

import com.ibm.designer.runtime.domino.adapter.HttpService;
import com.ibm.designer.runtime.domino.adapter.IServiceFactory;
import com.ibm.designer.runtime.domino.adapter.LCDEnvironment;

/**
 * @author Paul Withers
 * 
 *         An HttpServiceFactory allows us to load HttpServices which run code every 30 seconds, used for caching. See
 *         Paul Withers' blog
 *
 */
public class HttpServiceFactory implements IServiceFactory {

	@Override
	public HttpService[] getServices(final LCDEnvironment lcdEnv) {
		final HttpService[] ret = new HttpService[1];
		ret[0] = org.openntf.todo.httpService.HttpService.createInstance(lcdEnv);
		return ret;
	}

}
