package org.openntf.todo.json;

import java.lang.reflect.Type;

import org.openntf.todo.model.DatabaseAccess.AccessLevel;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@SuppressWarnings("restriction")
public class AccessLevelSerializer implements JsonSerializer<AccessLevel> {

	@Override
	public JsonElement serialize(AccessLevel level, Type typeOfT, JsonSerializationContext context) {
		return new JsonPrimitive(level.getLabel());
	}

}
