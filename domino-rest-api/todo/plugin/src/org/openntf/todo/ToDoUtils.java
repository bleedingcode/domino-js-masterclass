/*******************************************************************************
 * Copyright 2018 Paul Withers
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.openntf.todo;

import org.apache.commons.lang3.StringUtils;
import org.openntf.todo.json.AccessLevelDeserializer;
import org.openntf.todo.json.AccessLevelSerializer;
import org.openntf.todo.json.PriorityDeserializer;
import org.openntf.todo.json.PrioritySerializer;
import org.openntf.todo.json.StatusDeserializer;
import org.openntf.todo.json.StatusSerializer;
import org.openntf.todo.json.StoreTypeDeserializer;
import org.openntf.todo.json.StoreTypeSerializer;
import org.openntf.todo.model.DatabaseAccess.AccessLevel;
import org.openntf.todo.model.Store;
import org.openntf.todo.model.Store.StoreType;
import org.openntf.todo.model.ToDo.Priority;
import org.openntf.todo.model.ToDo.Status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Paul Withers
 * 
 *         Standard Util methods for use throughout the REST Service but without any application-platform-specific (e.g.
 *         Domino) code.
 *
 */
public class ToDoUtils {
	public static String DEBUG_PREFIX = "[TODO_APP]: ";
	private static boolean PROFILING = false;

	/**
	 * Prints a message out to the server console, with a standard prefix
	 * 
	 * @param msg
	 *            message to print
	 */
	public static void debugPrint(String msg) {
		System.out.println(DEBUG_PREFIX + msg);
	}

	/**
	 * Turn profiling on or off
	 * 
	 * @return whether or not profiling is enabled
	 */
	public static boolean toggleProfiling() {
		PROFILING = !PROFILING;
		System.out.println(DEBUG_PREFIX + "Profiling enabled: " + PROFILING);
		return PROFILING;
	}

	/**
	 * Used for profiling, to start the stopwatch
	 * 
	 * @return current time as a long
	 */
	public static long startTimer() {
		return System.currentTimeMillis();
	}

	/**
	 * Used for profiling, to stop the stopwatch and print to the server console
	 * 
	 * @param startTimer
	 *            time when the profiling started as a long
	 * @param msg
	 *            message to print
	 */
	public static void debugTimer(long startTimer, String msg) {
		if (PROFILING) {
			long now = System.currentTimeMillis();
			System.out.println(DEBUG_PREFIX + msg + " completed in " + (now - startTimer) + " milliseconds");
		}
	}

	/**
	 * Code to take store path passed in and replace forward slashes and spaces with underscores, plus append ".nsf"
	 * 
	 * @param name
	 *            Store name passed in
	 * @param type
	 *            StoreType (Personal or Team)
	 * @return Valid store name to use to store, all lower case
	 */
	public static String getStoreFilePath(String name, StoreType type) {
		name = StringUtils.replace(StringUtils.replace(name, "/", "_"), " ", "_");
		return StringUtils.lowerCase(Store.TODO_PATH + type.getValue() + "/" + name + ".nsf");
	}

	/**
	 * @return Gson with all relevant Serializers and Deserializers
	 */
	public static Gson getGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat("yyyy-MM-dd");
		builder.registerTypeAdapter(AccessLevel.class, new AccessLevelSerializer());
		builder.registerTypeAdapter(AccessLevel.class, new AccessLevelDeserializer());
		builder.registerTypeAdapter(Priority.class, new PrioritySerializer());
		builder.registerTypeAdapter(Priority.class, new PriorityDeserializer());
		builder.registerTypeAdapter(Status.class, new StatusSerializer());
		builder.registerTypeAdapter(Status.class, new StatusDeserializer());
		builder.registerTypeAdapter(StoreType.class, new StoreTypeSerializer());
		builder.registerTypeAdapter(StoreType.class, new StoreTypeDeserializer());
		return builder.create();
	}

	/**
	 * Method to try to extract the relevant error message from the Exception thrown
	 * 
	 * @param e
	 *            Exception thrown
	 * @return error message or catch-all
	 */
	public static String getErrorMessage(Exception e) {
		if (null == e.getMessage()) {
			if (null != e.getCause()) {
				return e.getCause().getMessage();
			} else {
				return "Unexpected error, see stack trace on server";
			}
		} else {
			return e.getMessage();
		}
	}

}
