package org.openntf.todo.json;

import java.lang.reflect.Type;

import org.openntf.todo.model.ToDo.Priority;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class PriorityDeserializer implements JsonDeserializer<Priority> {

	@Override
	public Priority deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		for (Priority priority : Priority.values()) {
			if (priority.getValue().equals(json.getAsString())) {
				return priority;
			}
		}
		return null;
	}

}
