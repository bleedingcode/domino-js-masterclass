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

import com.google.gson.Gson;

/**
 * @author Payul Withers
 *
 * @param <T>
 * 
 *            ResultParser for parsing JSON result into an object
 */
public class ResultParser<T> {

	private final Class<T> clazz;
	private final Gson gson;

	/**
	 * @param clazz
	 *            Class with which to initialise the ResultParser
	 * 
	 * @since 0.5.0
	 */
	public ResultParser(Class<T> clazz) {
		this.clazz = clazz;
		this.gson = ToDoUtils.getGson();
	}

	/**
	 * Converts the JSON string passed into an instance of the relevant class
	 * 
	 * @param jsonString
	 *            String, to convert to an object
	 * @return instance of class {@link #clazz}
	 * 
	 * @since 0.5.0
	 */
	public T parse(String jsonString) {
		return gson.fromJson(jsonString, clazz);
	}

}
