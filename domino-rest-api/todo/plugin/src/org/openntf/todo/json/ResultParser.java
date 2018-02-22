package org.openntf.todo.json;

import org.openntf.todo.ToDoUtils;

import com.google.gson.Gson;

/**
 * @author Christian Guedemann
 * @since 0.5.0
 *
 * @param <T>
 * 
 *            ResultParser for parsing JSON result into an object
 */
public class ResultParser<T> {

	private final Class<T> clazz;
	private final Gson gson;

	/**
	 * @param clazz
	 *            Class with which to initialise the ResultParser
	 * 
	 * @since 0.5.0
	 */
	public ResultParser(Class<T> clazz) {
		this.clazz = clazz;
		this.gson = ToDoUtils.getGson();
	}

	/**
	 * Converts the JSON string passed into an instance of the relevant class
	 * 
	 * @param jsonString
	 *            String, to convert to an object
	 * @return instance of class {@link #clazz}
	 * 
	 * @since 0.5.0
	 */
	public T parse(String jsonString) {
		return gson.fromJson(jsonString, clazz);
	}

}
