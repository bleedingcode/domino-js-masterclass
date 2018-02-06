package org.openntf.todo.json;

import java.lang.reflect.Type;

import org.openntf.todo.model.ToDo.Priority;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@SuppressWarnings("restriction")
public class PrioritySerializer implements JsonSerializer<Priority> {

	@Override
	public JsonElement serialize(Priority priority, Type typeOfT, JsonSerializationContext context) {
		return new JsonPrimitive(priority.getValue());
	}

}
