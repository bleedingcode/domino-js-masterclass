package org.openntf.todo.json;

import java.lang.reflect.Type;

import org.openntf.todo.model.ToDo.Status;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@SuppressWarnings("restriction")
public class StatusSerializer implements JsonSerializer<Status> {

	@Override
	public JsonElement serialize(Status status, Type typeOfT, JsonSerializationContext context) {
		return new JsonPrimitive(status.getValue());
	}
}
