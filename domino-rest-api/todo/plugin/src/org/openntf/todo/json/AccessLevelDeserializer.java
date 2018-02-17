package org.openntf.todo.json;

import java.lang.reflect.Type;

import org.openntf.todo.model.DatabaseAccess.AccessLevel;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class AccessLevelDeserializer implements JsonDeserializer<AccessLevel> {

	@Override
	public AccessLevel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		for (AccessLevel level : AccessLevel.values()) {
			if (level.getLabel().equals(json.getAsString())) {
				return level;
			}
		}
		return null;
	}

}
