package org.openntf.todo.json;

import java.lang.reflect.Type;

import org.openntf.todo.model.Store.StoreType;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@SuppressWarnings("restriction")
public class StoreTypeSerializer implements JsonSerializer<StoreType> {

	@Override
	public JsonElement serialize(StoreType storeType, Type typeOfT, JsonSerializationContext context) {
		return new JsonPrimitive(storeType.getValue());
	}

}
