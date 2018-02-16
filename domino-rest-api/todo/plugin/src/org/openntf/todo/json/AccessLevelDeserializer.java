package org.openntf.todo.json;

import java.lang.reflect.Type;

import org.openntf.todo.model.ToDo.Status;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class AccessLevelDeserializer implements JsonDeserializer<Status> {

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
