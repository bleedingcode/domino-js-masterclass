package org.openntf.todo.json;

import java.lang.reflect.Type;

import org.openntf.todo.model.Store.StoreType;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class StoreTypeDeserializer implements JsonDeserializer<StoreType> {

	@Override
	public StoreType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		for (StoreType storeType : StoreType.values()) {
			if (storeType.getValue().equals(json.getAsString())) {
				return storeType;
			}
		}
		return null;
	}

}
