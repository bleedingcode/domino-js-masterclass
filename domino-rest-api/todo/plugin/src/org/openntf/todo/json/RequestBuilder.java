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
package org.openntf.todo.json;

import org.openntf.todo.ToDoUtils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;

/**
 * @author Paul Withers
 *
 * @param <T>
 *            Class for Gson conversion
 */
public class RequestBuilder<T> {
	private Class<T> clazz;
	private Gson gson;

	/**
	 * @param clazz
	 *            Class with which to initialise the RequestBuilder
	 */
	public RequestBuilder(Class<T> clazz) {
		super();
		this.gson = ToDoUtils.getGson();
		this.clazz = clazz;
	}

	/**
	 * Fields to exclude when converting json request body to Java object
	 */
	ExclusionStrategy dbAccessExclusionStrategy = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipField(FieldAttributes fieldAttributes) {
			if ("dbName".equals(fieldAttributes.getName())) {
				return true;
			}
			return false;
		}

		@Override
		public boolean shouldSkipClass(Class aClass) {
			return false;
		}
	};

	/**
	 * Builds JSON string from the object passed
	 * 
	 * @param object
	 *            Object, instance of {@link #clazz}
	 * @return String, JSON conversion of the parameter passed
	 */
	public String buildJson(T object) {
		return gson.toJson(object);
	}

}
