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

import org.openntf.todo.model.ToDo.Status;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * @author Paul Withers
 * 
 *         Class to convert Json status passed to a Status enum
 *
 */
public class StatusDeserializer implements JsonDeserializer<Status> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type,
	 * com.google.gson.JsonDeserializationContext)
	 */
	@Override
	public Status deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		for (Status status : Status.values()) {
			if (status.getValue().equals(json.getAsString())) {
				return status;
			}
		}
		return null;
	}

}
