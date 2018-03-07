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

import java.lang.reflect.Type;

import org.openntf.todo.model.DatabaseAccess.AccessLevel;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Paul Withers
 * 
 *         Class to convert AccessLevel to its label to output to Json
 *
 */
public class AccessLevelSerializer implements JsonSerializer<AccessLevel> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type,
	 * com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(AccessLevel level, Type typeOfT, JsonSerializationContext context) {
		return new JsonPrimitive(level.getLabel());
	}

}
