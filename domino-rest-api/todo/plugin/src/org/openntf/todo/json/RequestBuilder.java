package org.openntf.todo.json;

import org.openntf.todo.model.DatabaseAccess.AccessLevel;
import org.openntf.todo.model.Store.StoreType;
import org.openntf.todo.model.ToDo.Priority;
import org.openntf.todo.model.ToDo.Status;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RequestBuilder<T> {
	private Class<T> clazz;
	private Gson gson;

	/**
	 * @param clazz
	 *            Class with which to initialise the RequestBuilder
	 */
	@SuppressWarnings("restriction")
	public RequestBuilder(Class<T> clazz) {
		super();
		GsonBuilder builder = new GsonBuilder();
		builder.setExclusionStrategies(dbAccessExclusionStrategy);
		builder.registerTypeAdapter(AccessLevel.class, new AccessLevelSerializer());
		builder.registerTypeAdapter(StoreType.class, new StoreTypeSerializer());
		builder.registerTypeAdapter(Priority.class, new PrioritySerializer());
		builder.registerTypeAdapter(Status.class, new StatusSerializer());
		this.gson = builder.create();
		this.clazz = clazz;
	}

	ExclusionStrategy dbAccessExclusionStrategy = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipField(FieldAttributes fieldAttributes) {
			if ("dbName".equals(fieldAttributes.getName())) {
				return true;
			}
			if ("replicaId".equals(fieldAttributes.getName())) {
				return true;
			}
			return false;
		}

		@Override
		public boolean shouldSkipClass(Class aClass) {
			return false;
		}
	};

	/**
	 * Builds JSON string from the object passed
	 * 
	 * @param object
	 *            Object, instance of {@link #clazz}
	 * @return String, JSON conversion of the parameter passed
	 */
	public String buildJson(T object) {
		return gson.toJson(object, clazz);
	}

}
