package org.openntf.todo.json;

import org.openntf.todo.ToDoUtils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;

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
		this.gson = ToDoUtils.getGson();
		this.clazz = clazz;
	}

	ExclusionStrategy dbAccessExclusionStrategy = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipField(FieldAttributes fieldAttributes) {
			if ("dbName".equals(fieldAttributes.getName())) {
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
		return gson.toJson(object);
	}

}
